package parseTree;

import tokens.NullTypeToken;
import tokens.Token;

public class NullTypeNode extends ParseNode {

	public NullTypeNode(Token token) {
		super(token);
	}
	public NullTypeNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public int getValue() {
		NullTypeNode NullTypeToken = (NullTypeNode)token;
		return NullTypeToken.getValue();
	}

	
///////////////////////////////////////////////////////////
// welcome mat for visitors
	
	public boolean accept(ParseNodeVisitor visitor) {
		return visitor.visit(this);
	}

}
