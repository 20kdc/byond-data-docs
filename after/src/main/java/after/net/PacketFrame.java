package after.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * This is the framing of a packet.
 */
public class PacketFrame {
	/**
	 * 'Sequence number' (?)
	 * (non-handshake client packets only)
	 */
	public short sequence;

	/**
	 * The type of packet.
	 */
	public short type;

	/**
	 * Data. This MUST be an array-backed buffer with offset 0.
	 */
	public ByteBuffer data;
	
	public static PacketFrame read(DataInputStream dis, boolean hasSeq) throws IOException {
		PacketFrame pf = new PacketFrame(); 
		if (hasSeq)
			pf.sequence = dis.readShort();
		pf.type = dis.readShort();
		int len = dis.readUnsignedShort();
		byte[] dat = new byte[len];
		dis.readFully(dat);
		pf.data = ByteBuffer.wrap(dat);
		return pf;
	}
	
	public void write(OutputStream os, boolean hasSeq) throws IOException {
		ByteArrayOutputStream res = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(res);
		if (hasSeq)
			dos.writeShort(sequence);
		dos.writeShort(type);
		byte[] basis = data.array();
		dos.writeShort(basis.length);
		dos.write(basis);
		res.writeTo(os);
	}
}
