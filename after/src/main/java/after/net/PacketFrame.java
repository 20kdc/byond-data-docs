package after.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This is the framing of a packet.
 * This doesn't handle encryption, on purpose.
 * It's also not thread-safe, even for 'thread-safe-looking' operations.
 */
public class PacketFrame {
	/**
	 * If this packet frame has a sequence number.
	 * (non-handshake client packets only)
	 */
	public boolean hasSeq;
	
	/**
	 * Sequence number.
	 */
	public short sequence;

	/**
	 * The type of packet.
	 */
	public short type;

	/**
	 * The data. The position in the buffer indicates the length of the data.
	 */
	public final ByteBuffer data = ByteBuffer.allocate(0xFFFF);
	
	/**
	 * The backing array of the data buffer.
	 */
	public final byte[] dataArray = data.array();
	
	/**
	 * Array in which a written packet is constructed.
	 * (There's reasons for this, really...)
	 */
	private final byte[] writeDataArray = new byte[0x10005];
	
	public PacketFrame() {
		data.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public void read(DataInputStream dis) throws IOException {
		if (hasSeq)
			sequence = dis.readShort();
		type = dis.readShort();
		int len = dis.readUnsignedShort();
		dis.readFully(dataArray, 0, len);
		data.position(len);
	}
	
	public void write(OutputStream dos) throws IOException {
		int ptr = 0;
		// This is another one of those "read-sensitive" applications.
		// In practice this means shenanigans.
		if (hasSeq) {
			writeDataArray[ptr++] = (byte) (sequence >> 8);
			writeDataArray[ptr++] = (byte) sequence;
		}
		writeDataArray[ptr++] = (byte) (type >> 8);
		writeDataArray[ptr++] = (byte) type;
		int len = data.position();
		writeDataArray[ptr++] = (byte) (len >> 8);
		writeDataArray[ptr++] = (byte) len;
		System.arraycopy(dataArray, 0, writeDataArray, ptr, len);
		ptr += len;
		// And now commit.
		dos.write(writeDataArray, 0, ptr);
	}
	
	public void writeWebSocket(DataOutputStream dos) throws IOException {
		writeWebSocketInternal(dos, type, dataArray, 0, data.position());
	}
	
	public static void writeWebSocketInternal(DataOutputStream dos, short type, byte[] data, int offset, int length) throws IOException {
		// FIN, Binary
		dos.write(0x82);
		int pktLen = length + 2;
		if (pktLen < 126) {
			dos.write(pktLen);
		} else if (pktLen < 65536) {
			dos.write(126);
			dos.writeShort(pktLen);
		} else  {
			dos.write(0x7F);
			dos.writeLong(length + 2);
		}
		dos.writeShort(type);
		dos.write(data, offset, length);
	}
	
	public void begin(int t) {
		data.position(0);
		type = (short) t;
	}
}
