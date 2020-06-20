package after.io;

import java.io.PrintStream;

import after.annotations.StringID;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

public class DMBVarTable extends DMBObjectEntryBasedSubblock<DMBVarTable.Entry> {
	// GEN & LHS >= 512, ListID
	public int unk;
	
	@Override
	public Entry make() {
		return new Entry();
	}

	@Override
	public void read(DMBReadContext rc) {
		super.read(rc);
		if (rc.vGEN >= 512 && rc.vLHS >= 512)
			unk = rc.io.getInt();
	}
	
	@Override
	public void write(DMBWriteContext wc) {
		super.write(wc);
		if (wc.vGEN >= 512 && wc.vLHS >= 512)
			wc.i32(unk);
	}
	
	public static class Entry implements DMBObjectEntryBasedSubblock.Entry {
		public DMBValue value = DMBValue.NULL_VALUE;
		@StringID
		public int name;
		
		@Override
		public void read(DMBReadContext rc) {
			int t = rc.io.get();
			int v = rc.io.getInt();
			value = new DMBValue(t, v);
			name = rc.id();
		}
		
		@Override
		public void write(DMBWriteContext wc) {
			wc.i8(value.type);
			wc.i32(value.value);
			wc.id(name);
		}
	}
}
