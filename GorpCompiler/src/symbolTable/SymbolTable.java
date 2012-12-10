package symbolTable;

import java.util.HashMap;
import java.util.Map;

import errorHandler.Error;

import tokens.Token;

public class SymbolTable {
	private Map<String, Binding> table;
	
	SymbolTable() {
		table = new HashMap<String, Binding>();
	}
	
	public boolean containsKey(String identifier) {
		return table.containsKey(identifier);
	}
	public Binding lookup(String identifier) {
		Binding binding = table.get(identifier);
		if(binding == null) {
			return Binding.nullInstance();
		}
		return binding;
		
	}
	public Binding install(String identifier, Binding binding) {
		table.put(identifier, binding);
		return binding;
	}

	
	public void errorIfAlreadyDefined(Token token) {
		if(containsKey(token.getLexeme())) {		
			multipleDefinitionError(token);
		}
	}
	static void multipleDefinitionError(Token token) {
		Error.reportError("variable " + token.getLexeme() + 
				          " multiply defined at " + token.getLocation());
	}
}
