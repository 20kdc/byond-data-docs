package after.io;

import java.util.LinkedList;

import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

/**
 * This implements the basic 'list of X' logic. This is NOT exclusive with having other properties.
 */
public abstract class DMBEntryBasedSubblock<X> {
	public static int OBJ_NULL = 0xFFFF;

	/**
	 * The list of entries. This is a linked list for easier manipulation.
	 */
	public LinkedList<X> entries = new LinkedList<>();

	public DMBEntryBasedSubblock() {
		super();
	}
	
	protected abstract X createDummyValue();
	
	/**
	 * Indicates if a given index is reserved.
	 */
	public boolean indexReserved(int index) {
		return index == OBJ_NULL;
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
	
	public int add(X value) {
		int res = entries.size();
		while (indexReserved(res)) {
			entries.add(createDummyValue());
			res++;
		}
		entries.add(value);
		return res;
	}
	
	public abstract X readEntry(DMBReadContext rc);
	public abstract void writeEntry(DMBWriteContext wc, X entry);
}