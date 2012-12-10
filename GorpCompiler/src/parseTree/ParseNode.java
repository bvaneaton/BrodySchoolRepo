package parseTree;

import java.util.ArrayList;
import java.util.List;

import semanticAnalyzer.Type;
import symbolTable.Binding;
import symbolTable.Scope;
import symbolTable.SymbolTable;
import tokens.Token;

public class ParseNode {
	private static final int INDENT_INCREMENT = 4;
	Token token;
	List<ParseNode>	children;
	ParseNode parent;
	
	Type type;					// used for expressions
	private Scope scope;		// the scope created by this node, if any.
	String object_name;
	ParseNode object_tree;
	

	public ParseNode(Token token) {
		this.token = token;
		this.type = null;
		this.scope = null;
		newChildren();
		parent = null;
		object_tree = null;
	}
	public ParseNode(ParseNode node) {
		this.token = node.token;
		this.type = node.type;
		this.scope = null;
		this.children = new ArrayList<ParseNode>(node.children);
		this.object_name = null;
		fixChildrensParents();
	}
	public Token getToken() {
		return token;
	}
////////////////////////////////////////////////////////////////////////////////////
// attributes
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {
		return type;
	}
	
	
	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	public boolean hasScope() {
		return scope != null;
	}
	public Scope getLocalScope() {
		for(ParseNode current : pathToRoot()) {
			if(current.getToken().getLexeme().equals("prog")){
				return Scope.nullInstance();
			}
			if(current.hasScope()) {
				return current.getScope();
			}
		}
		return Scope.nullInstance();
	}
	public boolean containsBindingOf(String identifier) {
		if(!hasScope()) {
			return false;
		}
		SymbolTable symbolTable = scope.getSymbolTable();
		return symbolTable.containsKey(identifier);
	}
	public Binding bindingOf(String identifier) {
		if(!hasScope()) {
			return Binding.nullInstance();
		}
		SymbolTable symbolTable = scope.getSymbolTable();
		return symbolTable.lookup(identifier);
	}
	

////////////////////////////////////////////////////////////////////////////////////
// dealing with children
	
	public List<ParseNode> getChildren() {
		return children;
	}
	public ParseNode child(int i) {
		return children.get(i);
	}
	public void newChildren() {
		if(children != null) {
			for(ParseNode child : children) {
				child.parent = null;
			}
		}
		children = new ArrayList<ParseNode>();
	}
	public void replaceChildren(List<ParseNode> newChildList) {
		newChildren();
		children = new ArrayList<ParseNode>(newChildList);
		fixChildrensParents();
	}
	public void insertChild(ParseNode child) {
		children.add(0, child);
		child.setParent(this);
	}
	public void appendChild(ParseNode child) {
		children.add(child);
		child.setParent(this);
	}
	public void removeLastChild() {
		int last = nChildren() - 1;
		children.get(last).setParent(null);
		children.remove(last);
	}
	public void replaceChild(ParseNode child, ParseNode replacement) {
		replaceChild(children.indexOf(child), replacement);
	}
	public void replaceChild(int i, ParseNode replacement) {
		children.set(i, replacement);
		replacement.setParent(this);
	}
	public int nChildren() {
		return children.size();
	}
////////////////////////////////////////////////////////////////////////////////////
// dealing with parents
		
	private void setParent(ParseNode parseNode) {
		parent = parseNode;
	}
	public ParseNode getParent() {
		return parent;
	}
	private void fixChildrensParents() {
		for(ParseNode child : children) {
			child.parent = this;
		}
	}
	public void clearParent() {
		setParent(null);
	}
	public void setObject(String thisString){
		this.object_name = thisString;
	};
	public String getObject(){
		return object_name;
	};
	public void setTree(ParseNode ASTree){
		this.object_tree = ASTree;
	};
	public ParseNode getTree(){
		return object_tree;
	}
////////////////////////////////////////////////////////////////////////////////////
// toString() and supporting code
	
	private static String terminator = System.getProperty("line.separator");

	public String toString() {
		StringBuffer result = new StringBuffer();
		appendIndentedSubtree(0, result);
		return result.toString();
	}
	public void appendIndentedSubtree(int indent, StringBuffer result) {
		for(int i=0; i<indent; i++) {
			result.append(' ');
		}
		result.append(getClass().getName());
		result.append(' ');
		result.append(token.toString());
		if(type != null) {
			result.append(" " + type);
		}
		result.append(subtypeString());
		result.append(terminator);
		
		
		for(ParseNode child : children) {
			child.appendIndentedSubtree(indent+INDENT_INCREMENT, result);
		}
	}
	public String subtypeString() {
		return "";
	}
////////////////////////////////////////////////////////////////////////////////////
// for visitors
			
	public boolean accept(ParseNodeVisitor visitor) {
		boolean doChildren = visitor.visitEnter(this);
		visitChildren(visitor, doChildren);
		return visitor.visitLeave(this);
	}
	protected void visitChildren(ParseNodeVisitor visitor, boolean doChildren) {
		if(doChildren) {
			for(ParseNode child : children) {
				if(!child.accept(visitor))
					break;
			}
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////
// Iterable<ParseNode> pathToRoot
	
	public Iterable<ParseNode> pathToRoot() {
		return new PathToRootIterable(this);
	}
}
