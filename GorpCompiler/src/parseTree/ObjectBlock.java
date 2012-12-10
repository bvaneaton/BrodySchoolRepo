package parseTree;

import tokens.Token;

public class ObjectBlock extends ParseNode {

	public ObjectBlock(Token token) {
		super(token);
	}

	public ObjectBlock(ParseNode node) {
		super(node);
	}
////////////////////////////////////////////////////////////
// attributes
	
///////////////////////////////////////////////////////////
// welcome mat for visitors
			
	public boolean accept(ParseNodeVisitor visitor) {
		boolean doChildren = visitor.visitEnter(this);
		visitChildren(visitor, doChildren);
		return visitor.visitLeave(this);
	}
}
