package after.io;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import aedium.NonNull;
import after.io.framework.ReadContext;
import after.io.framework.WriteContext;

/**
 * Random-access datafile entry.
 */
public class RADEntry {
	public Content content;
	
	@Override
	public String toString() {
		return content.toString();
	}
	
	public void read(ReadContext rc, Supplier<Content> validContent) {
		byte[] data = new byte[rc.i32()];
		byte type = rc.i8();
		// System.out.println(data.length + ";" + type);
		rc.bytes(data);
		if (type == 1) {
			content = validContent.get();
		} else {
			content = new InvalidContent();
		}
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
		}, data.length);
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
		wc.i8((content instanceof InvalidContent) ? 0 : 1);
		wc.bytes(data);
	}
	
	public interface Content {
		void read(ReadContext rc, int size);
		void write(WriteContext wc);
	}

	/**
	 * Represents invalid data that may be recoverable or may be completely corrupt.
	 */
	public class InvalidContent implements Content {
		@NonNull
		public byte[] data = new byte[0];
		
		@Override
		public String toString() {
			return "deleted (" + data.length + " bytes)";
		}
		
		@Override
		public void read(ReadContext rc, int size) {
			data = new byte[size];
			rc.bytes(data);
		}
		
		@Override
		public void write(WriteContext wc) {
			wc.bytes(data);
		}
	}
}
