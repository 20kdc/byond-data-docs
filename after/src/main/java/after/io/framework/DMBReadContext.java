package after.io.framework;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Read context.
 */
public class DMBReadContext extends DMBContext implements ReadContext {
	public ByteBuffer io;

	public DMBReadContext(byte[] data, boolean dbg) {
		super(dbg);
		
		io = ByteBuffer.wrap(data);
		io.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	@Override
	public int position() {
		return io.position();
	}
	
	public int remaining() {
		return io.capacity() - position();
	}
	
	@Override
	public byte i8() {
		return io.get();
	}
	
	public int id() {
		if (largeObjectIDs)
			return i32();
		return i16() & 0xFFFF;
	}
	
	public void ids(int[] group) {
		for (int i = 0; i < group.length; i++)
			group[i] = id();
	}
}
