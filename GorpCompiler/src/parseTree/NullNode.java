package parseTree;

import tokens.NullToken;
import tokens.Token;

public class NullNode extends ParseNode {

	public NullNode(Token token) {
		super(token);
	}
	public NullNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public int getValue() {
		NullNode NullToken = (NullNode)token;
		return NullToken.getValue();
	}

	
///////////////////////////////////////////////////////////
// welcome mat for visitors
	
	public boolean accept(ParseNodeVisitor visitor) {
		return visitor.visit(this);
	}

}
