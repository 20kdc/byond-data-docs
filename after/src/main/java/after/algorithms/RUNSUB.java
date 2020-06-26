package after.algorithms;

/**
 * Implementation of RUNSUB.
 * This implementation operates on whole buffers at a time in-place.
 */
public class RUNSUB {
	/**
	 * Decrypts data using RUNSUB.
	 * The last byte is checked against the checksum but otherwise ignored.
	 * If the decryption fails, RUNSUBFailedException is thrown, but the data is put in the buffer.
	 * 
	 * @throws RUNSUBFailedException On checksum failure
	 */
	public static void decrypt(byte[] data, int offset, int length, int key) {
		byte checksum = 0;
		for (int i = 0; i < length - 1; i++) {
			byte roundKey = (byte) (checksum + (key >> (checksum % 32)));
			// decryption: apply checksum after round key (to get decrypted value)
			data[i + offset] -= roundKey;
			checksum += data[i + offset];
		}
		if (checksum != data[(length - 1) + offset])
			throw new RUNSUBFailedException(data[(length - 1) + offset], checksum);
	}

	/**
	 * Encrypts data using RUNSUB.
	 * The last byte is overwritten with the checksum.
	 */
	public static void encrypt(byte[] data, int offset, int length, int key) {
		byte checksum = 0;
		for (int i = 0; i < length - 1; i++) {
			byte roundKey = (byte) (checksum + (key >> (checksum % 32)));
			// encryption: apply checksum before round key (to get decrypted value)
			checksum += data[i + offset];
			data[i + offset] += roundKey;
		}
		data[(length - 1) + offset] = checksum;
	}
}
