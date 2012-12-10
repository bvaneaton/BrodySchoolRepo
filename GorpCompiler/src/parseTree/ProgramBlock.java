package parseTree;

import tokens.Token;

public class ProgramBlock extends ParseNode {

	public ProgramBlock(Token token) {
		super(token);
	}

	public ProgramBlock(ParseNode node) {
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
