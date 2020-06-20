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
		public static final byte BT_DATUM = 8;
		public static final byte BT_ATOM_MOVABLE = 9;
		public static final byte BT_ATOM = 10;
		public static final byte BT_AREA = 11;
		public static final byte BT_IMAGE = 63;
		
		// This is the base type
		public byte baseType = BT_DATUM;
		// ClassID, but not written as an ID
		@ClassID
		public int clazz = OBJ_NULL; // even though this isn't nullable, default it to OBJ_NULL for debug reasons
		@ProcID // Nullable
		public int initializer = OBJ_NULL;
		
		@Override
		public void read(DMBReadContext rc) {
			baseType = rc.io.get();
			clazz = rc.io.getInt();
			initializer = rc.id();
		}
		
		@Override
		public void write(DMBWriteContext wc) {
			wc.i8(baseType);
			wc.i32(clazz);
			wc.id(initializer);
		}
	}
}
