package after.io;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import aedium.NonNull;
import after.io.framework.ReadContext;
import after.io.framework.WriteContext;

/**
 * There shouldn't be anything else, but there COULD be.
 */
public class RSCEntryContent implements RADEntry.Content {
	public static final Supplier<RADEntry.Content> SUPPLIER = () -> {
		return new RSCEntryContent();
	};

	@NonNull
	public CacheID id = CacheID.ZERO;

	public int time = 0;
	public int originalTime = 0;

	@NonNull
	public byte[] name = new byte[0];
	@NonNull
	public byte[] data = new byte[0];
	
	@Override
	public String toString() {
		return id + " " + new String(name, StandardCharsets.UTF_8);
	}
	
	@Override
	public void read(ReadContext rc, int size) {
		byte type = rc.i8();
		int uid = rc.i32();
		id = new CacheID(uid, type);
		time = rc.i32();
		originalTime = rc.i32();
		data = new byte[rc.i32()];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (true) {
			byte b = rc.i8();
			if (b == 0)
				break;
			baos.write(b);
		}
		name = baos.toByteArray();
		// System.out.println("RSC file: " + baos.toString() + "#" + baos.size() + "[" + data.length + "]");
		rc.bytes(data);
	}
	
	@Override
	public void write(WriteContext wc) {
		wc.i8(id.typeOrSomething);
		wc.i32(id.uniqueID);
		wc.i32(time);
		wc.i32(originalTime);
		wc.i32(data.length);
		wc.bytes(name);
		wc.i8(0);
		wc.bytes(data);
	}
}