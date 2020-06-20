package after.io;

import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

public class DMBMapAdditionalData extends DMBObjectEntryBasedSubblock<DMBMapAdditionalData.Entry> {
	@Override
	public Entry make() {
		return new Entry();
	}
	
	@Override
	protected int readCount(DMBReadContext rc) {
		return rc.io.getInt();
	}
	
	@Override
	protected void writeCount(DMBWriteContext wc) {
		wc.i32(entries.size());
	}
	
	public static class Entry implements DMBObjectEntryBasedSubblock.Entry {
		// Offset from last entry
		public short offset;
		// Nullable InstanceID
		public int instance;
		
		@Override
		public void read(DMBReadContext rc) {
			offset = rc.io.getShort();
			instance = rc.id();
		}
		
		@Override
		public void write(DMBWriteContext wc) {
			wc.i16(offset);
			wc.id(instance);
		}
	}
}
