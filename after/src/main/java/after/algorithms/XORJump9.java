package after.algorithms;

public class XORJump9 {
	public static void xorJump9(byte[] data, byte key) {
		for (int i = 0; i < data.length; i++) {
			data[i] ^= key;
			key += 9;
		}
	}
}
