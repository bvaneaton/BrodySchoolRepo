package semanticAnalyzer;
import parseTree.*;
import symbolTable.*;

public enum ObjectType implements Type {
	OBJECT(4);
	
	private int sizeInBytes;
	ParseNode ASTree;
	private symbolTable.Scope local_scope;
	private String objectName;
	private ObjectType(int size) {
		this.sizeInBytes = size;
	}
	
	public void ObjectType(){
		
	}
	public int getSize() {
		return sizeInBytes;
	}
	public String setName(String name){
		this.objectName = name;
		return "";
	}
	
	public String getName(){
		return objectName;
	}
	
	public ParseNode getTree(){
		return ASTree;
	}
	
	public void setTree(ParseNode ASTree_2){
		this.ASTree = ASTree_2;
	}
	
	public void setSymbol(symbolTable.Scope scopes){
		this.local_scope = scopes;
	}
	
	public symbolTable.Scope getScope(){
		return local_scope;
	}
	
	public int getSymbolSize(){
		int symbol_size = 0;
		//while(local_symbol.)
		
		return symbol_size;
	}
}
