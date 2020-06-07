# NQCRC

NQCRC is an MSB-first CRC-32 with 0xAF as the polynomial.

The way NQCRC is written implies that the initial value of the hash and what happens to "finalize it" is situation-dependent.

The following documentation exists because just saying all this is not how you write proper format documentation.

NQCRC, as with most CRC-32 implementations, uses a table of 256 32-bit values, one for each possible byte.

The per-byte NQCRC algorithm is `uint32_t hash = (hash << 8) ^ nqcrcTable[(hash >>> 24) ^ (byt & 0xFF)];`.

This functionally matches the description of an msbit-first CRC-32 in: https://en.wikipedia.org/wiki/Computation_of_cyclic_redundancy_checks#Multi-bit_computation

Following with the theme, the table computation is the same.

```java
	private static final int[] nqcrcTable = new int[256];
	static {
		for (int i = 0; i < nqcrcTable.length; i++) {
			int value = i << 0x18;
			for (int j = 0; j < 8; j++)
				value = (value << 1) ^ (value < 0 ? 0xAF : 0);
			nqcrcTable[i] = value;
		}
	}
```

The result is the NQCRC value for that index.

