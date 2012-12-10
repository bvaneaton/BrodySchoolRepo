package tokens;

import inputHandler.TextLocation;

public class BooleanToken extends TokenImp {
	protected boolean value;
	
	protected BooleanToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}
	protected void setValue(boolean value) {
		this.value = value;
	}
	public boolean getValue() {
		return value;
	}
	
	public static BooleanToken make(TextLocation location, String lexeme) {
		BooleanToken result = new BooleanToken(location, lexeme);
		result.setValue(Boolean.parseBoolean(lexeme));
		return result;
	}
	
	@Override
	protected String rawString() {
		return "Boolean, " + value;
	}
}
