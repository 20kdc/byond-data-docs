package after.net.web;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedList;

/**
 * And now you're going to find out why I got into the protocol side of things in the first place.
 * Note that this code is AWFUL, because some insane person figured it would be a good idea
 *  to make the WebSocket spec into a fucking nightmare to even get past the handshake.
 * #remove-sec-websocket-key
 */
public class WebAdapterMain {
	private static String targetHost;
	private static int targetPort;

	public static void main(String[] args) throws Exception {
		int sourcePort = Integer.parseInt(args[0]);
		targetHost = args[1];
		targetPort = Integer.parseInt(args[2]);
		
		ServerSocket ss = new ServerSocket(sourcePort);
		
		while (true) {
			Socket clientSocket = ss.accept();
			new Thread(new WebAdapterBaseWebClientRunnable(clientSocket) {
				
				@Override
				public void handleWebSocket() throws Exception {
					// Get the client to handshake
					clientOutput.writeShort(0x820A);
					clientOutput.writeShort(0x0001);
					// these have to be LE as usual
					clientOutput.writeInt(0x00020000);
					clientOutput.writeInt(0x00020000);

					// Winset
					clientOutput.writeShort(0x8207);
					clientOutput.writeShort(0x00E5);
					// u16 0
					clientOutput.writeByte(0);
					clientOutput.writeByte(0);
					// u16 1
					clientOutput.writeByte(1);
					clientOutput.writeByte(0);
					// !
					clientOutput.writeByte('!');

					// Get the client to boot
					clientOutput.writeShort(0x8208);
					clientOutput.writeShort(0x000E);
					clientOutput.writeShort(0x0000);
					clientOutput.writeInt(0);

					// stop
					Thread.sleep(10000);
				}
			}).start();
		}
	}
}
