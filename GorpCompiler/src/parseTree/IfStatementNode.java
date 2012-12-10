package parseTree;

import tokens.Token;

public class IfStatementNode extends ParseNode {
    private boolean check;
	public IfStatementNode(Token token) {
		super(token);
	}

	public IfStatementNode(ParseNode node) {
		super(node);
	}
////////////////////////////////////////////////////////////
// attributes
	public boolean IfStatementClauseCheck() {
		return this.check;
	}	
		
	public void SetIfStatementClauseCheck(boolean boolCheck) {
		this.check = boolCheck;
	}	
///////////////////////////////////////////////////////////
// welcome mat for visitors
			
	public boolean accept(ParseNodeVisitor visitor) {
		boolean doChildren = visitor.visitEnter(this);
		visitChildren(visitor, doChildren);
		return visitor.visitLeave(this);
	}
}
