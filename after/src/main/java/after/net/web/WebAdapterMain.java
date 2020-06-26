package after.net.web;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedList;

import after.net.PacketFrame;

/**
 * Ok, so, APPARENTLY, the WebClient protocol has been made different enough in ways that make writing my own adapter...
 * not worth the effort, so the NEW purpose of this is as a workbench for webclient stuff
 * might make it an adapter again in future
 */
public class WebAdapterMain {

	public static void main(String[] args) throws Exception {
		LinkedList<byte[]> spoonfeedWinset = new LinkedList<>();
		for (String s : new File(".").list())
			if (s.endsWith(".dms") && !s.equalsIgnoreCase("defaultSkin.dms"))
				spoonfeedWinset.add(Files.readAllBytes(Paths.get(s)));
		spoonfeedWinset.add(Files.readAllBytes(Paths.get("defaultSkin.dms")));
		
		int sourcePort = Integer.parseInt(args[0]);
		
		ServerSocket ss = new ServerSocket(sourcePort);
		
		while (true) {
			Socket clientSocket = ss.accept();
			new Thread(new WebAdapterBaseWebClientRunnable(clientSocket) {
				
				@Override
				public void handleWebSocket() throws Exception {
					PacketFrame webFrame = new PacketFrame();
					
					// Say hello to the client
					webFrame.begin(0x0001);
					webFrame.data.putInt(0x200);
					webFrame.data.putInt(0x200);
					webFrame.writeWebSocket(clientOutput);

					// Winset
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					baos.write(0);
					baos.write(0);
					for (byte[] sp : spoonfeedWinset) {
						int len = sp.length;
						baos.write(len);
						baos.write(len >> 8);
						baos.write(sp);
					}
					PacketFrame.writeWebSocketInternal(clientOutput, (short) 0x00E5, baos.toByteArray(), 0, baos.size());

					// Get the client to boot
					webFrame.begin(0x000E);
					webFrame.data.putShort((short) 0);
					webFrame.data.putInt(0);
					webFrame.writeWebSocket(clientOutput);

					// put up a test verb
					webFrame.begin(0x00d2);
					webFrame.data.put((byte) 0);
					webFrame.data.putShort((short) 1);
					webFrame.data.putShort((short) 0);
					webFrame.data.put((byte) 2);
					for (int i = 0; i < 3; i++) {
						webFrame.data.putShort((short) 5);
						byte[] bt = "HELLO".getBytes();
						for (byte b : bt)
							webFrame.data.put(b);
					}
					webFrame.data.putShort((short) 0);
					webFrame.data.put((byte) 0);
					webFrame.writeWebSocket(clientOutput);
					
					// stop
					while (true) {
						Thread.sleep(5000);
						webFrame.begin(0x0027);
						byte[] bt = "QUACK!<br/>\n".getBytes();
						for (byte b : bt)
							webFrame.data.put(b);
						webFrame.data.put((byte) 0);
						webFrame.writeWebSocket(clientOutput);
					}
				}
			}).start();
		}
	}
}
