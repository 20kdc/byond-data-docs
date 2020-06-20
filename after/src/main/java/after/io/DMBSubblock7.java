package after.io;

import java.io.PrintStream;
import java.util.LinkedList;

import aedium.ElementMarkers;
import aedium.NonNull;
import after.annotations.ClassID;
import after.annotations.ProcID;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

public class DMBSubblock7 {
	// list of ProcIDs
	@ElementMarkers({ProcID.class})
	@NonNull
	public LinkedList<Integer> entries = new LinkedList<>();

	public void print(PrintStream ps, DMB base) {
		ps.println("# sb7");
		int index = 0;
		for (Integer ent : entries) {
			ps.println(index + ": " + ent);
			index++;
		}
	}

	public void read(DMBReadContext rc) {
		int count = rc.id();
		for (int i = 0; i < count; i++)
			entries.add(rc.id());
	}

	public void write(DMBWriteContext wc) {
		wc.id(entries.size());
		for (int i : entries)
			wc.id(i);
	}
}
