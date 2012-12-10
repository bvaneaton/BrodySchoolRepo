package parseTree;

import tokens.Token;

public class FunctionBlock extends ParseNode {

	public FunctionBlock(Token token) {
		super(token);
	}

	public FunctionBlock(ParseNode node) {
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
