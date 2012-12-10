package parseTree;

import symbolTable.Binding;
import tokens.Token;

// child[0] doesn't exist		for normal identifier
// child[0] is a ExprListNode	for indexed identifier

public class IdentifierNode extends ParseNode {
	private Binding binding;	// used only for identifiers (note type may != bindng.type)
	
	public IdentifierNode(Token token) {
		super(token);
		this.binding = null;
	}
	public IdentifierNode(ParseNode node) {
		super(node);
		
		if(node instanceof IdentifierNode) {
			this.binding = ((IdentifierNode)node).binding;
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
	public void setObject(){};

	
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
		return visitor.visit(this);
	}

}
