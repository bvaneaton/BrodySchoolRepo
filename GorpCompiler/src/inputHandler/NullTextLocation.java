package inputHandler;

public class NullTextLocation extends TextLocation {
	private static NullTextLocation instance = new NullTextLocation("null", -1, -1);
	
	private NullTextLocation(String filename, int lineNumber, int position) {
		super(filename, lineNumber, position);
	}
	
	public static NullTextLocation getInstance() {
		return instance;
	}
	
	@Override
	public String toString() {
		return "(no location)";
	}
}
