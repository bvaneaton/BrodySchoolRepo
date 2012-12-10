package symbolTable;

import java.util.Stack;

import parseTree.ParseNode;
import semanticAnalyzer.ObjectType;

public class Scopes {
	static Stack<Scope> scopes = new Stack<Scope>();
	
	private Scopes() {}
	
	public static void enterScope(ParseNode node) {
		int level = scopes.size();
		Scope baseScope = (level == 0) ? null : scopes.peek().getBaseScope();
		Scope scope = Scope.createDynamicScope(level, baseScope);
		scopes.push(scope);
		node.setScope(scope);
	}
	public static void enterObjectScope(ParseNode node) {
		int level = scopes.size();
		Scope baseScope = (level == 0) ? null : scopes.peek().getBaseScope();
		Scope scope = Scope.createStaticScope(level, baseScope);
		scopes.push(scope);
		node.setScope(scope);
	}
	public static Scope leaveScope() {
		Scope scope = scopes.pop();
		scope.leave();
		return scope;
	}
	
	public static int getCurrentAllocatedSize() {
		if(scopes.isEmpty()) {
			return 0;
		}
		return scopes.peek().getAllocatedSize();
	}
}
