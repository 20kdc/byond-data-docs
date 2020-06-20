package after.io;

import java.io.PrintStream;

import after.annotations.ClassID;
import after.annotations.StringID;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

public class DMBMobTypeTable extends DMBObjectEntryBasedSubblock<DMBMobTypeTable.Entry> {
	@Override
	public Entry make() {
		return new Entry();
	}

	public static class Entry implements DMBObjectEntryBasedSubblock.Entry {
		@ClassID
		public int clazz;
		@StringID // Nullable
		public int key;
		public byte sightFlags;
		public int sightFlagsEx;
		public byte seeInDark;
		public byte seeInvisible;
		
		@Override
		public void read(DMBReadContext rc) {
			clazz = rc.id();
			key = rc.id();
			sightFlags = rc.io.get();
			if (sightFlags < 0) {
				sightFlagsEx = rc.io.getInt();
				seeInDark = rc.io.get();
				seeInvisible = rc.io.get();
			}
		}
		
		@Override
		public void write(DMBWriteContext wc) {
			wc.id(clazz);
			wc.id(key);
			wc.i8(sightFlags);
			if (sightFlags < 0) {
				wc.i32(sightFlagsEx);
				wc.i8(seeInDark);
				wc.i8(seeInvisible);
			}
		}
	}
}
