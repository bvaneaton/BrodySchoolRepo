package inputHandler;

public class NullLocatedChar extends LocatedChar {
	private static final NullTextLocation NULL_TEXT_LOCATION = NullTextLocation.getInstance();
	private static final char NULL_CHAR = '\0';
	private static final NullLocatedChar instance = new NullLocatedChar(NULL_CHAR, NULL_TEXT_LOCATION);

	
	private NullLocatedChar(Character character, TextLocation location) {
		super(character, location);
	}

	public static NullLocatedChar getInstance() {
		return instance;
	}
}
