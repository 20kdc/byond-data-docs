package after.io;

import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

/**
 * References to resources.
 */
public class DMBCacheFileTable extends DMBEntryBasedSubblock<CacheID> {
	@Override
	public CacheID readEntry(DMBReadContext rc) {
		int uniqueID = rc.i32();
		byte typeOrSomething = rc.i8();
		return new CacheID(uniqueID, typeOrSomething);
	}
	
	@Override
	public void writeEntry(DMBWriteContext wc, CacheID entry) {
		wc.i32(entry.uniqueID);
		wc.i8(entry.typeOrSomething);
	}
}
