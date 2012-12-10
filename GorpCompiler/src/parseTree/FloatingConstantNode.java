package parseTree;

import tokens.FloatingConstantToken;
import tokens.Token;

public class FloatingConstantNode extends ParseNode {

	public FloatingConstantNode(Token token) {
		super(token);
	}
	public FloatingConstantNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public float getValue() {
		FloatingConstantToken numberToken = (FloatingConstantToken)token;
		return numberToken.getValue();
	}

	
///////////////////////////////////////////////////////////
// welcome mat for visitors
	
	public boolean accept(ParseNodeVisitor visitor) {
		return visitor.visit(this);
	}

}
