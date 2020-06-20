package after.io.framework;

public abstract class DMBContext {
	public boolean debug;
	
	// if you don't want to know, take the blue pill. you'll forget all about this.
	// but if you take the red pill, you'll see how deep this versioning problem really goes...
	// *link to DMB.md*
	public int vGEN, vLHS, vRHS;
	
	public int basePointer;
	public boolean largeObjectIDs;

	public DMBContext(boolean dbg) {
		debug = dbg;
	}

	abstract protected int position();
	
	public void section(String text) {
		if (debug)
			System.out.println(text + " @ 0x" + Integer.toHexString(position()));
	}

}
