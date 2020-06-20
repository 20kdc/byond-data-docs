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
		public DMBValue value = DMBValue.NULL_VALUE;
		@ProcID // Nullable
		public int initializer = OBJ_NULL;
		
		@Override
		public void read(DMBReadContext rc) {
			byte bt = rc.io.get();
			int v = rc.io.getInt();
			value = new DMBValue(bt, v);
			initializer = rc.id();
		}
		
		@Override
		public void write(DMBWriteContext wc) {
			wc.i8(value.type);
			wc.i32(value.value);
			wc.id(initializer);
		}
	}
}
