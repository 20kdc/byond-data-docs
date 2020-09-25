package after.tools.map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import after.io.DMB;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

public class MapSurgeryMain {
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("check help");
			return;
		}
		// -- Read --
		DMB dmbG = get(args[0]);
		DMB dmbM = get(args[1]);
		// -- Map transplantation into dmbG --
		MapSurgery.run(dmbG, dmbM);
		// -- Write --
		FileOutputStream fos = new FileOutputStream(args[2]);
		dmbG.write(new DMBWriteContext(false) {
			@Override
			protected void i8Internal(int value) {
				try {
					fos.write(value);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		fos.close();
	}

	private static DMB get(String args) throws Exception {
		byte[] gameDMBData = Files.readAllBytes(new File(args).toPath());
		DMB d = new DMB();
		d.read(new DMBReadContext(gameDMBData, true));
		return d;
	}
}
