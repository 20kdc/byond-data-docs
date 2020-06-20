package after.tests;

import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import after.Eden;
import after.io.DMB;
import after.io.DMBProcTable;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

/**
 * This is an ongoing project to create an "eden file", a completely non-BYOND-created DMB file.
 * It will be considered successful when BYOND is capable of loading and running the DMB.
 * I've gotten to that point, so there's also a little side project to add a verb to that.
 */
public class EdenTest {
	@Test
	public void runEdenTest() throws Exception {
		DMB dmb = Eden.newEden();
		// ---
		put(dmb, "eden.dmb");
		// ---
		
		dmb.standardObjectIDs.idAZA = dmb.strings.of("default");
		
		// verb table
		int lVT = dmb.lists.add(new int[] {0});
		// empty
		int lEmpty = dmb.lists.add(new int[0]);
		// verb code
		// GETVAR src
		// PUSHVAL ">:D"
		// OUTPUT
		// END
		int lVC = dmb.lists.add(new int[] {0x33, 0xFFCE, 0x60, 0x06, dmb.strings.of(">:D"), 0x03, 0});
		
		DMBProcTable.Entry testProc = new DMBProcTable.Entry();
		testProc.name = dmb.strings.of("boop");
		testProc.path = dmb.strings.of("/client/verb/boop");
		testProc.verbCategory = dmb.strings.of("boop");
		testProc.unkC = 0;
		testProc.code = lVC;
		testProc.args = lEmpty;
		testProc.locals = lEmpty;
		dmb.procs.add(testProc);
		
		dmb.classes.entries.get(dmb.standardObjectIDs.client).verbTable = lVT;
		
		dmb.fixStrings();
		// ---
		put(dmb, "eden.ext.dmb");
	}

	private static void put(DMB dmb, String where) throws Exception {
		FileOutputStream putEden = new FileOutputStream(where);
		dmb.write(new DMBWriteContext(true) {
			@Override
			protected void i8Internal(int value) {
				try {
					putEden.write(value);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		putEden.close();
	}
}
