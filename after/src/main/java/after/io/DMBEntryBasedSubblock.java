package after.io;

import java.util.LinkedList;

import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

/**
 * This implements the basic 'list of X' logic. This is NOT exclusive with having other properties.
 */
public abstract class DMBEntryBasedSubblock<X> {

	/**
	 * The list of entries. This is a linked list for easier manipulation.
	 */
	public LinkedList<X> entries = new LinkedList<>();

	public DMBEntryBasedSubblock() {
		super();
	}

	protected int readCount(DMBReadContext rc) {
		return rc.id();
	}
	
	public void read(DMBReadContext rc) {
		int count = readCount(rc);
		for (int i = 0; i < count; i++)
			entries.add(readEntry(rc));
	}
	
	protected void writeCount(DMBWriteContext wc) {
		wc.id(entries.size());
	}
	
	public void write(DMBWriteContext wc) {
		writeCount(wc);
		for (X entry : entries)
			writeEntry(wc, entry);
	}
	
	public abstract X readEntry(DMBReadContext rc);
	public abstract void writeEntry(DMBWriteContext wc, X entry);
}