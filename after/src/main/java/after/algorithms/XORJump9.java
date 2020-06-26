package after.algorithms;

public class XORJump9 {
	/**
	 * Runs XORJUMP9 on some data.
	 * @return The modified key. 
	 */
	public static byte xorJump9(byte[] data, byte key) {
		for (int i = 0; i < data.length; i++) {
			data[i] ^= key;
			key += 9;
		}
		return key;
	}
}
