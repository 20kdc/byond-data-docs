package after.io;

import java.util.LinkedList;

import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

/**
 * Lists of arrays of ObjectIDs.
 */
public class DMBListTable extends DMBEntryBasedSubblock<int[]> {
	@Override
	public int[] readEntry(DMBReadContext rc) {
		int[] ie = new int[rc.io.getShort() & 0xFFFF];
		rc.ids(ie);
		return ie;
	}
	
	@Override
	public void writeEntry(DMBWriteContext wc, int[] entry) {
		wc.i16(entry.length);
		wc.ids(entry);
	}
}
