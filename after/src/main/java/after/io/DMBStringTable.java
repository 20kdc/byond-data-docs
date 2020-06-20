package after.io;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import after.algorithms.NQCRC;
import after.algorithms.XORJump9;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

/**
 * The DM string table.
 */
public class DMBStringTable extends DMBEntryBasedSubblock<byte[]> {
	
	/**
	 * The NQCRC hash of the string table. Use calculateHash to get the correct value for this. 
	 */
	public int hash = -1;
	
	/**
	 * Gets (doesn't apply) the correct hash value.
	 */
	public int calculateHash() {
		int value = -1;
		for (byte[] entry : entries) {
			for (byte b : entry)
				value = NQCRC.hash(value, b);
			value = NQCRC.hash(value, (byte) 0);
		}
		return value;
	}

	/**
	 * Gets (doesn't apply) the total size of all strings (you'll need this)
	 */
	public int calculateTotalSize() {
		int value = 0;
		for (byte[] entry : entries)
			value += entry.length + 1;
		return value;
	}
	
	@Override
	public void read(DMBReadContext rc) {
		super.read(rc);
		if (rc.vGEN >= 468)
			hash = rc.io.getInt();
	}
	
	@Override
	public byte[] readEntry(DMBReadContext rc) {
		int totalLength = 0;
		while (true) {
			short val = (short) (rc.io.position() - rc.basePointer);
			val ^= rc.io.getShort();
			totalLength += val & 0xFFFF;
			if (val != (short) -1)
				break;
		}
		byte[] data = new byte[totalLength];
		byte key = (byte) (rc.io.position() - rc.basePointer);
		rc.io.get(data);
		XORJump9.xorJump9(data, key);
		return data;
	}
	
	@Override
	public void write(DMBWriteContext wc) {
		super.write(wc);
		if (wc.vGEN >= 468)
			wc.i32(hash);
	}
	
	@Override
	public void writeEntry(DMBWriteContext wc, byte[] str) {
		int remainingLength = str.length;
		while (remainingLength >= 0xFFFF) {
			wc.i16((wc.position - wc.basePointer) ^ 0xFFFF);
			remainingLength -= 0xFFFF;
		}
		wc.i16((wc.position - wc.basePointer) ^ remainingLength);
		
		byte[] data = new byte[str.length];
		System.arraycopy(str, 0, data, 0, str.length);
		byte key = (byte) (wc.position - wc.basePointer);
		XORJump9.xorJump9(data, key);
		wc.bytes(data);
	}
	
	// -- Utilities --
	
	/**
	 * Deliberately ignores Unicode for lossless conversion.
	 */
	public static String fromWTF(byte[] dat) {
		return new String(dat, StandardCharsets.ISO_8859_1);
	}
	
	/**
	 * Gets a string by the "internal" value.
	 * Uses ISO\_8859_1 to guarantee lossless conversion.
	 */
	public String getString(int i) {
		return fromWTF(entries.get(i));
	}
	
	/**
	 * Sets a string by the "internal" value.
	 * Potentially not lossless if you were to put Unicode into it.
	 * Otherwise, uses ISO\_8859_1 to guarantee lossless conversion.
	 */
	public void setString(int i, String val) {
		entries.set(i, val.getBytes(StandardCharsets.ISO_8859_1));
	}
}
