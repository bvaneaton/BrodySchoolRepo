package symbolTable;

import inputHandler.TextLocation;
import semanticAnalyzer.PrimitiveType;
import semanticAnalyzer.Type;

public class Binding {
	protected Type type;
	protected TextLocation textLocation;
	protected String lexeme;
	protected MemoryLocation memoryLocation;
	
	public Binding(Type type, TextLocation location, MemoryLocation memoryLocation, String lexeme) {
		super();
		this.type = type;
		this.textLocation = location;
		this.memoryLocation = memoryLocation;
		this.lexeme = lexeme;
	}
	
	public String toString() {
		return "[" + uniqueIdent() +
				" " + type +  // " " + textLocation +	
				" " + memoryLocation +
				"]";
	}

	public String uniqueIdent(){
		return lexeme;
	}
	public Type getType() {
		return type;
	}
	public TextLocation getLocation() {
		return textLocation;
	}
	public MemoryLocation getMemoryLocation() {
		return memoryLocation;
	}

	
////////////////////////////////////////////////////////////////////////////////////
// Null Binding object
////////////////////////////////////////////////////////////////////////////////////

	public static Binding nullInstance() {
		return NullBinding.getInstance();
	}
	private static class NullBinding extends Binding {
		private static NullBinding instance=null;
		private NullBinding() {
			super(PrimitiveType.UNDEFINED,
					TextLocation.nullInstance(),
					MemoryLocation.nullInstance(),
					"the-null-binding");
		}
		public static NullBinding getInstance() {
			if(instance==null)
				instance = new NullBinding();
			return instance;
		}
	}
}
