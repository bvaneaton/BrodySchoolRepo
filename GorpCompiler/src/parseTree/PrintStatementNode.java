package parseTree;

import tokens.Token;

public class PrintStatementNode extends ParseNode {
	boolean printNewline = true;
	
	public PrintStatementNode(Token token) {
		super(token);
	}

	public PrintStatementNode(ParseNode node) {
		super(node);
	}
	
////////////////////////////////////////////////////////////
// attributes
	
	public void setNewline(boolean value) {
		printNewline = value;
	}
	public boolean getNewline() {
		return printNewline;
	}
	@Override
	public String subtypeString() {
		return printNewline? " with newline" : " no newline";
	}
///////////////////////////////////////////////////////////
// welcome mat for visitors
		
	public boolean accept(ParseNodeVisitor visitor) {
		boolean doChildren = visitor.visitEnter(this);
		visitChildren(visitor, doChildren);
		return visitor.visitLeave(this);
	}
}
