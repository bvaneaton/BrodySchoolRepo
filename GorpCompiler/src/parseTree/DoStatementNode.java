package parseTree;

import tokens.Token;

public class DoStatementNode extends ParseNode {

	public DoStatementNode(Token token) {
		super(token);
	}

	public DoStatementNode(ParseNode node) {
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
