package parseTree;

import tokens.Token;

public class ObjectStatementListNode extends ParseNode {

	public ObjectStatementListNode(Token token) {
		super(token);
	}

	public ObjectStatementListNode(ParseNode node) {
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
