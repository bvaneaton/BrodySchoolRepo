package parseTree;

import tokens.Token;

public class StatementListNode extends ParseNode {

	public StatementListNode(Token token) {
		super(token);
	}

	public StatementListNode(ParseNode node) {
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
