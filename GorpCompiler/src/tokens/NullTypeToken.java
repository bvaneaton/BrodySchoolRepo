package tokens;

import inputHandler.TextLocation;

public class NullTypeToken extends TokenImp {
	protected int value;
	
	protected NullTypeToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}
	protected void setValue() {
		this.value = 0;
	}
	public int getValue() {
		return value;
	}
	
	public static NullTypeToken make(TextLocation location, String lexeme) {
		NullTypeToken result = new NullTypeToken(location, lexeme);
		result.setValue();
		return result;
	}
	
	@Override
	protected String rawString() {
		return "Null, " + value;
	}
}
