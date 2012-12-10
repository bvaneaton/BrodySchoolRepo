package tokens;

import inputHandler.TextLocation;

public class OperatorToken extends TokenImp {

	private Operator operator;
	
	protected OperatorToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}
	protected void setOperator(Operator operator) {
		this.operator = operator;
	}
	public Operator getOperator() {
		return operator;
	}

	
	public static OperatorToken make(TextLocation location, String lexeme, Operator operator) {
		OperatorToken result = new OperatorToken(location, lexeme);
		result.setOperator(operator);
		return result;
	}
	
	protected String rawString() {
		return operator.toString();
	}
	
	public boolean isOperator(Operator operator) {
		return this.operator == operator;
	}
	public boolean isOperator() {
		return true;
	}
}
