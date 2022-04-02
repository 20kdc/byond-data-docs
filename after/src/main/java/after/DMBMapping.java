package after;

import java.util.HashMap;

import after.annotations.ClassID;
import after.annotations.HasNoMeaning;
import after.annotations.InstanceID;
import after.annotations.MobTypeID;
import after.io.DMB;
import after.io.DMBClassTable;
import after.io.DMBEntryBasedSubblock;
import after.io.DMBInstanceTable;
import after.io.DMBMobTypeTable;
import after.io.DMBProcTable;
import after.io.DMBStringTable;
import after.io.DMBValue;

/**
 * Creates a mapping between two DMB files (a source and a target).
 * Note that instances and strings may be automatically created if necessary.
 */
public class DMBMapping {
	public final DMB dmbSrc, dmbTgt;

	// mapping tables
	//  setup in constructor, never changed after
	private HashMap<Integer, Integer> classMap = new HashMap<>();
	private HashMap<Integer, Integer> mobTypeMap = new HashMap<>();
	//  dynamic
	private HashMap<Integer, Integer> stringMap = new HashMap<>();
	private HashMap<Integer, Integer> instanceMap = new HashMap<>();

	public final int emptyListTgt;

	public DMBMapping(DMB s, DMB t) {
		dmbSrc = s;
		dmbTgt = t;
		int emptyListWork = -1;
		for (int i = 0, iC = dmbTgt.lists.entries.size(); i < iC; i++) {
			if (dmbTgt.lists.entries.get(i).length == 0) {
				emptyListWork = i;
				break;
			}
		}
		if (emptyListWork == -1)
			emptyListWork = dmbTgt.lists.add(new int[0]);
		emptyListTgt = emptyListWork;
		classMap.put(DMBEntryBasedSubblock.OBJ_NULL, DMBEntryBasedSubblock.OBJ_NULL);
		for (int i = 0, iC = dmbSrc.classes.entries.size(); i < iC; i++) {
			DMBClassTable.Entry iV = dmbSrc.classes.entries.get(i);
			if (iV.name == DMBEntryBasedSubblock.OBJ_NULL)
				continue;
			byte[] iVN = dmbSrc.strings.entries.get(iV.name);
			for (int j = 0, jC = dmbTgt.classes.entries.size(); j < jC; j++) {
				DMBClassTable.Entry jV = dmbTgt.classes.entries.get(j);
				byte[] jVN = dmbTgt.strings.entries.get(jV.name);
				if (DMBStringTable.equal(iVN, jVN)) {
					classMap.put(i, j);
					break;
				}
			}
		}
		mobTypeMap.put(DMBEntryBasedSubblock.OBJ_NULL, DMBEntryBasedSubblock.OBJ_NULL);
		for (int i = 0, iC = dmbSrc.mobTypes.entries.size(); i < iC; i++) {
			DMBMobTypeTable.Entry iV = dmbSrc.mobTypes.entries.get(i);
			Integer mappedClass = classMap.get(iV.clazz);
			if (mappedClass == null)
				continue;
			int mappedClassI = mappedClass;
			for (int j = 0, jC = dmbTgt.mobTypes.entries.size(); j < jC; j++) {
				DMBMobTypeTable.Entry jV = dmbTgt.mobTypes.entries.get(j);
				if (mappedClassI == jV.clazz) {
					mobTypeMap.put(i, j);
					break;
				}
			}
		}
		stringMap.put(DMBEntryBasedSubblock.OBJ_NULL, DMBEntryBasedSubblock.OBJ_NULL);
		instanceMap.put(DMBEntryBasedSubblock.OBJ_NULL, DMBEntryBasedSubblock.OBJ_NULL);
	}

	/**
	 * Maps a string ID into the target.
	 * Or creates one if necessary.
	 */
	private int mapString(int i) {
		Integer mapped = stringMap.get(i);
		if (mapped != null)
			return mapped;
		byte[] iV = dmbSrc.strings.entries.get(i);
		for (int j = 0, jC = dmbTgt.strings.entries.size(); j < jC; j++) {
			byte[] jV = dmbTgt.strings.entries.get(j);
			if (DMBStringTable.equal(iV, jV)) {
				stringMap.put(i, j);
				break;
			}
		}
		int r = dmbTgt.strings.add(dmbSrc.strings.entries.get(i));
		stringMap.put(i, r);
		return r;
	}

