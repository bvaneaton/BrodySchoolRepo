package inputHandler;

// value object for specifying the location of a lexeme or other piece of text
public class TextLocation {
	private String filename;
	private int lineNumber;
	private int position;
	
	public TextLocation(String filename, int lineNumber, int position) {
		super();
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.position = position;
	}

	public String getFilename() {
		return filename;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getPosition() {
		return position;
	}
	
	public String toString() {
		return "(" + rawString() + ")";
	}

	private String rawString() {
		return filename + 
			   " line " + lineNumber + 
			   ", character " + position;
	}
	
	public static TextLocation nullInstance() {
		return NullTextLocation.getInstance();
	}
}