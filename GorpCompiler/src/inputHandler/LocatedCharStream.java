package inputHandler;


import java.util.Iterator;



public class LocatedCharStream implements Iterator<LocatedChar> {
	public static final LocatedChar FLAG_END_OF_INPUT = NullLocatedChar.getInstance();

	private InputHandler input;
	private String line;
	private int index;

	private LocatedChar next;
	
	
	public LocatedCharStream(InputHandler input) {
		super();
		this.input = input;
		this.index = 0;
		this.line = "";
		preloadChar();
	}
	
	private void preloadChar() {
		ensureLineHasACharacter();
		next = nextCharInLine();
	}	
	private LocatedChar nextCharInLine() {
		if(endOfInput()) {
			return FLAG_END_OF_INPUT;
		}
		
		TextLocation location = new TextLocation(input.fileName(), input.lineNumber(), index);
		char character = line.charAt(index++);
		return new LocatedChar(character, location);
	}
	private void ensureLineHasACharacter() {
		while(!moreCharsInLine() && input.hasNext()) {
			readNextLine();
		}
	}
	private boolean endOfInput() {
		return !moreCharsInLine() && !input.hasNext();
	}
	private boolean moreCharsInLine() {
		return index < line.length();
	}
	private void readNextLine() {
		assert(input.hasNext());
		line = input.next();
		index = 0;
	}
	
	
//////////////////////////////////////////////////////////////////////////////
// Iterator<LocatedChar> overrides
// next() extra-politely returns a fully-formed LocatedChar (FLAG_END_OF_INPUT)
//         if hasNext() is false.

	@Override
	public boolean hasNext() {
		return next != FLAG_END_OF_INPUT;
	}
	@Override
	public LocatedChar next() {
		LocatedChar result = next;
		preloadChar();
		return result;
	}

	/**
	 * remove is an unsupported operation.  It throws an UnsupportedOperationException.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
