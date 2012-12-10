package parseTree;

import tokens.Token;

public class DeclarationNode extends ParseNode {
    private boolean check;
	public DeclarationNode(Token token) {
		super(token);
	}

	public DeclarationNode(ParseNode node) {
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
