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
		@StringID // Nullable
		public int desc = OBJ_NULL;
		@StringID // Nullable
		public int verbCategory = OBJ_NULL;
		public byte verbSrcParam = -1;
		public byte verbSrcKind;
		public byte flags;
		public int flagsExx;
		public byte flagsExy;
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
			desc = rc.id();
			verbCategory = rc.id();
			verbSrcParam = rc.io.get();
			verbSrcKind = rc.io.get();
			flags = rc.io.get();
			if (flags < 0) {
				flagsExx = rc.io.getInt();
				flagsExy = rc.io.get();
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
			wc.id(desc);
			wc.id(verbCategory);
			wc.i8(verbSrcParam);
			wc.i8(verbSrcKind);
			wc.i8(flags);
			if (flags < 0) {
				wc.i32(flagsExx);
				wc.i8(flagsExy);
			}
			wc.id(code);
			wc.id(locals);
			wc.id(args);
		}
	}
}
