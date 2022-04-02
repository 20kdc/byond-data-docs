package after.io;

import after.annotations.CacheFileID;
import after.annotations.ClassID;
import after.annotations.ListOfProcID;
import after.annotations.MobTypeID;
import after.annotations.ProcID;
import after.annotations.StringID;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;
import static after.io.DMBObjectEntryBasedSubblock.OBJ_NULL;

import aedium.ElementMarkers;

public class DMBStandardObjectIDs {
	@MobTypeID // Nullable
	public int mob = OBJ_NULL;
	// Nullable ClassID
	@ClassID // Nullable
	public int turf = OBJ_NULL;
	@ClassID // Nullable
	public int area = OBJ_NULL;
	@ListOfProcID
	public int procs = OBJ_NULL;
	@ProcID // Nullable
	public int globalVariableInitializer = OBJ_NULL;
	@StringID // Nullable
	public int domain = OBJ_NULL;
	@StringID
	public int name = OBJ_NULL;
	// GEN < 368 (yes, under)
	public int idOld1 = OBJ_NULL;
	// --
	public int tickTimeMillis = 100;
	@ClassID
	public int client = OBJ_NULL;
	// GEN >= 308
	@ClassID
	public int image = OBJ_NULL;
	// --	
	public byte clientLazyEye;
	public byte clientDir = 1;
	// GEN >= 415
	public short clientControlFreak;
	// --
	public byte unkE;
	// GEN >= 230
	@StringID // Nullable
	public int clientScript = OBJ_NULL;
	// --
	// GEN >= 507
	@ElementMarkers(CacheFileID.class)
	public int[] clientScriptFiles = new int[0];
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
	public int hubNumber;
	public int gameVersion;
	// GEN >= 272
	public short cacheLifespan = 30;
	@StringID // Nullable
	public int clientCommandText;
	@StringID // Nullable
	public int clientCommandPrompt;
	// GEN >= 276
	@StringID // Nullable
	public int hub = OBJ_NULL;
	// GEN >= 305
	@StringID // Nullable (?) but really should be "default"
	public int channel = OBJ_NULL;
	// GEN >= 360
	@CacheFileID // Nullable
	public int skin = OBJ_NULL;
	
	// LHS >= 455
	public short iconSizeX = 32;
	public short iconSizeY = 32;
	public short mapFormat = (short) 32768;
	
	public void read(DMBReadContext rc) {
		mob = rc.id();
		turf = rc.id();
		area = rc.id();
		procs = rc.id();
		globalVariableInitializer = rc.id();
		domain = rc.id();
		name = rc.id();
		if (rc.vGEN < 368)
			idOld1 = rc.id();
		tickTimeMillis = rc.io.getInt();
		client = rc.id();
		if (rc.vGEN >= 308)
			image = rc.id();
		clientLazyEye = rc.io.get();
		clientDir = rc.io.get();
		if (rc.vGEN >= 415)
			clientControlFreak = rc.io.getShort();
		unkE = rc.io.get();
		if (rc.vGEN >= 230)
			clientScript = rc.id();
		if (rc.vGEN >= 507) {
			clientScriptFiles = new int[rc.io.getShort() & 0xFFFF];
			rc.ids(clientScriptFiles);
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
			hubNumber = rc.io.getInt();
			gameVersion = rc.io.getInt();
		}
		if (rc.vGEN >= 272) {
			cacheLifespan = rc.io.getShort();
			clientCommandText = rc.id();
			clientCommandPrompt = rc.id();
		}
		if (rc.vGEN >= 276)
			hub = rc.id();
		if (rc.vGEN >= 305)
			channel = rc.id();
		if (rc.vGEN >= 360)
			skin = rc.id();
		
		if (rc.vLHS >= 455) {
			iconSizeX = rc.io.getShort();
			iconSizeY = rc.io.getShort();
			mapFormat = rc.io.getShort();
		} else {
			iconSizeX = 32;
			iconSizeY = 32;
			mapFormat = (short) 32768;
		}
	}
	
	public void write(DMBWriteContext wc) {
		wc.id(mob);
		wc.id(turf);
		wc.id(area);
		wc.id(procs);
		wc.id(globalVariableInitializer);
		wc.id(domain);
		wc.id(name);
		if (wc.vGEN < 368)
			wc.id(idOld1);
		wc.i32(tickTimeMillis);
		wc.id(client);
		if (wc.vGEN >= 308)
			wc.id(image);
		wc.i8(clientLazyEye);
		wc.i8(clientDir);
		if (wc.vGEN >= 415)
			wc.i16(clientControlFreak);
		wc.i8(unkE);
		if (wc.vGEN >= 230)
			wc.id(clientScript);
		if (wc.vGEN >= 507) {
			wc.i16(clientScriptFiles.length);
			wc.ids(clientScriptFiles);
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
			wc.i32(hubNumber);
			wc.i32(gameVersion);
		}
		if (wc.vGEN >= 266) {
			wc.i16(cacheLifespan);
			wc.id(clientCommandText);
			wc.id(clientCommandPrompt);
		}
		if (wc.vGEN >= 276)
			wc.id(hub);
		if (wc.vGEN >= 305)
			wc.id(channel);
		if (wc.vGEN >= 360)
			wc.id(skin);
		if (wc.vLHS >= 455) {
			wc.i16(iconSizeX);
			wc.i16(iconSizeY);
			wc.i16(mapFormat);
		}
	}
}
