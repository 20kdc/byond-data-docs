package after.net.web;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedList;

/**
 * Internals.
 */
abstract class WebAdapterBaseWebClientRunnable implements Runnable {
	public final Socket clientSocket;
	public DataInputStream clientInput;
	public DataOutputStream clientOutput;
	public WebAdapterBaseWebClientRunnable(Socket s) {
		clientSocket = s;
	}
	
	@Override
	public void run() {
		try {
			clientInput = new DataInputStream(clientSocket.getInputStream());
			clientOutput = new DataOutputStream(clientSocket.getOutputStream());
			
			// Initialization, Part 1 (request parsing)
			LinkedList<String> headerLines = new LinkedList<>();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (true) {
				byte b = clientInput.readByte();
				if (b == '\r')
					continue;
				if (b == '\n') {
					String line = new String(baos.toByteArray(), StandardCharsets.UTF_8);
					baos.reset();
					if (line.equals(""))
						break;
					headerLines.add(line);
				} else {
					baos.write(b);
				}
			}
			
			// Initialization, Part 2 (do we want to give the browser a file?)
			String path = headerLines.getFirst().split(" ")[1];
			
			boolean isWebSocket = false;
			String webSocketKey = null;
			
			for (int i = 0; i < headerLines.size(); i++) {
				String[] kv = headerLines.get(i).split(":");
				if (kv[0].trim().equalsIgnoreCase("Upgrade"))
					isWebSocket = true;
				else if (kv[0].trim().equalsIgnoreCase("Sec-WebSocket-Key")) {
					// just to be sure
					baos.reset();
					baos.write(kv[1].trim().getBytes(StandardCharsets.UTF_8));
					baos.write(("258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.UTF_8));
					// "seemingly" overcomplicated? SEEMINGLY?
					// No. NO. THIS IS UTTER INSANITY.
					// Dear goodness what is WRONG with whoever designed this
					webSocketKey = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA1").digest(baos.toByteArray()));
				}
			}
			
			System.out.println(path + " " + isWebSocket + " " + webSocketKey);
			
			if (isWebSocket) {
				clientOutput.writeBytes("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\n");
				if (webSocketKey != null)
					clientOutput.writeBytes("Sec-WebSocket-Accept: " + webSocketKey + "\r\n");
				clientOutput.writeBytes("Sec-WebSocket-Version: 13\r\n\r\n");
				handleWebSocket();
			} else {
				String status = "200 OK";
				byte[] contentResult;
				if (path.contains("\\")) {
					status = "404 Not Found";
					contentResult = "NO".getBytes();
				} else {
					String[] bits = path.split("/");
					if (bits.length == 0 || bits[bits.length - 1].equals("play")) {
						StringBuilder ongoing = new StringBuilder();
						// this is actually all that needs to be done
						ongoing.append("<script src=\"/res/ext.js\"></script>");
						ongoing.append("<script src=\"/res/webclient.dart.js\"></script>");
						ongoing.append("<div id=\"skin\"></div>");
						contentResult = ongoing.toString().getBytes();
					} else {
						try {
							contentResult = Files.readAllBytes(Paths.get(bits[bits.length - 1]));
						} catch (Exception e2) {
							status = "404 Not Found";
							contentResult = e2.getMessage().getBytes();
						}
					}
				}
				clientOutput.writeBytes("HTTP/1.1 " + status + "\r\nConnection: close\r\n");
				clientOutput.writeBytes("Content-Length: " + contentResult.length + "\r\n\r\n");
				clientOutput.write(contentResult);
				clientOutput.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (Exception e2) {
				// DROPPING A RESOURCE MUST NEVER BE ALLOWED TO FAIL.
			}
		}
	}
	
	public abstract void handleWebSocket() throws Exception;
}
