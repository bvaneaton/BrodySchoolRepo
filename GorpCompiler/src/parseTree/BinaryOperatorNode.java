package parseTree;

import tokens.Operator;
import tokens.OperatorToken;
import tokens.Token;

public class BinaryOperatorNode extends ParseNode {
	public BinaryOperatorNode(Token token) {
		super(token);
	}
	public BinaryOperatorNode(ParseNode node) {
		super(node);
	}
////////////////////////////////////////////////////////////
// attributes
	
	public Operator getOperator() {
		OperatorToken operatorToken = (OperatorToken)token;
		return operatorToken.getOperator();
	}
///////////////////////////////////////////////////////////
// welcome mat for visitors
			
	public boolean accept(ParseNodeVisitor visitor) {
		boolean doChildren = visitor.visitEnter(this);
		visitChildren(visitor, doChildren);
		return visitor.visitLeave(this);
	}
}
