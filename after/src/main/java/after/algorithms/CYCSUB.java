package after.algorithms;

/**
 * Implementation of CYCSUB.
 * This implementation operates on whole buffers at a time in-place.
 */
public class CYCSUB {
	/**
	 * Decrypts data using CYCSUB.
	 */
	public static void decrypt(byte[] data, int offset, int length, byte[] key, int keyOffset, int keyLength) {
		byte checksum = 0;
		int keyIndex = 0;
		for (int i = length - 1; i >= 0; i--) {
			byte roundKey = (byte) (checksum + key[keyOffset + keyIndex]);
			// decryption: apply checksum after round key (to get decrypted value)
			data[i + offset] -= roundKey;
			checksum += data[i + offset];
			keyIndex = (keyIndex + 1) % keyLength;
		}
	}

	/**
	 * Encrypts data using CYCSUB.
	 */
	public static void encrypt(byte[] data, int offset, int length, byte[] key, int keyOffset, int keyLength) {
		byte checksum = 0;
		int keyIndex = 0;
		for (int i = length - 1; i >= 0; i--) {
			byte roundKey = (byte) (checksum + key[keyOffset + keyIndex]);
			// encryption: apply checksum before round key (to get decrypted value)
			checksum += data[i + offset];
			data[i + offset] += roundKey;
			keyIndex = (keyIndex + 1) % keyLength;
		}
	}
}
