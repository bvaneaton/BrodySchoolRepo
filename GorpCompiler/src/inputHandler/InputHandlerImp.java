package inputHandler;

import java.io.FileNotFoundException;

/** Line-based file reader that knows the filename and line number.
 *  The lines returned DO NOT include the line terminator.
 * @author shermer
 *
 */
public class InputHandlerImp extends LineBasedIteratorImp
									   implements InputHandler {
	protected int lineNumber;
	protected String filename;
	
	public InputHandlerImp(String filename)
			throws FileNotFoundException {
		super(filename);
		this.filename = filename;
		this.lineNumber = 0;
	}


	@Override
	public String next() {
		lineNumber++;
		return super.next();
	}
	
	/** Get the current line number.
	 * @return the one-based line number of the line that the
	 * <code>next()</code> last returned, or
	 * zero if <code>next</code> has not been called.
	 */
	public int lineNumber() {
		return lineNumber;
	}
	/** Get the current file name.
	 * @return the filename that was passed to the constructor.
	 * This may be a relative or absolute file name.
	 */
	public String fileName() {
		return filename;
	}
}
