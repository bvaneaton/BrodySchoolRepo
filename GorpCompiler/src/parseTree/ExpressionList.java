package parseTree;

import tokens.Token;

public class ExpressionList extends ParseNode {

	public ExpressionList(Token token) {
		super(token);
	}

	public ExpressionList(ParseNode node) {
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
