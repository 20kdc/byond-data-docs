package after.io;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import aedium.NonNull;
import after.io.framework.ReadContext;
import after.io.framework.WriteContext;

public class RSCEntry {
	public byte type;
	public Content content;
	
	@Override
	public String toString() {
		return content.toString();
	}
	
	public void read(ReadContext rc) {
		byte[] data = new byte[rc.i32()];
		type = rc.i8();
		rc.bytes(data);
		content = new StandardContent();
		content.read(new ReadContext() {
			int ptr = 0;
			@Override
			public byte i8() {
				return data[ptr++];
			}
			
			@Override
			public void bytes(byte[] group) {
				System.arraycopy(data, ptr, group, 0, group.length);
				ptr += group.length;
			}
		});
	}

	public void write(WriteContext wc) {
		ByteArrayOutputStream store = new ByteArrayOutputStream();
		content.write(new WriteContext() {
			@Override
			public void i8(int value) {
				store.write(value);
			}
			
			@Override
			public void bytes(byte[] data) {
				store.write(data, 0, data.length);
			}
		});
		byte[] data = store.toByteArray();
		wc.i32(data.length);
		wc.i8(type);
		wc.bytes(data);
	}
	
	public interface Content {
		void read(ReadContext rc);
		void write(WriteContext wc);
	}
	
	/**
	 * There shouldn't be anything else, but there COULD be.
	 */
	public class StandardContent implements Content {
		@NonNull
		public CacheID id = CacheID.ZERO;
		@NonNull
		public byte[] unknown = new byte[8];
		@NonNull
		public byte[] name = new byte[0];
		@NonNull
		public byte[] data = new byte[0];
		
		@Override
		public String toString() {
			return id + " " + new String(name, StandardCharsets.UTF_8);
		}
		
		@Override
		public void read(ReadContext rc) {
			byte type = rc.i8();
			int uid = rc.i32();
			id = new CacheID(uid, type);
			rc.bytes(unknown);
			data = new byte[rc.i32()];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (true) {
				byte b = rc.i8();
				if (b == 0)
					break;
				baos.write(b);
			}
			name = baos.toByteArray();
			rc.bytes(data);
		}
		
		@Override
		public void write(WriteContext wc) {
			wc.i8(type);
			wc.i32(id.uniqueID);
			wc.i8(id.typeOrSomething);
			wc.bytes(unknown);
			wc.i32(data.length);
			wc.bytes(name);
			wc.i8(0);
			wc.bytes(data);
		}
	}
}
