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
	public static void decrypt(byte[] data, int key) {
		byte checksum = 0;
		for (int i = 0; i < data.length - 1; i++) {
			byte roundKey = (byte) (checksum + (key >> (checksum % 32)));
			// decryption: apply checksum after round key (to get decrypted value)
			data[i] -= roundKey;
			checksum += data[i];
		}
		if (checksum != data[data.length - 1])
			throw new RUNSUBFailedException(data[data.length - 1], checksum);
	}

	/**
	 * Encrypts data using RUNSUB.
	 * The last byte is overwritten with the checksum.
	 */
	public static void encrypt(byte[] data, int key) {
		byte checksum = 0;
		for (int i = 0; i < data.length - 1; i++) {
			byte roundKey = (byte) (checksum + (key >> (checksum % 32)));
			// encryption: apply checksum before round key (to get decrypted value)
			checksum += data[i];
			data[i] += roundKey;
		}
		data[data.length - 1] = checksum;
	}
}
