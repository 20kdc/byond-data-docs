package after.io;

import java.io.PrintStream;

import aedium.ElementMarkers;
import aedium.NonNull;
import after.annotations.InstanceID;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

/**
 * This represents the '3D' map.
 */
public class DMBGrid {
	public int xSize = 1, ySize = 1, zSize = 1;
	// Array of nullable (0xFFFF) InstanceIDs
	@ElementMarkers({InstanceID.class})
	@NonNull
	public int[] turf = new int[0];
	// Array of nullable (0xFFFF) InstanceIDs
	@ElementMarkers({InstanceID.class})
	@NonNull
	public int[] area = new int[0];
	// Array of nullable (0xFFFF) ListIDs of ObjectIDs
	@NonNull
	public int[] additionalTurfs = new int[0];
	
	public void read(DMBReadContext rc) {
		xSize = rc.io.getShort() & 0xFFFF;
		ySize = rc.io.getShort() & 0xFFFF;
		zSize = rc.io.getShort() & 0xFFFF;
		int total = xSize * ySize * zSize; 
		turf = new int[total];
		area = new int[total];
		additionalTurfs = new int[total];
		
		int i = 0;
		while (i < total) {
			turf[i] = rc.id();
			area[i] = rc.id();
			additionalTurfs[i] = rc.id();
			int dup = rc.io.get() & 0xFF;
			for (int j = 0; j < dup; j++) {
				turf[i + j] = turf[i];
				area[i + j] = area[i];
				additionalTurfs[i + j] = additionalTurfs[i];
			}
			i += dup;
		}
	}
	
	public void write(DMBWriteContext wc) {
		wc.i16(xSize);
		wc.i16(ySize);
		wc.i16(zSize);
		int total = xSize * ySize * zSize;
		int rleStart = -1;
		for (int i = 0; i < total; i++) {
			boolean reset = false;
			// reset conditions {
			// ensure that an RLE length can never exceed 255
			if (i - rleStart == 255)
				reset = true;
			// detect if an RLE change occurred
			if (rleStart != -1) {
				if (turf[i] != turf[rleStart])
					reset = true;
				else if (area[i] != area[rleStart])
					reset = true;
				else if (additionalTurfs[i] != additionalTurfs[rleStart])
					reset = true;
			}
			// actually apply resets
			if (reset) {
				wc.i8(i - rleStart);
				rleStart = -1;
			}
			// if not extending (this doesn't necessarily mean a reset happened, for first tile), write & start
			if (rleStart == -1) {
				wc.id(turf[i]);
				wc.id(area[i]);
				wc.id(additionalTurfs[i]);
				rleStart = i;
			}
		}
		if (rleStart != -1)
			wc.i8(total - rleStart);
	}
}
