package parseTree;

import tokens.Token;

public class UnaryNode extends ParseNode {

	public UnaryNode(Token token) {
		super(token);
	}
	public UnaryNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public int getValue() {
		UnaryNode OperatorToken = (UnaryNode)token;
		return OperatorToken.getValue();
	}

	
///////////////////////////////////////////////////////////
// welcome mat for visitors
	
	public boolean accept(ParseNodeVisitor visitor) {
		boolean doChildren = visitor.visitEnter(this);
		visitChildren(visitor, doChildren);
		return visitor.visitLeave(this);
	}

}
