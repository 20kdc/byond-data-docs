package after.tools;

import after.io.DMB;
import after.tools.map.MapSurgeryMain;

/**
 * Testing utility to ensure stuff can be read.
 */
public class CheckMain {
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("check help");
			return;
		}
		// -- Read --
		DMB dmbG = MapSurgeryMain.get(args[0]);
		System.out.println("ok");
	}
}
