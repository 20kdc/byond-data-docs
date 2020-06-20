package after.io;

/**
 * Yup, it's a microclass. Deal with it.
 */
public final class DMBValue {
	// Constants named with help from dmdism, but not all names match.
	public static final int NULL = 0;
	/** StringID */
	public static final int STRING = 6;
	/** MobTypeID */
	public static final int MOB_TYPEPATH = 8;
	/** ClassID */
	public static final int ATOM_MOVABLE_TYPEPATH = 9;
	/** ClassID */
	public static final int ATOM_TYPEPATH = 10;
	/** ClassID */
	public static final int AREA_TYPEPATH = 11;
	/** CacheFileID */
	public static final int RESOURCE = 12;
	/** ClassID */
	public static final int DATUM_TYPEPATH = 32;
	public static final int LIST_TYPEPATH = 40;
	public static final int FLOAT = 42;
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
	 * If the DMBValue is a FLOAT, this gets the resulting value.
	 */
	public float getFloatValue() {
		return Float.intBitsToFloat(value);
	}
}
