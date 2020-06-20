package after.io;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import aedium.NonNull;
import after.io.framework.DMBContext;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

public class DMBHeader {
	// These are converted in Latin-1 style for lack of any better ideas
	public String shebang = null;

	public int vGEN = 512, vLHS = 512, vRHS = 512;
	public int flags;
	public int exFlags;

	public void read(DMBReadContext rc) {
		StringBuilder lineText = new StringBuilder();
		boolean compatLine = false;
		shebang = "";
		while (true) {
			byte b = rc.io.get();
			if (b == 10) {
				String text = lineText.toString();
				if (!compatLine) {
					if (text.startsWith("#")) {
						shebang += text + "\n";
						// move the base pointer forward to the next line
						// if there's more comment lines, this keeps happening
						rc.basePointer = rc.io.position();
					} else {
						vLHS = vRHS = vGEN = Integer.parseInt(text.split(" v")[1]);
						compatLine = true;
					}
				} else {
					String[] versionBits = text.split(" v")[1].split(" ");
					if (versionBits.length < 2) {
						int v = Integer.parseInt(versionBits[0]);
						vLHS = vRHS = v;
					} else {
						vLHS = Integer.parseInt(versionBits[0]);
						vRHS = Integer.parseInt(versionBits[1]);
					}
					// Done
					break;
				}
				lineText = new StringBuilder();
			} else {
				lineText.append((char) (b & 0xFF));
			}
		}
		
		flags = rc.io.getInt();
		if ((flags & 0x80000000) != 0)
			exFlags = rc.io.getInt();

		updateContext(rc);
		
		if (rc.debug) {
			System.out.println(" version " + vGEN + " " + vLHS + " " + vRHS);
			System.out.println(" large object IDs = " + rc.largeObjectIDs);
			System.out.println(" base pointer = " + rc.basePointer);
		}
	}
	
	public void write(DMBWriteContext wc) {
		if (shebang != null)
			wc.bytes(shebang.getBytes(StandardCharsets.ISO_8859_1));
		wc.basePointer = wc.position;
		wc.bytes(("world bin v" + vGEN).getBytes(StandardCharsets.ISO_8859_1));
		wc.i8(10);
		wc.bytes(("min compatibility v" + vLHS + " " + vRHS).getBytes(StandardCharsets.ISO_8859_1));
		wc.i8(10);
		
		wc.i32(flags);
		if ((flags & 0x80000000) != 0)
			wc.i32(exFlags);
		
		updateContext(wc);
	}

	private void updateContext(DMBContext c) {
		c.vGEN = vGEN;
		c.vLHS = vLHS;
		c.vRHS = vRHS;
		c.largeObjectIDs = (flags & 0x40000000) != 0;
	}
}
