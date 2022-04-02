package after.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import after.tools.map.MapSurgeryMain;
import after.tools.net.NetWorkbenchMain;
import after.tools.net.web.WebAdapterMain;

/**
 * Entrypoint for easier access to sub-tools.
 */
public class Main {
	private static Tool[] tools = new Tool[] {
		new Tool("mapsurgery", " GAME-DMB MAP-DMB OUTPUT", "Transplants a map from one DMB into another.", MapSurgeryMain.class),
		new Tool("proxy", "", "Launches a BYOND protocol proxy on localhost:5100 which proxies to localhost:5101. Decrypts messages.", NetWorkbenchMain.class),
		new Tool("broken-webadapter", " PORT", "DOESN'T WORK PROPERLY, attempts to get the webclient to do something", WebAdapterMain.class),
		new Tool("check", " DMB", "Checks that reading a DMB file works.", CheckMain.class),
		new Tool("exit", "", "Exits.", QuitMain.class)
	};
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			String[] slogan = new String[] {
				"Tools for reverse-engineering to rebuild after the end of the BYOND.",
				"Community Edition.",
				"Java client when? (when you code it)",
				"Decompiler when? (when you code it)",
				"Have you tried 'proxy'?",
				"Have you tried 'mapsurgery'?",
				"Home Edition.\n" +
				"+---------------------------------------------+\n" +
				"| Activate AFTER                              |\n" +
				"+---------------------------------------------+\n" +
				"| AFTER: Home Edition has not been activated. |\n" +
				"| Click here to activate AFTER: Home Edition. |\n" +
				"+---------------------------------------------+",
				"Activating WinRAR supports a dangerously monopolistic company known for dodgy license agreements.\n" +
				"...that's right, I'm talking about RARLAB. (Seriously, have you looked at the unrar license? No, not the GPL'd early versions.)"
			};
			System.out.println("AFTER: " + slogan[new Random().nextInt(slogan.length)]);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				System.out.append("> ");
				System.out.flush();
				try {
				String[] words = br.readLine().split(" ");
				if (words.length != 0)
					main(words);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		for (Tool t : tools) {
			if (args[0].equals(t.name)) {
				String[] remainingArgs = new String[args.length - 1];
				System.arraycopy(args, 1, remainingArgs, 0, remainingArgs.length);
				t.main.getMethod("main", String[].class).invoke(null, new Object[] {remainingArgs});
				return;
			}
		}
		help();
	}

	private static void help() {
		System.out.println("java -jar after-tools.jar SUBCOMMAND ARGS...");
		for (Tool t : tools) {
			System.out.println(t.name + t.args);
			System.out.println(" " + t.description);
		}
	}

	private static class Tool {
		public final String name, args, description;
		public final Class main;
		public Tool(String n, String a, String d, Class m) {
			name = n;
			args = a;
			description = d;
			main = m;
		}
	}
	
	private static class QuitMain {
		public static void main(String[] args) {
			System.exit(0);
		}
	}
}
