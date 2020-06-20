package after.io.framework;

public interface ReadContext {
	byte i8();
	
	default short i16() {
		int a = i8() & 0xFF;
		return (short) (((i8() & 0xFF) << 8) | a);
	}
	
	default int i32() {
		int a = i16() & 0xFFFF;
		return (((i16() & 0xFFFF) << 16) | a);
	}
	
	default float f32() {
		return Float.intBitsToFloat(i32());
	}

	default void ints(int[] group) {
		for (int i = 0; i < group.length; i++)
			group[i] = i32();
	}
	
	default void floats(float[] group) {
		for (int i = 0; i < group.length; i++)
			group[i] = f32();
	}

	default void bytes(byte[] group) {
		for (int i = 0; i < group.length; i++)
			group[i] = i8();
	}

}
