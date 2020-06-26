package after.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import after.algorithms.RUNSUB;

/**
 * Testing utility for studying the protocol.
 */
public class WorkbenchMain {
	public static void main(String[] args) throws Exception {
		System.out.println("Listening on port 5100. Expects target server on localhost port 5101.");
		ServerSocketChannel ss = ServerSocketChannel.open();
		ss.bind(new InetSocketAddress(5100));

		SocketChannel sockC = ss.accept();
		SocketChannel sockS = SocketChannel.open(new InetSocketAddress("localhost", 5101));
		
		Selector main = Selector.open();

		DataInputStream disC = new DataInputStream(sockC.socket().getInputStream());
		DataInputStream disS = new DataInputStream(sockS.socket().getInputStream());
		
		PacketFrame pfC = new PacketFrame();
		PacketFrame pfS = new PacketFrame();
		
		DataOutputStream dosC = new DataOutputStream(sockC.socket().getOutputStream());
		DataOutputStream dosS = new DataOutputStream(sockS.socket().getOutputStream());
		
		// various states
		int NEST_NORMAL = 0;
		int NEST_CTOS_HANDSHAKE = 1;
		int NEST_STOC_HANDSHAKE = 2;
		int state = NEST_CTOS_HANDSHAKE;

		int encryptionKey = 0;

		while (true) {
			// blocking off...
			sockC.configureBlocking(false);
			sockS.configureBlocking(false);

			SelectionKey keyC = sockC.register(main, SelectionKey.OP_READ);
			SelectionKey keyS = sockS.register(main, SelectionKey.OP_READ);

			main.select();
			Set<SelectionKey> active = main.selectedKeys();

			boolean hitC = active.contains(keyC);
			boolean hitS = active.contains(keyS);
			
			keyC.cancel();
			keyS.cancel();
			
			main.selectNow();
			
			sockC.configureBlocking(true);
			sockS.configureBlocking(true);

			// ...back to blocking for simpler logic (this is just a workbench)

			if (hitC) {
				pfC.read(disC);
				pfC.write(dosS);
				logAndDecrypt("C", pfC, encryptionKey);
				if (state == NEST_CTOS_HANDSHAKE && pfC.type == 1) {
					// initial handshake
					pfC.hasSeq = true;
					state = NEST_STOC_HANDSHAKE;
					encryptionKey = pfC.data.getInt(8);
					encryptionKey += pfC.data.getInt(0) + (pfC.data.getInt(4) * 0x10000);
				} else if (state != NEST_NORMAL) {
					System.out.println("*** ctos " + pfC.type + " is odd");
				}
			}
			if (hitS) {
				pfS.read(disS);
				pfS.write(dosC);
				logAndDecrypt("S", pfS, encryptionKey);
				if (state == NEST_STOC_HANDSHAKE && pfS.type == 1) {
					// skip past padding
					int ptr = 11;
					while (true) {
						int v = pfS.data.getInt(ptr);
						ptr += 4;
						v += 0x71bd632f;
						v &= 0x04008000;
						if (v == 0)
							break;
					}
					encryptionKey += pfS.data.getInt(ptr);
					// right, done
					state = NEST_NORMAL;
				} else if (state != NEST_NORMAL) {
					System.out.println("*** stoc " + pfS.type + " is odd");
				}
			}
		}
	}
	
	private static void logAndDecrypt(String where, PacketFrame pf, int key) {
		System.out.println(where + " " + Integer.toHexString(pf.sequence & 0xFFFF) + " " + Integer.toHexString(pf.type & 0xFFFF));
		int len = pf.data.position();
		for (int pass = 0; pass < 2; pass++) {
			for (int i = 0; i < len; i++) {
				String hx = Integer.toHexString(pf.dataArray[i] & 0xFF);
				if (hx.length() == 1)
					hx = "0" + hx;
				System.out.print(" " + hx);
			}
			System.out.println();
			if (pass == 0 && key != 0) {
				System.out.println("decrypted");
				RUNSUB.decrypt(pf.dataArray, 0, len, key);
				len--;
			} else {
				// final pass: ASCII
				for (int i = 0; i < len; i++) {
					char c = (char) (pf.dataArray[i] & 0xFF);
					if (c >= 32 && c != 127) {
						System.out.print(" " + c + " ");
					} else {
						System.out.print(" . ");
					}
				}
				System.out.println();
				break;
			}
		}
	}
}
