package symbolTable;

import inputHandler.TextLocation;
import errorHandler.Error;
import parseTree.IdentifierNode;
import semanticAnalyzer.Type;
import tokens.Token;


public class Scope {
	private final int staticNestingLevel;
	private Scope baseScope;
	private AllocationStrategy allocator;
	private SymbolTable symbolTable;
	
	
//////////////////////////////////////////////////////////////////////
// factories and private constructor.
	
	public static Scope createDynamicScope(int staticNestingLevel, Scope baseScope) {
		AllocationStrategy allocator = dynamicAllocator(baseScope);
		return new Scope(staticNestingLevel, allocator, baseScope);
	}
	private static AllocationStrategy dynamicAllocator(Scope baseScope) {
		if (baseScope == null) {
			return new NegativeAllocationStrategy(BaseAddress.FRAME_POINTER, 0);
		}
		return baseScope.getAllocationStrategy();
	}
	public static Scope createStaticScope(int staticNestingLevel, Scope baseScope) {
		AllocationStrategy allocator = staticAllocator(baseScope);
		return new Scope(staticNestingLevel, allocator, baseScope);
	}
	private static AllocationStrategy staticAllocator(Scope baseScope) {
		if (baseScope == null) {
			return new PositiveAllocationStrategy(BaseAddress.FRAME_POINTER, 0);
		}
		return baseScope.getAllocationStrategy();
	}
	
	private Scope(int staticNestingLevel, AllocationStrategy allocator, Scope baseScope) {
		super();
		this.baseScope = (baseScope == null) ? this : baseScope;
		this.staticNestingLevel = staticNestingLevel;
		this.symbolTable = new SymbolTable();
		
		this.allocator = allocator;
		allocator.saveState();
	}
	
///////////////////////////////////////////////////////////////////////
//  basic queries
	
	public int getStaticNestingLevel() {
		return staticNestingLevel;
	}
	public Scope getBaseScope() {
		return baseScope;
	}
	private AllocationStrategy getAllocationStrategy() {
		return allocator;
	}
	
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}
///////////////////////////////////////////////////////////////////////
//  memory allocation

	// must call leave() when destroying a scope.
	public void leave() {
		allocator.restoreState();
	}
	public int getAllocatedSize() {
		return allocator.getMaxAllocatedSize();
	}

///////////////////////////////////////////////////////////////////////
// bindings

	public Binding createBinding(IdentifierNode identifierNode, Type type) {
		Token token = identifierNode.getToken();
		symbolTable.errorIfAlreadyDefined(token);
		
		String lexeme = token.getLexeme();
		Binding binding = allocateNewBinding(type, token.getLocation(), lexeme);	
		symbolTable.install(lexeme, binding);
		return binding;
	}
	private Binding allocateNewBinding(Type type, TextLocation textLocation, String lexeme) {
		MemoryLocation memoryLocation = allocator.allocate(type.getSize());
		return new Binding(type, textLocation, memoryLocation, lexeme);
	}
	
///////////////////////////////////////////////////////////////////////
//  toString
	
	public String toString() {
		String result = "scope: static nesting level " + staticNestingLevel + "\n";
//		for(Binding binding : bindings) {
//			String bindingString = "    " + binding + "\n";
//			result += bindingString;
//		}
		return result;
	}
	
////////////////////////////////////////////////////////////////////////////////////
// Null Scope object
////////////////////////////////////////////////////////////////////////////////////

	public static Scope nullInstance() {
		return NullScope.getInstance();
	}
	private static class NullScope extends Scope {
		private static final int NULL_SCOPE_NESTING_LEVEL = -1;
		private static NullScope instance = null;
		
		private NullScope() {
			super(NULL_SCOPE_NESTING_LEVEL,
					new PositiveAllocationStrategy(BaseAddress.NULL_BASE_ADDRESS, 0),
					null);
		}
		public String toString() {
			return "scope: the-null-scope";
		}
		@Override
		public Binding createBinding(IdentifierNode identifierNode, Type type) {
			unscopedIdentifierError(identifierNode.getToken());
			return super.createBinding(identifierNode, type);
		}
		public static Scope getInstance() {
			if(instance==null)
				instance = new NullScope();
			return instance;
		}
	}
	
	
///////////////////////////////////////////////////////////////////////
// error reporting

	private static void unscopedIdentifierError(Token token) {
		Error.reportError("variable " + token.getLexeme() + 
				          " used outside of any scope at " + token.getLocation());
	}
	
}
