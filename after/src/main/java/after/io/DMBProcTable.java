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
		public int name;
		// --
		@StringID // Nullable
		public int displayName;
		public int[] idBA = new int[2];
		public byte unkA;
		public byte unkB;
		public byte unkC;
		public int unkCx;
		public byte unkCy;
		// ListID (list containing DM bytecode)
		@ListID
		public int code;
		@ListOfVarID
		public int locals;
		@ListID
		public int args;
		
		@Override
		public void read(DMBReadContext rc) {
			if (rc.vGEN >= 224 || rc.largeObjectIDs)
				name = rc.id();
			displayName = rc.id();
			rc.ids(idBA);
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
				wc.id(name);
			wc.id(displayName);
			wc.ids(idBA);
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
