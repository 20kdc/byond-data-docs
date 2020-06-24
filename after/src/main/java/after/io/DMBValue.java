package after.io;

/**
 * Yup, it's a microclass. Deal with it.
 */
public final class DMBValue {
	// Constants named with help from dmdism, but not all names match.
	/** no value */
	public static final int NULL = 0;
	/** StringID */
	public static final int STRING = 6;
	/** MobTypeID */
	public static final int MOB_TYPEPATH = 8;
	/** ClassID */
	public static final int OBJ_TYPEPATH = 9;
	/** ClassID */
	public static final int TURF_TYPEPATH = 10;
	/** ClassID */
	public static final int AREA_TYPEPATH = 11;
	/** CacheFileID */
	public static final int RESOURCE = 12;
	/** ClassID */
	public static final int DATUM_TYPEPATH = 32;
	/** (no value?) */
	public static final int SAVEFILE_TYPEPATH = 36;
	/** (no value?) */
	public static final int FILE_TYPEPATH = 39;
	/** (no value?) */
	public static final int LIST_TYPEPATH = 40;
	/** float */
	public static final int NUMBER = 42;
	/** InstanceID */
	public static final int INSTANCE_TYPEPATH = 41;
	/** ClassID */
	public static final int IMAGE_TYPEPATH = 63;

	public static final DMBValue NULL_VALUE = new DMBValue(0, 0);

	public final int type;
	public final int value;
	
	public DMBValue(int t, int v) {
		type = t;
		value = v;
	}
	
	/**
	 * If the DMBValue is a NUMBER, this gets the resulting value.
	 */
	public float getFloatValue() {
		return Float.intBitsToFloat(value);
	}
}
