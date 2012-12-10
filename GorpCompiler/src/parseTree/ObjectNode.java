package parseTree;

import symbolTable.Binding;
import tokens.Token;

// child[0] doesn't exist		for normal identifier
// child[0] is a ExprListNode	for indexed identifier

public class ObjectNode extends ParseNode {
	private boolean check = false;
	private Binding binding;	// used only for identifiers (note type may != bindng.type)
	
	public ObjectNode(Token token) {
		super(token);
		this.binding = null;
	}
	public ObjectNode(ParseNode node) {
		super(node);
		
		if(node instanceof ObjectNode) {
			this.binding = ((ObjectNode)node).binding;
		}
		else {
			this.binding = null;
		}
	}
	
////////////////////////////////////////////////////////////
// attributes
	
	public boolean hasIndices() {
		return nChildren() == 1;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}
	public Binding getBinding() {
		return binding;
	}
	public void setCheckBlock(){
		check = true;
	}
	public boolean checkBlock(){
		return check;
	}
	
////////////////////////////////////////////////////////////
// specialty function
	
	public Binding findVariableBinding() {
		String identifier = token.getLexeme();
		
		for(ParseNode current : pathToRoot()) {
			if(current.containsBindingOf(identifier)) {
				return current.bindingOf(identifier);
			}
		}
		return Binding.nullInstance();
	}
///////////////////////////////////////////////////////////
// welcome mat for visitors
	
	public boolean accept(ParseNodeVisitor visitor) {
		boolean doChildren = visitor.visitEnter(this);
		visitChildren(visitor, doChildren);
		return visitor.visitLeave(this);
	}
}
