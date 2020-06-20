package after;

import after.io.DMB;
import after.io.DMBClassTable;
import after.io.DMBInstanceTable;
import after.io.DMBMobTypeTable;

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
	
	private static void addInstance(DMB dmb, int clz, int unk) {
		DMBInstanceTable.Entry ent = new DMBInstanceTable.Entry();
		ent.baseType = (byte) unk;
		ent.clazz = clz;
		dmb.instances.add(ent);
	}
	
	public static DMB newEden() {
		DMB dmb = new DMB();
		// I suspect this is relied upon, don't want to jinx it
		dmb.strings.entries.add(new byte[0]);

		int datum = addClass(dmb, "/datum", OBJ_NULL);
		int atom = addClass(dmb, "/atom", datum);
		int atomMovable = addClass(dmb, "/atom/movable", atom);
		int mob = addClass(dmb, "/mob", atomMovable);
		dmb.standardObjectIDs.image = addClass(dmb, "/image", datum);
		dmb.standardObjectIDs.turf = addClass(dmb, "/turf", atom);
		dmb.standardObjectIDs.area = addClass(dmb, "/area", atom);
		dmb.standardObjectIDs.client = addClass(dmb, "/client", OBJ_NULL);

		DMBMobTypeTable.Entry mobType = new DMBMobTypeTable.Entry();
		mobType.clazz = mob;
		dmb.mobTypes.add(mobType);
		dmb.standardObjectIDs.mob = 0;
		
		dmb.fixStrings();
		return dmb;
	}
}
