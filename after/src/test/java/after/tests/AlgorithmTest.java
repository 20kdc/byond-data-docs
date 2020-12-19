package after.tests;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import after.algorithms.CYCSUB;
import after.algorithms.NQCRC;
import after.algorithms.RUNSUB;
import after.algorithms.XORJump9;

/**
 * Acts as a set of test vectors for various algorithms.
 */
public class AlgorithmTest {
	@Test
	public void testNQCRC() {
		assert NQCRC.hash(0, "The quick brown fox jumps over the lazy dog.\n".getBytes(StandardCharsets.UTF_8)) == 0x93fe309a;
		assert NQCRC.hash(0, "３\n".getBytes(StandardCharsets.UTF_8)) == 0xda08bd33;
	}

	@Test
	public void testXORJUMP9() {
		byte[] bloop = new byte[] {(byte) 0x00, (byte) 0x80, (byte) 0x40};
		assert XORJump9.xorJump9(bloop, (byte) 0x40) == 0x5B;
		assert bloop[0] == 0x40;
		assert bloop[1] == (byte) 0xC9;
		assert bloop[2] == 0x12;
	}
	
	@Test
	public void testRUNSUB() {
		byte[] data = new byte[] {(byte) 0xf8, 0x38, 0x78, (byte) 0xb8, (byte) 0xc2, 0x3b, 0x0a};
		byte[] dataCopy = new byte[data.length];
		System.arraycopy(data, 0, dataCopy, 0, data.length);
		
		int key = 0xa374c5b8;
		
		// Decrypt
		RUNSUB.decrypt(data, 0, data.length, key);
		assert data[0] == '@';
		assert data[1] == '@';
		assert data[2] == '@';
		assert data[3] == '@';
		assert data[4] == '\n';
		assert data[5] == 0;

		// Encrypt again and compare to the original
		RUNSUB.encrypt(data, 0, data.length, key);
		for (int i = 0; i < data.length; i++)
			assert data[i] == dataCopy[i];
	}
	
	@Test
	public void testCYCSUB() {
		byte[] data = new byte[] {0x01, 0x02, 0x03, 0x04, 0x05};
		byte[] key = new byte[] {0x10, 0x20, 0x1F};
		byte[] dataCopy = new byte[data.length];
		System.arraycopy(data, 0, dataCopy, 0, data.length);

		// Encrypt
		CYCSUB.encrypt(data, 0, data.length, key, 0, key.length);

		// Decrypt again and compare to the original
		CYCSUB.decrypt(data, 0, data.length, key, 0, key.length);
		for (int i = 0; i < data.length; i++)
			assert data[i] == dataCopy[i];
	}
}
