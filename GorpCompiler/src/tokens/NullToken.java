package tokens;

import inputHandler.NullTextLocation;
import inputHandler.TextLocation;

public class NullToken extends TokenImp {

	protected NullToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}

	public static NullToken make(TextLocation location) {
		NullToken result = new NullToken(location, "");
		return result;
	}
	public static NullToken make() {
		return make(NullTextLocation.getInstance());
	}
	@Override
	protected String rawString() {
		return "NULL TOKEN / END OF INPUT";
	}
}
