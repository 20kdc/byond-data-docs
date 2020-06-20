package after.io;

import java.io.PrintStream;

import after.annotations.ClassID;
import after.annotations.ProcID;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

public class DMBInstanceTable extends DMBObjectEntryBasedSubblock<DMBInstanceTable.Entry> {
	@Override
	public Entry make() {
		return new Entry();
	}

	public static class Entry implements DMBObjectEntryBasedSubblock.Entry {
		public byte unkA;
		// ClassID, but not written as an ID
		@ClassID
		public int clazz;
		@ProcID // Nullable
		public int initializer;
		
		@Override
		public void read(DMBReadContext rc) {
			unkA = rc.io.get();
			clazz = rc.io.getInt();
			initializer = rc.id();
		}
		
		@Override
		public void write(DMBWriteContext wc) {
			wc.i8(unkA);
			wc.i32(clazz);
			wc.id(initializer);
		}
	}
}
