package inputHandler;

import java.io.FileNotFoundException;

// a decorator that adds a string to the end of every line 
// returned by another InputHandler.
public class TerminatingInputHandler implements InputHandler {
	private static final String DEFAULT_TERMINATOR = "\n";
	private InputHandler input;
	private String terminator;
	
	
	public TerminatingInputHandler(InputHandler input,
			String terminator) {
		super();
		this.input = input;
		this.terminator = terminator;
	}
	public TerminatingInputHandler(InputHandler input) {
		this(input, DEFAULT_TERMINATOR);
	}
	
	
	
	@Override
	public String next() {
		return input.next() + terminator;
	}
	
//////////////////////////////////////////////////////////////////////////////
// simple delegates to input.

	@Override
	public String fileName() {
		return input.fileName();
	}
	@Override
	public int lineNumber() {
		return input.lineNumber();
	}
	@Override
	public boolean hasNext() {
		return input.hasNext();
	}
	@Override
	public void remove() {
		input.remove();
	}

//////////////////////////////////////////////////////////////////////////////
// factories
	
	public static InputHandler fromFilename(String filename, String terminator)
	throws FileNotFoundException {
		InputHandler handler = new InputHandlerImp(filename);
		return new TerminatingInputHandler(handler, terminator);
	}
	public static InputHandler fromFilename(String filename)
	throws FileNotFoundException {
		return fromFilename(filename, DEFAULT_TERMINATOR);
	}
}
