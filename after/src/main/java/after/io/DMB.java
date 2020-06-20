package after.io;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import aedium.NonNull;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

/**
 * This is the root, the DMB file as a whole.
 */
public class DMB {

	@NonNull
	public DMBHeader header = new DMBHeader();
	@NonNull
	public DMBGrid grid = new DMBGrid();

	public int totalSizeOfAllStrings;
	
	@NonNull
	public DMBClassTable classes = new DMBClassTable();
	@NonNull
	public DMBMobTypeTable mobTypes = new DMBMobTypeTable();
	@NonNull
	public DMBStringTable strings = new DMBStringTable();
	@NonNull
	public DMBListTable lists = new DMBListTable();
	@NonNull
	public DMBProcTable procs = new DMBProcTable();
	@NonNull
	public DMBVarTable vars = new DMBVarTable();
	@NonNull
	public DMBSubblock7 sb7 = new DMBSubblock7();
	@NonNull
	public DMBInstanceTable instances = new DMBInstanceTable();
	@NonNull
	public DMBMapAdditionalData mapAdditionalData = new DMBMapAdditionalData();
	@NonNull
	public DMBStandardObjectIDs standardObjectIDs = new DMBStandardObjectIDs();
	@NonNull
	public DMBCacheFileTable cacheFiles = new DMBCacheFileTable();
	
	public void read(DMBReadContext rc) {
		// Sub-Block -1 (Header)
		rc.section("header");
		header.read(rc);
		
		// Sub-Block 0 (The Grid)
		rc.section("grid");
		grid.read(rc);
		
		// Interlude
		rc.section("totalSizeOfAllStrings");
		totalSizeOfAllStrings = rc.i32();
		if (rc.debug)
			System.out.println(" = " + totalSizeOfAllStrings);
		
		// Classes Table
		rc.section("classes");
		classes.read(rc);
		if (rc.debug)
			System.out.println(" = " + classes.entries.size());
		
		// Mob Type Table
		rc.section("mobTypes");
		mobTypes.read(rc);
		if (rc.debug)
			System.out.println(" = " + mobTypes.entries.size());
		
		// String Table
		rc.section("strings");
		strings.read(rc);

		// List Table
		rc.section("lists");
		lists.read(rc);
		
		// Proc Table
		rc.section("procs");
		procs.read(rc);
		
		// Var Table
		rc.section("vars");
		vars.read(rc);
		
		// Sub-Block 7
		rc.section("sb7");
		sb7.read(rc);
		
		// Instance Table
		rc.section("instances");
		instances.read(rc);

		// Map Additional Data
		rc.section("mapAdditionalData");
		mapAdditionalData.read(rc);

		// Map Additional Data
		rc.section("standardObjectIDs");
		standardObjectIDs.read(rc);

		rc.section("cacheFiles");
		cacheFiles.read(rc);
		
		// -- other unfinished --
		rc.section("unfinished");
		int unfinished = rc.remaining();
		if (unfinished != 0)
			if (rc.debug)
				System.err.println("warning: ignored 0x" + Integer.toHexString(unfinished));
	}
	
	public void write(DMBWriteContext wc) {
		wc.section("header");
		header.write(wc);

		wc.section("grid");
		grid.write(wc);
		
		wc.section("totalSizeOfAllStrings");
		wc.i32(totalSizeOfAllStrings);

		wc.section("classes");
		classes.write(wc);

		wc.section("mobTypes");
		mobTypes.write(wc);
		
		wc.section("strings");
		strings.write(wc);
		
		wc.section("lists");
		lists.write(wc);

		wc.section("procs");
		procs.write(wc);
		
		wc.section("vars");
		vars.write(wc);
		
		wc.section("sb7");
		sb7.write(wc);
		
		wc.section("instances");
		instances.write(wc);

		wc.section("mapAdditionalData");
		mapAdditionalData.write(wc);

		wc.section("standardObjectIDs");
		standardObjectIDs.write(wc);

		wc.section("cacheFiles");
		cacheFiles.write(wc);
	}

	/**
	 * Fixes the string table stuff after it's been twiddled with
	 */
	public void fixStrings() {
		strings.hash = strings.calculateHash();
		totalSizeOfAllStrings = strings.calculateTotalSize();
	}
}
