package tokens;

import inputHandler.TextLocation;

public class KeywordToken extends TokenImp {
	protected static int value;

	protected KeywordToken(TextLocation location, String lexeme) {
		super(location, lexeme.intern());
	}
	
	protected void setValue(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	
	
	public static KeywordToken make(TextLocation location, String lexeme, int constValue) {
		KeywordToken result = new KeywordToken(location, lexeme);
		value = constValue;
		return result;
	}

	@Override
	protected String rawString() {
		return "Keyword, " + getLexeme() + ":";
	}
}
