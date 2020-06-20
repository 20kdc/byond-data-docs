package after.io;

import java.io.PrintStream;

import after.annotations.CacheFileID;
import after.annotations.ClassID;
import after.annotations.ListID;
import after.annotations.ListOfProcID;
import after.annotations.ProcID;
import after.annotations.StringID;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

public class DMBClassTable extends DMBObjectEntryBasedSubblock<DMBClassTable.Entry> {
	
	@Override
	public Entry make() {
		return new Entry();
	}
	
	public static class Entry implements DMBObjectEntryBasedSubblock.Entry {
		@StringID
		public int name;
		@ClassID // Nullable
		public int parent;
		@StringID // Nullable
		public int objName;
		@StringID // Nullable
		public int description;
		@CacheFileID // Nullable
		public int icon;
		@StringID // Nullable
		public int iconState;
		
		// need to figure this out too
		public byte direction;
		
		// >= 307
		
		public boolean dmSpecialTypeLong;
		public int dmSpecialType;
		
		// --
		
		@StringID // maybe nullable
		public int text;
		
		// >= 494
		
		public int idH;
		public short unkB;
		public short unkC;
		
		// >= 508
		
		public short unkD;
		public short unkE;
		
		// --
		
		@StringID // Nullable
		public int suffix;
		// need to figure this out
		public int flags;
		@ListOfProcID // Nullable (not the elements)
		public int verbTable;
		@ListOfProcID // Nullable (not the elements)
		public int procTable;
		@ProcID // Nullable
		public int initializer;
		@ListID
		public int initializedVarsTable;
		@ListID // Nullable, (complex key/value pairs thing, see docs)
		public int varTable;
		
		// >= 267
		
		public float layer;
		
		// >= 500
		
		public byte hasFloats;
		public float[] floats = new float[6];
		
		// >= 509
		
		public byte hasEvenMoreFloats;
		public float[] evenMoreFloats = new float[20];
		
		// >= 306
		
		@ListID // Nullable, complex key/value pairs thing
		public int overridingVarList;
		
		@Override
		public void read(DMBReadContext rc) {
			name = rc.id();
			parent = rc.id();
			objName = rc.id();
			description = rc.id();
			icon = rc.id();
			iconState = rc.id();
			direction = rc.io.get();
			
			if (rc.vGEN >= 307) {
				dmSpecialType = rc.io.get();
				dmSpecialTypeLong = dmSpecialType == 0x0F;
				if (dmSpecialTypeLong)
					dmSpecialType = rc.io.getInt();
			} else {
				dmSpecialType = 1;
			}
			
			text = rc.id();
			
			if (rc.vRHS >= 494) {
				idH = rc.id();
				unkB = rc.io.getShort();
				unkC = rc.io.getShort();
			}
			
			if (rc.vRHS >= 508) {
				unkD = rc.io.getShort();
				unkE = rc.io.getShort();
			}
			
			suffix = rc.id();
			if (rc.vGEN >= 306) {
				flags = rc.io.getInt();
			} else {
				flags = rc.io.get() & 0xFF;
			}
			verbTable = rc.id();
			procTable = rc.id();
			initializer = rc.id();
			initializedVarsTable = rc.id();
			varTable = rc.id();
			
			if (rc.vGEN >= 267)
				layer = rc.io.getFloat();
			
			if (rc.vRHS >= 500) {
				hasFloats = rc.io.get();
				if (hasFloats != 0)
					rc.floats(floats);
			}
			
			if (rc.vRHS >= 509) {
				hasEvenMoreFloats = rc.io.get();
				if (hasEvenMoreFloats != 0)
					rc.floats(evenMoreFloats);
			}
			
			if (rc.vGEN >= 306)
				overridingVarList = rc.id();
		}
		
		@Override
		public void write(DMBWriteContext wc) {
			wc.id(name);
			wc.id(parent);
			wc.id(objName);
			wc.id(description);
			wc.id(icon);
			wc.id(iconState);
			wc.i8(direction);
			
			if (wc.vGEN >= 307) {
				if (dmSpecialTypeLong) {
					wc.i8(0x0F);
					wc.i32(dmSpecialType);
				} else {
					wc.i8(dmSpecialType);
				}
			}
			
			wc.id(text);
			
			if (wc.vRHS >= 494) {
				wc.id(idH);
				wc.i16(unkB);
				wc.i16(unkC);
			}
			
			if (wc.vRHS >= 508) {
				wc.i16(unkD);
				wc.i16(unkE);
			}
			
			wc.id(suffix);
			if (wc.vGEN >= 306) {
				wc.i32(flags);
			} else {
				wc.i8(flags);
			}
			wc.id(verbTable);
			wc.id(procTable);
			wc.id(initializer);
			wc.id(initializedVarsTable);
			wc.id(varTable);
			if (wc.vGEN >= 267)
				wc.i32(Float.floatToIntBits(layer));
			
			if (wc.vRHS >= 500) {
				wc.i8(hasFloats);
				if (hasFloats != 0)
					wc.floats(floats);
			}
			
			if (wc.vRHS >= 509) {
				wc.i8(hasEvenMoreFloats);
				if (hasEvenMoreFloats != 0)
					wc.floats(evenMoreFloats);
			}
			
			if (wc.vGEN >= 306)
				wc.id(overridingVarList);
		}
	}
}
