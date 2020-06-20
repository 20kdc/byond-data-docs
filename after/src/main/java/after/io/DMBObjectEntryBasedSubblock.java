package after.io;

import java.util.LinkedList;

import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

public abstract class DMBObjectEntryBasedSubblock<X extends DMBObjectEntryBasedSubblock.Entry> extends DMBEntryBasedSubblock<X> {
	public static int OBJ_NULL = 0xFFFF;
	
	@Override
	public X readEntry(DMBReadContext rc) {
		X entry = make();
		entry.read(rc);
		return entry;
	}
	
	@Override
	public void writeEntry(DMBWriteContext rc, X entry) {
		entry.write(rc);
	}
	
	public abstract X make();
	
	public static interface Entry {
		void read(DMBReadContext rc);
		void write(DMBWriteContext wc);
	}
}
