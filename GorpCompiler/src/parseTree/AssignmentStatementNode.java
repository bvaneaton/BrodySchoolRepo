package parseTree;

import tokens.Token;

public class AssignmentStatementNode extends ParseNode {
	public final static int LHS = 0;		// child indices
	public final static int RHS = 1;

	public AssignmentStatementNode(Token token) {
		super(token);
	}

	public AssignmentStatementNode(ParseNode node) {
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
