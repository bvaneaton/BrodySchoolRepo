package tokens;

import inputHandler.TextLocation;

public interface Token {
	public String getLexeme();
	public TextLocation getLocation();
	public String fullString();
	
	
	
	// these are a compromise.  One should not have methods on an interface
	// that deal with particular implementing classes of the interface.
	
	/** determine if this token is a particular operator.
	 * @param operator
	 * @return true if this token is the given operator
	 */
	public boolean isOperator(Operator operator);
	
	/** determine if this token is an operator.
	 * @return true if this token is an operator
	 */
	public boolean isOperator();
}

