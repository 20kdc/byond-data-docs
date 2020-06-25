package after.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

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
		DataOutputStream dosC = new DataOutputStream(sockC.socket().getOutputStream());
		DataOutputStream dosS = new DataOutputStream(sockS.socket().getOutputStream());
		
		boolean clientHasSeq = false;
		
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
				PacketFrame pf = PacketFrame.read(disC, clientHasSeq);
				pf.write(dosS, clientHasSeq);
				log("C", pf);
				clientHasSeq = true;
			}
			if (hitS) {
				PacketFrame pf = PacketFrame.read(disS, false);
				pf.write(dosC, false);
				log("S", pf);
			}
		}
	}
	
	private static void log(String where, PacketFrame pf) {
		System.out.println(where + " " + Integer.toHexString(pf.sequence & 0xFFFF) + " " + Integer.toHexString(pf.type & 0xFFFF));
		int len = pf.data.capacity();
		for (int i = 0; i < len; i++) {
			String hx = Integer.toHexString(pf.data.get(i) & 0xFF);
			if (hx.length() == 1)
				hx = "0" + hx;
			System.out.print(" " + hx);
		}
		System.out.println();
	}
}
