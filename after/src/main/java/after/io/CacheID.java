package after.io;

/**
 * Represents a Cache ID as used in both the DMB and RSC file formats.
 */
public final class CacheID {
	public static final CacheID ZERO = new CacheID(0, (byte) 0);
	
	public final int uniqueID;
	public final byte typeOrSomething;
	
	public CacheID(int uid, byte type) {
		uniqueID = uid;
		typeOrSomething = type;
	}
	
	@Override
	public String toString() {
		return "(" + typeOrSomething + ", " + uniqueID + ")";
	}
	
	@Override
	public int hashCode() {
		return uniqueID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CacheID))
			return false;
		CacheID cid = ((CacheID) obj);
		return (cid.typeOrSomething == typeOrSomething) && (cid.uniqueID == uniqueID);
	}
}
