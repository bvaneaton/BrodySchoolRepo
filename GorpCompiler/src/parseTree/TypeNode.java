package parseTree;

import tokens.*;
import tokens.Token;

public class TypeNode extends ParseNode {

	public TypeNode(Token token) {
		super(token);
	}
	public TypeNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	
///////////////////////////////////////////////////////////
// welcome mat for visitors
	
	public boolean accept(ParseNodeVisitor visitor) {
		return visitor.visit(this);
	}

}
