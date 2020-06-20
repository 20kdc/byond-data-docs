package after.io.framework;

public interface WriteContext {
	void i8(int value);
	
	default void i16(int value) {
		i8(value);
		i8(value >> 8);
	}
	
	default void i32(int value) {
		i16(value);
		i16(value >> 16);
	}

	default void f32(float value) {
		i32(Float.floatToRawIntBits(value));
	}

	default void ints(int[] it) {
		for (int i : it)
			i32(i);
	}
	
	default void floats(float[] it) {
		for (float i : it)
			f32(i);
	}
	
	default void bytes(byte[] data) {
		for (byte b : data)
			i8(b);
	}
}
