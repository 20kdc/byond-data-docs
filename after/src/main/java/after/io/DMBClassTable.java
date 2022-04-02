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
		public static final int CF_MOB = 2;
		public static final int CF_ATOM = 4;
		public static final int CF_AREA = 8;
		
		@StringID
		public int name;
		@ClassID // Nullable
		public int parent = OBJ_NULL;
		@StringID // Nullable
		public int objName = OBJ_NULL;
		@StringID // Nullable
		public int description = OBJ_NULL;
		@CacheFileID // Nullable
		public int icon = OBJ_NULL;
		@StringID // Nullable
		public int iconState = OBJ_NULL;
		
		// need to figure this out too
		public byte direction = 2;
		
		// >= 307
		
		public boolean dmSpecialTypeLong;
		public int dmSpecialType = 1;
		
		// --
		
		@StringID // maybe nullable
		public int text = OBJ_NULL;
		
		// >= 494
		
		@StringID // Nullable
		public int maptext;
		public short maptextWidth;
		public short maptextHeight;
		
		// >= 508
		
		public short maptextX;
		public short maptextY;
		
		// --
		
		@StringID // Nullable
		public int suffix = OBJ_NULL;
		// see CF_* constants
		public int flags;
		@ListOfProcID // Nullable (not the elements)
		public int verbTable = OBJ_NULL;
		@ListOfProcID // Nullable (not the elements)
		public int procTable = OBJ_NULL;
		@ProcID // Nullable
		public int initializer = OBJ_NULL;
		@ListID // Marked Nullable because honestly it probably is, but unsure
		public int initializedVarsTable = OBJ_NULL;
		@ListID // Nullable, (complex key/value pairs thing, see docs)
		public int varTable = OBJ_NULL;
		
		// >= 267
		
		public float layer;
		
		// >= 500
		
		public byte hasTransform;
		public float[] transform = new float[6];
		
		// >= 509
		
		public byte hasColourMatrix;
		public float[] colourMatrix = new float[20];
		
		// >= 306
		
		@ListID // Nullable, complex key/value pairs thing
		public int overridingVarList = OBJ_NULL;
		
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
				maptext = rc.id();
				maptextWidth = rc.io.getShort();
				maptextHeight = rc.io.getShort();
			}
			
			if (rc.vRHS >= 508) {
				maptextX = rc.io.getShort();
				maptextY = rc.io.getShort();
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
				hasTransform = rc.io.get();
				if (hasTransform != 0)
					rc.floats(transform);
			}
			
			if (rc.vRHS >= 509) {
				hasColourMatrix = rc.io.get();
				if (hasColourMatrix != 0)
					rc.floats(colourMatrix);
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
				wc.id(maptext);
				wc.i16(maptextWidth);
				wc.i16(maptextHeight);
			}
			
			if (wc.vRHS >= 508) {
				wc.i16(maptextX);
				wc.i16(maptextY);
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
				wc.i8(hasTransform);
				if (hasTransform != 0)
					wc.floats(transform);
			}
			
			if (wc.vRHS >= 509) {
				wc.i8(hasColourMatrix);
				if (hasColourMatrix != 0)
					wc.floats(colourMatrix);
			}
			
			if (wc.vGEN >= 306)
				wc.id(overridingVarList);
		}
	}
}
