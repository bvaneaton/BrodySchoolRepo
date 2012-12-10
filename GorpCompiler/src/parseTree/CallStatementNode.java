package parseTree;

import tokens.Token;

public class CallStatementNode extends ParseNode {

	public CallStatementNode(Token token) {
		super(token);
	}

	public CallStatementNode(ParseNode node) {
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
