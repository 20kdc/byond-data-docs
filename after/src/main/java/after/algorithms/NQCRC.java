package after.algorithms;

/**
 * Implementation of NQCRC.
 */
public class NQCRC {
	private static final int[] nqcrcTable = new int[256];
	static {
		for (int i = 0; i < nqcrcTable.length; i++) {
			int value = i << 0x18;
			for (int j = 0; j < 8; j++)
				value = (value << 1) ^ (value < 0 ? 0xAF : 0);
			nqcrcTable[i] = value;
		}
	}
	
	/**
	 * Advances NQCRC by a single byte.
	 */
	public static int hash(int hash, byte byt) {
		return (hash << 8) ^ nqcrcTable[(hash >>> 24) ^ (byt & 0xFF)];
	}

	/**
	 * Advances NQCRC by an array of bytes.
	 */
	public static int hash(int hash, byte[] data) {
		for (byte b : data)
			hash = hash(hash, b);
		return hash;
	}
}
