package parseTree;

import tokens.BooleanToken;
import tokens.Token;

public class BooleanNode extends ParseNode {

	public BooleanNode(Token token) {
		super(token);
	}
	public BooleanNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public boolean getValue() {
		BooleanToken boolToken = (BooleanToken)token;
		return boolToken.getValue();
	}

	
///////////////////////////////////////////////////////////
// welcome mat for visitors
	
	public boolean accept(ParseNodeVisitor visitor) {
		return visitor.visit(this);
	}

}
