package after.tests;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import after.io.DMB;
import after.io.framework.DMBReadContext;
import after.io.framework.DMBWriteContext;

/**
 * Performs a general battery of sanity tests given source test subjects.
 */
@RunWith(Parameterized.class)
public class EquivalenceTest {
	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		final LinkedList<Object[]> tests = new LinkedList<Object[]>();
		File[] files = new File("external/test-subjects").listFiles();
		if (files != null)
			for (File f : files)
				tests.add(new Object[] {f.toPath()});
		return tests;
	}

	public final Path path;

	public EquivalenceTest(Path p) {
		path = p;
	}

	@Test
	public void runEquivalenceTest() throws Exception {
		byte[] data = Files.readAllBytes(path);
		DMB d = new DMB();
		d.read(new DMBReadContext(data, true));
		AtomicInteger index = new AtomicInteger();
		d.write(new DMBWriteContext(true) {
			@Override
			protected void i8Internal(int value) {
				byte res = (byte) value;
				int where = index.getAndIncrement();
				if (res != data[where])
					throw new RuntimeException("Mismatch at 0x" + Integer.toHexString(where));
			}
		});
		int finalWhere = index.get();
		if (finalWhere != data.length)
			throw new RuntimeException("Too short, 0x" + Integer.toHexString(finalWhere));
		
		if (d.strings.calculateTotalSize() != d.totalSizeOfAllStrings)
			throw new RuntimeException("invalid string total size");

		if (d.strings.calculateHash() != d.strings.hash)
			throw new RuntimeException("invalid string hash");
	}
}