	/**
	 * Maps an instance ID to the target.
	 * Or creates one if necessary.
	 * Can return Java null if the instance requires a class/mobType that doesn't exist.
	 */
	private Integer mapInstance(int i) {
		Integer mapped = instanceMap.get(i);
		if (mapped != null)
			return mapped;
		// If it has no initializer, then we do things the easy way.
		// If it has an initializer, then things get hard and we might as well just build a new instance.
		DMBInstanceTable.Entry iV = dmbSrc.instances.entries.get(i);
		// No matter what, we have to map this for equality checks / etc.
		DMBValue iVValueMapped = mapValue(iV.value);
		if (iVValueMapped == null)
			return null;
		// There's a possibility this is dreadfully simple. Check for that.
		if (iV.initializer == DMBEntryBasedSubblock.OBJ_NULL) {
			for (int j = 0, jC = dmbTgt.instances.entries.size(); j < jC; j++) {
				DMBInstanceTable.Entry jV = dmbTgt.instances.entries.get(j);
				if (jV.initializer == DMBEntryBasedSubblock.OBJ_NULL) {
					if (iVValueMapped.equals(jV.value)) {
						instanceMap.put(i, j);
						return j;
					}
				}
			}
		}
		// Not so simple, then.
		Integer proc = mapInitializerProc(iV.initializer);
		if (proc == null)
			return null;
		DMBInstanceTable.Entry newInst = new DMBInstanceTable.Entry();
		newInst.value = iVValueMapped;
		newInst.initializer = proc;
		int newInstId = dmbTgt.instances.add(newInst);
		instanceMap.put(i, newInstId);
		return newInstId;
	}

	/**
	 * Maps an initializer proc. Note that this always creates a new proc.
	 * @param i source proc ID
	 * @return target proc ID on success, null on failure
	 */
	private Integer mapInitializerProc(int i) {
		DMBProcTable.Entry iV = dmbSrc.procs.entries.get(i);
		int[] originalCode = dmbSrc.lists.entries.get(iV.code);
		int[] newCode = new int[originalCode.length];
		int oPC = 0;
		int nPC = 0;
		while (oPC < originalCode.length) {
			int op = originalCode[oPC++];
			newCode[nPC++] = op;
			if (op == 0) {
				// END
				break;
			} else if (op == 0x34) {
				// SETVAR
				int tmp;
				tmp = newCode[nPC++] = originalCode[oPC++];
				if (tmp != 65500) {
					System.out.println("incorrect initializer sv[1] " + tmp);
					return null;
				}
				tmp = newCode[nPC++] = originalCode[oPC++];
				if (tmp != 65486) {
					System.out.println("incorrect initializer sv[2] " + tmp);
					return null;
				}
				int field = originalCode[oPC++];
				//System.out.println("initializer dbg: " + dmbSrc.strings.getString(field) + " = ...");
				newCode[nPC++] = mapString(field);
			} else if (op == 0x50) {
				// PUSHI
				newCode[nPC++] = originalCode[oPC++];
			} else if (op == 0x60) {
				// PUSHVAL
				int type = newCode[nPC++] = originalCode[oPC++];
				if (type == DMBValue.NUMBER) {
					newCode[nPC++] = originalCode[oPC++];
					newCode[nPC++] = originalCode[oPC++];
				} else {
					int val = originalCode[oPC++];
					Integer res = mapValue(type, val);
					if (res == null) {
						System.out.println("initializer pushval map failure");
						return null;
					}
					newCode[nPC++] = res;
				}
			} else {
				System.out.println("bad initializer, op " + op);
				return null;
			}
		}
		if (nPC != newCode.length)
			System.out.println("warning: initializer code size different (bad mapping?)");
		DMBProcTable.Entry newProc = new DMBProcTable.Entry();
		newProc.args = emptyListTgt;
		newProc.code = dmbTgt.lists.add(newCode);
		newProc.locals = emptyListTgt;
		newProc.flags = iV.flags;
		return dmbTgt.procs.add(newProc);
	}

	/**
	 * Maps a source value to a target value.
	 * Note that this can return java null (unmappable)
	 */
	public Integer mapValue(int type, int value) {
		switch (type) {
			case DMBValue.NULL:
			case DMBValue.NUMBER:
				return value;
			case DMBValue.STRING:
				return mapString(value);
			case DMBValue.AREA_TYPEPATH:
			case DMBValue.DATUM_TYPEPATH:
			case DMBValue.IMAGE_TYPEPATH:
			case DMBValue.OBJ_TYPEPATH:
			case DMBValue.TURF_TYPEPATH:
				return classMap.get(value);
			case DMBValue.INSTANCE_TYPEPATH:
				return mapInstance(value);
			case DMBValue.MOB_TYPEPATH:
				return mobTypeMap.get(value);
			default:
				System.err.println("DMBMapping cannot map type " + type);
				return null;
		}
	}

	/**
	 * Maps a source value to a target value.
	 * Note that this can return java null (unmappable)
	 */
	public DMBValue mapValue(DMBValue dv) {
		Integer res = mapValue(dv.type, dv.value);
		if (res == null)
			return null;
		return new DMBValue(dv.type, res);
	}
}
