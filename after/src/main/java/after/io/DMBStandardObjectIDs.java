package after.io;

import after.annotations.ClassID;
import after.annotations.MobTypeID;
import after.annotations.StringID;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;
import static after.io.DMBObjectEntryBasedSubblock.OBJ_NULL;

public class DMBStandardObjectIDs {
	@MobTypeID // Nullable
	public int mob = OBJ_NULL;
	// Nullable ClassID
	@ClassID // Nullable
	public int turf = OBJ_NULL;
	@ClassID // Nullable
	public int area = OBJ_NULL;
	public int[] idBA = new int[] {OBJ_NULL, OBJ_NULL, OBJ_NULL};
	@StringID
	public int name = OBJ_NULL;
	// GEN < 368 (yes, under)
	public int idOld1 = OBJ_NULL;
	// --
	public int unkA = 100;
	@ClassID
	public int client = OBJ_NULL;
	// GEN >= 308
	@ClassID
	public int image = OBJ_NULL;
	// --	
	public byte unkB;
	public byte unkC = 1;
	// GEN >= 415
	public short unkD;
	// --
	public byte unkE;
	// GEN >= 230
	public int idC = OBJ_NULL;
	// --
	// GEN >= 507
	public int[] idEvenMore = new int[0];
	// GEN < 507 (yes, under)
	public int idX = OBJ_NULL;
	// GEN >= 232
	public short unkF = 2827;
	// GEN >= 235 && GEN < 368
	public short unkPX;
	// GEN >= 236 && GEN < 368
	public short unkPY;
	// GEN >= 341
	@StringID // Nullable
	public int hubPasswordHashed = OBJ_NULL;
	// GEN >= 266
	@StringID // Nullable
	public int serverName = OBJ_NULL;
	public int[] unkBG = new int[2];
	// GEN >= 272
	public short unkG = 30;
	public int[] idAY = new int[] {OBJ_NULL, OBJ_NULL};
	// GEN >= 276
	@StringID // Nullable
	public int hub = OBJ_NULL;
	// GEN >= 305
	public int idAZA = OBJ_NULL;
	// GEN >= 360
	public int idAZB = OBJ_NULL;
	
	// LHS >= 455
	public short iconSizeX = 32;
	public short iconSizeY = 32;
	public short unkZ = (short) 32768;
	
	public void read(DMBReadContext rc) {
		mob = rc.id();
		turf = rc.id();
		area = rc.id();
		rc.ids(idBA);
		name = rc.id();
		if (rc.vGEN < 368)
			idOld1 = rc.id();
		unkA = rc.io.getInt();
		client = rc.id();
		if (rc.vGEN >= 308)
			image = rc.id();
		unkB = rc.io.get();
		unkC = rc.io.get();
		if (rc.vGEN >= 415)
			unkD = rc.io.getShort();
		unkE = rc.io.get();
		if (rc.vGEN >= 230)
			idC = rc.id();
		if (rc.vGEN >= 507) {
			idEvenMore = new int[rc.io.getShort() & 0xFFFF];
			rc.ids(idEvenMore);
		}
		if (rc.vGEN < 507) {
			idX = rc.id();
		}
		if (rc.vGEN >= 232)
			unkF = rc.io.getShort();
		if (rc.vGEN >= 235 && rc.vGEN < 368)
			unkPX = rc.io.getShort();
		if (rc.vGEN >= 236 && rc.vGEN < 368)
			unkPY = rc.io.getShort();
		if (rc.vGEN >= 341)
			hubPasswordHashed = rc.id();
		if (rc.vGEN >= 266) {
			serverName = rc.id();
			rc.ints(unkBG);
		}
		if (rc.vGEN >= 272) {
			unkG = rc.io.getShort();
			rc.ids(idAY);
		}
		if (rc.vGEN >= 276)
			hub = rc.id();
		if (rc.vGEN >= 305)
			idAZA = rc.id();
		if (rc.vGEN >= 360)
			idAZB = rc.id();
		
		if (rc.vLHS >= 455) {
			iconSizeX = rc.io.getShort();
			iconSizeY = rc.io.getShort();
			unkZ = rc.io.getShort();
		} else {
			iconSizeX = 32;
			iconSizeY = 32;
			unkZ = (short) 32768;
		}
	}
	
	public void write(DMBWriteContext wc) {
		wc.id(mob);
		wc.id(turf);
		wc.id(area);
		wc.ids(idBA);
		wc.id(name);
		if (wc.vGEN < 368)
			wc.id(idOld1);
		wc.i32(unkA);
		wc.id(client);
		if (wc.vGEN >= 308)
			wc.id(image);
		wc.i8(unkB);
		wc.i8(unkC);
		if (wc.vGEN >= 415)
			wc.i16(unkD);
		wc.i8(unkE);
		if (wc.vGEN >= 230)
			wc.id(idC);
		if (wc.vGEN >= 507) {
			wc.i16(idEvenMore.length);
			wc.ids(idEvenMore);
		}
		if (wc.vGEN < 507)
			wc.id(idX);
		if (wc.vGEN >= 232)
			wc.i16(unkF);
		if (wc.vGEN >= 235 && wc.vGEN < 368)
			wc.i16(unkPX);
		if (wc.vGEN >= 236 && wc.vGEN < 368)
			wc.i16(unkPY);
		if (wc.vGEN >= 341)
			wc.id(hubPasswordHashed);
		if (wc.vGEN >= 266) {
			wc.id(serverName);
			wc.ints(unkBG);
		}
		if (wc.vGEN >= 266) {
			wc.i16(unkG);
			wc.ids(idAY);
		}
		if (wc.vGEN >= 276)
			wc.id(hub);
		if (wc.vGEN >= 305)
			wc.id(idAZA);
		if (wc.vGEN >= 360)
			wc.id(idAZB);
		if (wc.vLHS >= 455) {
			wc.i16(iconSizeX);
			wc.i16(iconSizeY);
			wc.i16(unkZ);
		}
	}
}
