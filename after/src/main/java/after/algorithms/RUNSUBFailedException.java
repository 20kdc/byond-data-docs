package after.algorithms;

/**
 * Indicates that a checksum failure happened during RUNSUB decryption.
 * 
 * This is a RuntimeException on the same basis that NumberFormatException is a RuntimeException,
 *  i.e. that it would be obstructive and annoying to make this a normal Exception.
 */
public class RUNSUBFailedException extends RuntimeException {
	/**
	 * The expected checksum: The one written in the file.
	 */
	public final byte expected;
	
	/**
	 * The resulting checksum: The actual result.
	 */
	public final byte result;
	
	public RUNSUBFailedException(byte e, byte g) {
		expected = e;
		result = g;
	}
	
	@Override
	public String getMessage() {
		return "RUNSUB decryption failed - stated checksum " + expected + ", got " + result;
	}
}
