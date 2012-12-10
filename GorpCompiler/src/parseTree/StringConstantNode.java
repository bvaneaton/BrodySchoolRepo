package parseTree;

import tokens.StringConstantToken;
import tokens.Token;

public class StringConstantNode extends ParseNode {

	public StringConstantNode(Token token) {
		super(token);
	}
	public StringConstantNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public String getValue() {
		StringConstantToken stringToken = (StringConstantToken)token;
		return stringToken.getValue();
	}

///////////////////////////////////////////////////////////
// welcome mat for visitors
	
	public boolean accept(ParseNodeVisitor visitor) {
		return visitor.visit(this);
	}

}