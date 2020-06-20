package after.io.framework;

/**
 * Write context.
 */
public abstract class DMBWriteContext extends DMBContext implements WriteContext {
	public int position = 0;
	
	public DMBWriteContext(boolean dbg) {
		super(dbg);
	}

	@Override
	protected int position() {
		return position;
	}

	protected abstract void i8Internal(int value);
	
	@Override
	public void i8(int i) {
		i8Internal(i);
		position++;
	}
	
	public void id(int i) {
		if (largeObjectIDs) {
			i32(i);
		} else {
			i16(i);
		}
	}
	public void ids(int[] it) {
		for (int i : it)
			id(i);
	}
}
