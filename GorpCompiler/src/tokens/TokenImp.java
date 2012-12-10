package tokens;

import inputHandler.TextLocation;

public abstract class TokenImp implements Token {
	private final TextLocation location;
	private final String lexeme;

	protected TokenImp(TextLocation location, String lexeme) {
		super();
		assert(location != null);
		assert(lexeme != null);
		this.location = location;
		this.lexeme = lexeme;
	}

	@Override
	public String getLexeme() {
		return lexeme;
	}

	@Override
	public TextLocation getLocation() {
		return location;
	}
	
	/** A string (not surrounded by parentheses) representing the subclass information.
	 * @return subclass information string
	 */
	abstract protected String rawString();
	
	public String toString() {
		return "(" + rawString() + ")";
	}
	
	/** convert to a string containing all information about the token.
	 * @return string with all token info.
	 */
	public String fullString() {
		return "(" + rawString() + 
			   ", " + location.toString() +
			   ", " + lexeme +
			   ")";
	}

	public boolean isOperator() {
		return false;
	}
	public boolean isOperator(Operator operator) {
		return false;
	}
}
