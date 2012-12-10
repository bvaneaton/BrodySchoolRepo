package parseTree;

import tokens.Token;

public class ProgramNode extends ParseNode {

	public ProgramNode(Token token) {
		super(token);
	}

	public ProgramNode(ParseNode node) {
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
