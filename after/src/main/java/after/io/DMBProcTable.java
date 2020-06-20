package after.io;

import java.io.PrintStream;

import after.annotations.ListID;
import after.annotations.ListOfVarID;
import after.annotations.StringID;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

public class DMBProcTable extends DMBObjectEntryBasedSubblock<DMBProcTable.Entry> {
	@Override
	public Entry make() {
		return new Entry();
	}

	public static class Entry implements DMBObjectEntryBasedSubblock.Entry {
		// GEN >= 224 | LO
		@StringID // Nullable
		public int path = OBJ_NULL;
		// --
		@StringID // Nullable
		public int name = OBJ_NULL;
		public int idBAA = OBJ_NULL;
		@StringID // Nullable
		public int verbCategory = OBJ_NULL;
		public byte unkA = -1;
		public byte unkB;
		public byte unkC;
		public int unkCx;
		public byte unkCy;
		// ListID (list containing DM bytecode)
		@ListID
		public int code = OBJ_NULL; // fill these with NULL so that the 0 doesn't get used by mistake
		@ListOfVarID
		public int locals = OBJ_NULL;
		@ListID
		public int args = OBJ_NULL;
		
		@Override
		public void read(DMBReadContext rc) {
			if (rc.vGEN >= 224 || rc.largeObjectIDs)
				path = rc.id();
			name = rc.id();
			idBAA = rc.id();
			verbCategory = rc.id();
			unkA = rc.io.get();
			unkB = rc.io.get();
			unkC = rc.io.get();
			if (unkC < 0) {
				unkCx = rc.io.getInt();
				unkCy = rc.io.get();
			}
			code = rc.id();
			locals = rc.id();
			args = rc.id();
		}
		
		@Override
		public void write(DMBWriteContext wc) {
			if (wc.vGEN >= 224 || wc.largeObjectIDs)
				wc.id(path);
			wc.id(name);
			wc.id(idBAA);
			wc.id(verbCategory);
			wc.i8(unkA);
			wc.i8(unkB);
			wc.i8(unkC);
			if (unkC < 0) {
				wc.i32(unkCx);
				wc.i8(unkCy);
			}
			wc.id(code);
			wc.id(locals);
			wc.id(args);
		}
	}
}
