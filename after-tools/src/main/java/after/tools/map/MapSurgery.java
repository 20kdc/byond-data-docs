package after.tools.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import after.DMBMapping;
import after.io.DMB;
import after.io.DMBEntryBasedSubblock;
import after.io.DMBInstanceTable;
import after.io.DMBMapAdditionalData;
import after.io.DMBValue;

public class MapSurgery {
	/**
	 * Imports a map from another DMB file, deleting the existing grid.
	 * @param dmbG the place to inject the map
	 * @param dmbM the map to inject
	 */
	public static void run(DMB dmbG, DMB dmbM) {
		// reset grid (though don't fill in arrays yet)
		int total = dmbM.grid.turf.length;
		dmbG.grid.turf = new int[total];
		dmbG.grid.area = new int[total];
		dmbG.grid.additionalTurfs = new int[total];
		dmbG.grid.xSize = dmbM.grid.xSize;
		dmbG.grid.ySize = dmbM.grid.ySize;
		dmbG.grid.zSize = dmbM.grid.zSize;
		// prepare fallback instance
		int fallbackTurfInstance = getOrMakeTurfInstance(dmbG);
		DMBMapping mapping = new DMBMapping(dmbM, dmbG);
		HashMap<ArrayList<Integer>, Integer> lists = new HashMap<ArrayList<Integer>, Integer>();
		// fill in grids
		for (int i = 0; i < total; i++) {
			// set sane defaults
			dmbG.grid.turf[i] = fallbackTurfInstance;
			dmbG.grid.area[i] = DMBEntryBasedSubblock.OBJ_NULL;
			dmbG.grid.additionalTurfs[i] = DMBEntryBasedSubblock.OBJ_NULL;
			// apply stuff
			Integer cTurf = mapping.mapValue(DMBValue.INSTANCE_TYPEPATH, dmbM.grid.turf[i]);
			if (cTurf != null) {
				dmbG.grid.turf[i] = cTurf;
			} else {
				System.out.println("unable to map turf @ " + i);
			}
			Integer cArea = mapping.mapValue(DMBValue.INSTANCE_TYPEPATH, dmbM.grid.area[i]);
			if (cArea != null) {
				dmbG.grid.area[i] = cArea;
			} else {
				System.out.println("unable to map area @ " + i);
			}
			if (dmbM.grid.additionalTurfs[i] != DMBEntryBasedSubblock.OBJ_NULL) {
				int[] addTurfsO = dmbM.lists.entries.get(dmbM.grid.additionalTurfs[i]);
				ArrayList<Integer> addTurfsN = new ArrayList<>(addTurfsO.length);
				for (int x = 0; x < addTurfsO.length; x++) {
					Integer v = mapping.mapValue(DMBValue.TURF_TYPEPATH, addTurfsO[x]);
					if (v != null)
						addTurfsN.add(v);
				}
				Integer existingList = lists.get(addTurfsN);
				if (existingList == null) {
					int[] addTurfsNA = new int[addTurfsN.size()];
					for (int x = 0; x < addTurfsNA.length; x++)
						addTurfsNA[x] = addTurfsN.get(x);
					int res = dmbM.lists.add(addTurfsNA);
					lists.put(addTurfsN, res);
					existingList = res;
				}
				dmbG.grid.additionalTurfs[i] = existingList;
			}
		}
		dmbG.mapAdditionalData.entries.clear();
		for (DMBMapAdditionalData.Entry ent : dmbM.mapAdditionalData.entries) {
			DMBMapAdditionalData.Entry oh = new DMBMapAdditionalData.Entry();
			oh.offset = ent.offset;
			Integer mapped = mapping.mapValue(DMBValue.INSTANCE_TYPEPATH, oh.instance);
			if (mapped != null)
				oh.instance = mapped;
			dmbG.mapAdditionalData.entries.add(ent);
		}
		dmbG.fixStrings();
	}

	private static int getOrMakeTurfInstance(DMB dmbG) {
		int target = dmbG.standardObjectIDs.turf;
		for (int i = 0, iC = dmbG.instances.entries.size(); i < iC; i++) {
			DMBInstanceTable.Entry ent = dmbG.instances.entries.get(i);
			if ((ent.value.type == DMBValue.TURF_TYPEPATH) && (ent.value.value == target) && (ent.initializer == DMBEntryBasedSubblock.OBJ_NULL))
				return i;
		}
		DMBInstanceTable.Entry ent = new DMBInstanceTable.Entry();
		ent.value = new DMBValue(DMBValue.TURF_TYPEPATH, target);
		ent.initializer = DMBEntryBasedSubblock.OBJ_NULL;
		return dmbG.instances.add(ent);
	}
}
