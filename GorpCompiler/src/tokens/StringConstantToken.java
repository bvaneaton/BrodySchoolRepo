package tokens;

import inputHandler.TextLocation;

public class StringConstantToken extends TokenImp implements Token {
	static final char DOUBLE_QUOTE = '"';
	protected String value;
	
	protected StringConstantToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}
	protected void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	public static StringConstantToken make(TextLocation location, String lexeme) {
		StringConstantToken result = new StringConstantToken(location, lexeme);
		result.setValue(stripDoubleQuotes(lexeme));
		assert result.getValue().length() + 2 == lexeme.length();
		return result;
	}
	private static String stripDoubleQuotes(String quotedString) {
		int lastIndex = quotedString.length()-1;
		
		assert quotedString.charAt(0)== DOUBLE_QUOTE;
		assert quotedString.charAt(lastIndex) == DOUBLE_QUOTE;
		return quotedString.substring(1, lastIndex);
	}
	
	@Override
	protected String rawString() {
		return "stringConstant, " + '"' + value + '"';
	}
}