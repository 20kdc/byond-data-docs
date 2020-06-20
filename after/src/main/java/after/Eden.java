package after;

import after.io.DMB;
import after.io.DMBClassTable;
import after.io.DMBInstanceTable;
import after.io.DMBMobTypeTable;
import after.io.DMBValue;

import static after.io.DMBObjectEntryBasedSubblock.OBJ_NULL;

import after.annotations.MobTypeID;

/**
 * Current state of the BYOND-less DMB project ('Eden').
 */
public class Eden {
	private static int addClass(DMB dmb, String name, int parent) {
		DMBClassTable.Entry datum = new DMBClassTable.Entry();
		datum.name = dmb.strings.of(name);
		datum.parent = parent;
		return dmb.classes.add(datum);
	}
	
	private static DMBClassTable.Entry cls(DMB dmb, int id) {
		return dmb.classes.entries.get(id);
	}
	
	public static DMB newEden() {
		DMB dmb = new DMB();
		// I suspect this is relied upon, don't want to jinx it
		dmb.strings.entries.add(new byte[0]);

		int mob = addClass(dmb, "", OBJ_NULL);
		dmb.standardObjectIDs.client = mob;

		DMBMobTypeTable.Entry mobType = new DMBMobTypeTable.Entry();
		mobType.clazz = mob;
		dmb.mobTypes.add(mobType);
		dmb.standardObjectIDs.mob = 0;
		
		dmb.fixStrings();
		return dmb;
	}
}
