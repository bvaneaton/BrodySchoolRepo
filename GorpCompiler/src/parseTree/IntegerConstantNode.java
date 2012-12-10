package parseTree;

import tokens.IntegerConstantToken;
import tokens.Token;

public class IntegerConstantNode extends ParseNode {

	public IntegerConstantNode(Token token) {
		super(token);
	}
	public IntegerConstantNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public int getValue() {
		IntegerConstantToken numberToken = (IntegerConstantToken)token;
		return numberToken.getValue();
	}

	
///////////////////////////////////////////////////////////
// welcome mat for visitors
	
	public boolean accept(ParseNodeVisitor visitor) {
		return visitor.visit(this);
	}

}
