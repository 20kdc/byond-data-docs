package after.tests;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import after.algorithms.NQCRC;

/**
 * Acts as a set of test vectors for various algorithms.
 */
public class AlgorithmTest {
	@Test
	public void testNQCRC() {
		assert NQCRC.hash(0, "The quick brown fox jumps over the lazy dog.\n".getBytes(StandardCharsets.UTF_8)) == 0x93fe309a;
		assert NQCRC.hash(0, "３\n".getBytes(StandardCharsets.UTF_8)) == 0xda08bd33;
	}
}
