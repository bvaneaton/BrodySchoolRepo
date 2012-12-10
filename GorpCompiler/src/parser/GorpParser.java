package parser;
import java.util.*;
import java.lang.reflect.Constructor;
import semanticAnalyzer.*;
import inputHandler.TextLocation;
import parseTree.*;
import scanner.Scanner;
import tokens.*;
import errorHandler.Error;

public class GorpParser {
	private Scanner scanner;

	private Token nowReading = NullToken.make();
	private Token lastEaten  = NullToken.make();
	private Vector<String> objectVector = new Vector<String>();
	private Hashtable<ParseNode, String> functionNodeVector = new Hashtable<ParseNode, String>();
	private Hashtable<String, String> functionHash = new Hashtable<String, String>();
	private Hashtable<String, String> objectFunction = new Hashtable<String, String>();
	private String[][] objectFunDecals = new String[100][100];
	int functionCounter = 0;

	
	public GorpParser(Scanner scanner) {
		super();
		this.scanner = scanner;
		readToken();
	}
	private void readToken() {
			lastEaten = nowReading;
			nowReading = scanner.next();	
	}	
	
	public ParseNode parse_program() {
		boolean endCheck = false;
		ParseNode program = null;
		int obj = 0;
		int fun = 0;
		while(!(nowReading.getLexeme().equals("prog")))
		{
		while (true){			
			if (nowReading.getLexeme().equals("OBJECTS")){
				ProgramNode programNode = new ProgramNode(nowReading);
				ObjectNode objNode = new ObjectNode(nowReading);
				programNode.appendChild(objNode);
				program = new ProgramNode(programNode);
				readToken();
				while(true){
					if (nowReading.getLexeme().equals("OBJETS")){
						break;
					}
					else if (nowReading.getLexeme().equals("OBJ")){
						break;
					}
					else if (nowReading.getLexeme().equals("prog")){
						break;
					}
					else if (nowReading.getLexeme().equals("function")){
						break;
					}
					else if (nowReading.getLexeme().equals("fun")){
						break;
					}
					else
					{
						ParseNode objectDeclaration = new ObjectNode(nowReading);
						objectVector.add(objectDeclaration.getToken().getLexeme());
						objectFunDecals[obj][fun] = objectDeclaration.getToken().getLexeme();						
						obj++;
						if (program == null){
							//ProgramNode programNode = new ProgramNode(nowReading);
							program = new ProgramNode(nowReading);
							ObjectNode nodey = new ObjectNode(objectDeclaration);
							program.appendChild(nodey);
						}
						else
						{
							program.appendChild(objectDeclaration);
						}
						readToken();
					}
				}		
			}
			if (nowReading.getLexeme().equals("OBJ")){	
				boolean pass = true;
				ParseNode identifier = null;
					ParseNode objectBlock = new ObjectBlock(nowReading);
					if (program == null){
						program = new ProgramNode(nowReading);
						readToken();
						identifier = parse_identifier();
						identifier.setType(ObjectType.OBJECT);
						identifier.setObject(ObjectType.OBJECT.setName(identifier.getToken().getLexeme()));
						objectVector.add(identifier.getToken().getLexeme());
						Token tempToken = KeywordToken.make(nowReading.getLocation(), "OBJECTS", 199);
						ObjectNode objNode = new ObjectNode(tempToken);
						ObjectNode identifier_node = new ObjectNode(identifier.getToken());
						identifier_node.setType(ObjectType.OBJECT);
						identifier_node.setObject(ObjectType.OBJECT.setName(identifier.getToken().getLexeme()));
						program.appendChild(new ObjectNode(objNode));
						program.appendChild(identifier_node);
						identifier_node.appendChild(objectBlock);
						while( nowReadingAStatementStart() ) {
							if (nowReading.getLexeme().equals("JBO")){
								ObjectNode jboNode = new ObjectNode(nowReading);
								identifier.appendChild(jboNode);
								break;
							}
							if (nowReading.getLexeme().equals("FUN")){
								ParseNode funNode = new FunctionBlock(nowReading);
								if (program == null){
									program = new ProgramNode(nowReading);
									program.appendChild(new FunctionBlock(funNode));
								}
								else
								{
									objectBlock.appendChild(funNode);
								}
								readToken();
								TypeNode funTypeNode = new TypeNode(nowReading);
								readToken();
								ParseNode functionDeclaration = new FunctionNode(nowReading);
								funNode.appendChild(functionDeclaration);
								functionDeclaration.appendChild(funTypeNode);
								readToken();
								ParseNode expressionList = new ExpressionList(nowReading);
								functionDeclaration.appendChild(expressionList);
								expect(Operator.OPARENTTHESIS);				
								while(!(nowReading.isOperator(Operator.CPARENTTHESIS)))
								{
									TypeNode paraTypeNode = new TypeNode(nowReading);
									readToken();
									IdentifierNode identNode = new IdentifierNode(nowReading);
									readToken();
									expressionList.appendChild(identNode);
									identNode.appendChild(paraTypeNode);
								}
								readToken();
								ParseNode statementList = new StatementListNode(nowReading);
								functionDeclaration.appendChild(statementList);				
								while( nowReadingAStatementStart() ) {
									if (nowReading.getLexeme().equals("NUF")){
										ParseNode functionEnd = new FunctionBlock(nowReading);
										functionDeclaration.appendChild(functionEnd);
										readToken();
										functionNodeVector.put(funNode, objectBlock.getParent().getToken().getLexeme());
										break;
									}
									ParseNode statement = parse_statement();
									statementList.appendChild(statement);
									if(nowReading.getLexeme().equals("return")){
										ParseNode functionReturn = new FunctionNode(nowReading);
										readToken();
										ParseNode expressionNode = parse_expression();
										statementList.appendChild(functionReturn);
										functionReturn.appendChild(expressionNode);
										
									}
								}				
						}
						else
						{
							ParseNode declarations = parse_statement();
							objectBlock.appendChild(declarations);
						}
						}
						continue;
					}
					else
					{
						readToken();						
						identifier = parse_identifier();
						ObjectNode identifier_2 = new ObjectNode(identifier);
						identifier = identifier_2;
					}
					int i = 0;						
						while(i < program.nChildren()){	
							
							if (program.child(i).getToken().getLexeme().equals(identifier.getToken().getLexeme())){
								program.child(i).setType(ObjectType.OBJECT);
								program.child(i).setObject(ObjectType.OBJECT.setName(identifier.getToken().getLexeme()));
								program.child(i).appendChild(objectBlock);
								while( nowReadingAStatementStart() ) {
									if (nowReading.getLexeme().equals("JBO")){
										ObjectNode jboNode = new ObjectNode(nowReading);
										program.child(i).appendChild(jboNode);
										pass = false;
										break;
									}
									if (nowReading.getLexeme().equals("FUN")){
											ParseNode funNode = new FunctionBlock(nowReading);
											if (program == null){
												program = new ProgramNode(nowReading);
												program.appendChild(new FunctionBlock(funNode));
											}
											else
											{
												objectBlock.appendChild(funNode);
											}
											readToken();
											TypeNode funTypeNode = new TypeNode(nowReading);
											readToken();
											ParseNode functionDeclaration = new FunctionNode(nowReading);
											funNode.appendChild(functionDeclaration);
											functionDeclaration.appendChild(funTypeNode);
											readToken();
											ParseNode expressionList = new ExpressionList(nowReading);
											functionDeclaration.appendChild(expressionList);
											expect(Operator.OPARENTTHESIS);				
											while(!(nowReading.isOperator(Operator.CPARENTTHESIS)))
											{
												TypeNode paraTypeNode = new TypeNode(nowReading);
												readToken();
												IdentifierNode identNode = new IdentifierNode(nowReading);
												readToken();
												expressionList.appendChild(identNode);
												identNode.appendChild(paraTypeNode);
											}
											readToken();
											ParseNode statementList = new StatementListNode(nowReading);
											functionDeclaration.appendChild(statementList);				
											while( nowReadingAStatementStart() ) {
												if (nowReading.getLexeme().equals("NUF")){
													ParseNode functionEnd = new FunctionBlock(nowReading);
													functionDeclaration.appendChild(functionEnd);
													readToken();
													functionNodeVector.put(funNode, objectBlock.getParent().getToken().getLexeme());
													break;
												}
												ParseNode statement = parse_statement();
												statementList.appendChild(statement);
												if(nowReading.getLexeme().equals("return")){
													ParseNode functionReturn = new FunctionNode(nowReading);
													readToken();
													ParseNode expressionNode = parse_expression();
													statementList.appendChild(functionReturn);
													functionReturn.appendChild(expressionNode);
													
												}
											}				
									}
									else
									{
										ParseNode declarations = parse_statement();
										objectBlock.appendChild(declarations);
									}
								}							
							}
							i++;	
						}	
						if (pass == true){
							identifier.setType(ObjectType.OBJECT);
							identifier.setObject(ObjectType.OBJECT.setName(identifier.getToken().getLexeme()));
							objectVector.add(identifier.getToken().getLexeme());
							program.appendChild(identifier);
							program.child(program.nChildren() - 1).appendChild(objectBlock);							
							while( nowReadingAStatementStart() ) {
								boolean objectCheck = false;
								if (nowReading.getLexeme().equals("JBO")){
									ObjectNode jboNode = new ObjectNode(nowReading);
									program.child(program.nChildren() - 1).appendChild(jboNode);
									break;
								}								
								if (nowReading.getLexeme().equals("FUN")){
									ParseNode funNode = new FunctionBlock(nowReading);
									if (program == null){
										program = new ProgramNode(nowReading);
										program.appendChild(new FunctionBlock(funNode));
									}
									else
									{
										objectBlock.appendChild(funNode);
									}
									readToken();
									TypeNode funTypeNode = new TypeNode(nowReading);
									readToken();
									ParseNode functionDeclaration = new FunctionNode(nowReading);
									if (nowReading.isOperator(Operator.RESOLUTION)){
										if (objectVector.contains(lastEaten.getLexeme())){
											ParseNode funObjNode = new ObjectNode(lastEaten);
											ParseNode resolutionNode = new BinaryOperatorNode(nowReading);
											readToken();
											functionDeclaration = new FunctionNode(nowReading);
											funObjNode.appendChild(resolutionNode);
											resolutionNode.appendChild(functionDeclaration);
											funNode.appendChild(funObjNode);
											program.appendChild(funNode);
											objectCheck = true;		
											readToken();
										}
										else
										{
											compilerError("Dont know object " + lastEaten.getLexeme());
										}
									}
									if (objectCheck == false)
									{
										funNode.appendChild(functionDeclaration);
										functionDeclaration.appendChild(funTypeNode);
										readToken();
									}
									ParseNode expressionList = new ExpressionList(nowReading);
									functionDeclaration.appendChild(expressionList);
									expect(Operator.OPARENTTHESIS);				
									while(!(nowReading.isOperator(Operator.CPARENTTHESIS)))
									{
										TypeNode paraTypeNode = new TypeNode(nowReading);
										readToken();
										IdentifierNode identNode = new IdentifierNode(nowReading);
										readToken();
										expressionList.appendChild(identNode);
										identNode.appendChild(paraTypeNode);
									}
									readToken();
									ParseNode statementList = new StatementListNode(nowReading);
									functionDeclaration.appendChild(statementList);				
									while( nowReadingAStatementStart() ) {
										if (nowReading.getLexeme().equals("NUF")){
											ParseNode functionEnd = new FunctionBlock(nowReading);
											functionDeclaration.appendChild(functionEnd);
											readToken();
											functionNodeVector.put(funNode, objectBlock.getParent().getToken().getLexeme());
											break;
										}
										ParseNode statement = parse_statement();
										statementList.appendChild(statement);
										if(nowReading.getLexeme().equals("return")){
											ParseNode functionReturn = new FunctionNode(nowReading);
											readToken();
											ParseNode expressionNode = parse_expression();
											statementList.appendChild(functionReturn);
											functionReturn.appendChild(expressionNode);
											
										}
									}
								}
								else
								{
								ParseNode declarations = parse_statement();
								program.child(program.nChildren() - 1).child(0).appendChild(declarations);
								}
							}
						}
			}
			if (nowReading.getLexeme().equals("function")){
				FunctionNode funNode = new FunctionNode(nowReading);
				if (program == null){
					//program = new FunctionNode(funNode);
				}
				else
				{
					//program.appendChild(funNode);
				}
				
				readToken();
				int p = 0;
				while(true){
					FunctionNode funNode2Pointer;
					funNode2Pointer = funNode;
					if (nowReading.getLexeme().equals("OBJETS")){
						break;
					}
					else if (nowReading.getLexeme().equals("OBJ")){
						break;
					}
					else if (nowReading.getLexeme().equals("prog")){
						break;
					}
					else if (nowReading.getLexeme().equals("FUN")){
						break;
					}
					else if (nowReading.getLexeme().equals("function")){
						if (p == 0){
							readToken();
						}
						else
						{
							FunctionNode funNode2 = new FunctionNode(nowReading);
							funNode2Pointer = funNode2;
							program.appendChild(funNode2);
							readToken();
						}
					}
					else if (nowReading.isOperator(Operator.CPARENTTHESIS)){
						readToken();
					}
					else
					{
						boolean objectCheck = false;
						ParseNode functionDeclaration;
						TypeNode funTypeNode = new TypeNode(nowReading);
						readToken();
						functionDeclaration = new FunctionNode(nowReading);
						readToken();
						if (nowReading.isOperator(Operator.RESOLUTION)){
							if (objectVector.contains(lastEaten.getLexeme())){
								ParseNode funObjNode = new ObjectNode(lastEaten);
								ParseNode resolutionNode = new BinaryOperatorNode(nowReading);
								readToken();
								functionDeclaration = new FunctionNode(nowReading);
								funObjNode.appendChild(resolutionNode);
								resolutionNode.appendChild(functionDeclaration);
								funNode2Pointer.appendChild(funObjNode);
								program.appendChild(funNode2Pointer);
								objectCheck = true;		
								readToken();
								int y = 0;
								int funInc = 1;
								while(y < objectFunDecals.length){
									if (objectFunDecals[y][0] == funObjNode.getToken().getLexeme()){
										objectFunDecals[y][funInc] = functionDeclaration.getToken().getLexeme();
										functionCounter++;
										funInc++;
									}
									y++;
								}
								//objectFunDecals[obj][fun] =  
								objectFunction.put(functionDeclaration.getToken().getLexeme(), funObjNode.getToken().getLexeme());
							}
							else
							{
								compilerError("Dont know object " + lastEaten.getLexeme());
							}
						}
						functionHash.put(functionDeclaration.getToken().getLexeme(), funTypeNode.getToken().getLexeme());
						if (program == null){
							program = new ProgramNode(nowReading);
							program.appendChild(new FunctionNode(funNode));
							program.appendChild(functionDeclaration);
							functionDeclaration.appendChild(funTypeNode);
						}
						else
						{
							if (objectCheck == false)
							{
							funNode2Pointer.appendChild(functionDeclaration);
							functionDeclaration.appendChild(funTypeNode);
							program.appendChild(funNode2Pointer);
							}
						}
						
						expect(Operator.OPARENTTHESIS);
						while(!(nowReading.isOperator(Operator.CPARENTTHESIS)))
						{
							TypeNode paraTypeNode = new TypeNode(nowReading);
							readToken();
							IdentifierNode identNode = new IdentifierNode(nowReading);
							readToken();
							functionDeclaration.appendChild(identNode);
							identNode.appendChild(paraTypeNode);
						}
					}
				}	
			}
			
				if (nowReading.getLexeme().equals("FUN")){
					ParseNode funNode = new FunctionBlock(nowReading);
					if (program == null){
						program = new ProgramNode(nowReading);
						program.appendChild(new FunctionBlock(funNode));
					}
					else
					{
						program.appendChild(funNode);
					}
					readToken();
					TypeNode funTypeNode = new TypeNode(nowReading);
					readToken();
					ParseNode functionDeclaration = new FunctionNode(nowReading);
					funNode.appendChild(functionDeclaration);
					functionDeclaration.appendChild(funTypeNode);
					readToken();
					ParseNode expressionList = new ExpressionList(nowReading);
					functionDeclaration.appendChild(expressionList);
					expect(Operator.OPARENTTHESIS);				
					while(!(nowReading.isOperator(Operator.CPARENTTHESIS)))
					{
						TypeNode paraTypeNode = new TypeNode(nowReading);
						readToken();
						IdentifierNode identNode = new IdentifierNode(nowReading);
						readToken();
						expressionList.appendChild(identNode);
						identNode.appendChild(paraTypeNode);
					}
					readToken();
					ParseNode statementList = new StatementListNode(nowReading);
					functionDeclaration.appendChild(statementList);				
					while( nowReadingAStatementStart() ) {
						if (nowReading.getLexeme().equals("NUF")){
							ParseNode functionEnd = new FunctionBlock(nowReading);
							functionDeclaration.appendChild(functionEnd);
							if (program.child(0).getToken().getLexeme().equals("FUN")){
								if (program.child(0).nChildren() == 0){
								program.child(0).appendChild(functionDeclaration);
								}
							}
							readToken();
							functionNodeVector.put(funNode, "null");
							break;
						}
						ParseNode statement = parse_statement();
						statementList.appendChild(statement);
						if(nowReading.getLexeme().equals("return")){
							ParseNode functionReturn = new FunctionNode(nowReading);
							readToken();
							if (nowReading instanceof IntegerConstantToken || nowReading instanceof IdentifierToken || nowReading instanceof FloatingConstantToken ||
									nowReading instanceof BooleanToken || nowReading instanceof StringConstantToken || nowReading instanceof NullToken){
								ParseNode expressionNode = parse_expression();
								statementList.appendChild(functionReturn);
								functionReturn.appendChild(expressionNode);
							}	
							else
							{
								statementList.appendChild(functionReturn);
							}
						}
					}				
				}			
			if (nowReading.getLexeme().equals("prog")){
				break;
			}	
			if (nowReading.getLexeme().equals("OBJ")){
				continue;
			}
			if (nowReading.getLexeme().equals("FUN")){
				continue;
			}
			readToken();
			if (nowReading.getLexeme().equals("prog")){
				break;
			}
		}
		
	}
		if (nowReading.getLexeme().equals("prog")){
			if (program == null){
				program = new ProgramNode(nowReading);
				program.appendChild(new ProgramBlock(nowReading));
			}
			else
			{
				int i = 0;
				while(i < program.nChildren()){
					String temp = program.child(i).getToken().getLexeme();
					int j = 0;
					while (j < objectVector.size()){
						if (objectVector.contains(temp)){
							if (program.child(i).nChildren() >= 1){
								break;
							}
							else{
								compilerError("No object block for object " + temp);
							}
						}
						j++;
					}
					i++;
				}
				
				int k = 0;
				int checkHash = 0;
				while(k < functionHash.size()){
					Set<String> temp = functionHash.keySet();
					String[] newTemp = temp.toArray(new String[k]);
					String thisFun = newTemp[k];
					int z = 0;
					while(z < functionNodeVector.size())
					{
						Set<ParseNode> tempNode = functionNodeVector.keySet();
						Collection<String> typeSet = functionNodeVector.values();
						Object[] newTempNode = tempNode.toArray();
						Object[] newTempType = typeSet.toArray();
						ParseNode thisFunNode = (ParseNode)newTempNode[z];
						if(newTempType[z] != "null"){
							if (newTempType[z].equals(objectFunction.get(thisFun))){
								if (thisFun.equals(thisFunNode.child(0).getToken().getLexeme())){
									checkHash++;
								}
							}
							else
							{
								int y = 0;
								while (y < objectFunDecals.length){
									if (objectFunDecals[y][0] == null){
										break;
									}
									if (newTempType[z] == objectFunDecals[y][0]){
										int q = 0;
										while (q < objectFunDecals[y].length){
											if (objectFunDecals[y][q] == null){
												break;
											}
											if (thisFunNode.child(0).getToken().getLexeme() == objectFunDecals[y][q]){
												checkHash++;
												break;
											}
											q++;
										}
									}
									y++;
								}
							}
						}						
						z++;
					}
					k++;					
				}				
				if (checkHash != functionCounter){
					compilerError("No function block for function");
				}
				program.appendChild(new ProgramBlock(nowReading));
			}						
			readToken();
			ParseNode statementList = new StatementListNode(nowReading);
			if (!(program instanceof ProgramBlock)){
				int size = program.nChildren() - 1;
				program.child(size).appendChild(statementList);
			}
			else{
			program.appendChild(statementList);
			}
			while( nowReadingAStatementStart() ) {
				ParseNode statement = parse_statement();
				statementList.appendChild(statement);
				if (nowReading.getLexeme().equals("gorp")){
					ParseNode programEnd = new ProgramBlock(nowReading);
					program.appendChild(programEnd);
					readToken();
					endCheck = true;
					break;
				}
			}	
			expect(NullToken.class);
			if (endCheck == false){
				compilerError("Incorrect closing statement", nowReading.getLocation());
				return null;
			}
			else
			{
			return program;
			}
		}
		else
		{
			compilerError("Incorrect opening statement", nowReading.getLocation());
			return null;
		}		
	}


//	expressions:
//	level 0:	+
//	level 1:	*
//  level 2:	literals
	private ParseNode parse_expression() 
	{
		return parse_expression0_2();
	}
	private ParseNode binopNode(Token operatorToken, ParseNode left, ParseNode right) {
		ParseNode result = new BinaryOperatorNode(operatorToken);
		result.appendChild(left);
		result.appendChild(right);
		return result;
	}
	private ParseNode unopNode(Token operatorToken, ParseNode left) {
		ParseNode result = new UnaryNode(operatorToken);
		result.appendChild(left);
		return result;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////
// expression parsing
//////////////////////////////////////////////////////////////////////////////////////////
	private ParseNode parse_expression0_2() 
	{		
		ParseNode left = parse_expression0_1();
		while (true){
			if (nowReading.isOperator(Operator.OR))
			{
				while(nowReading.isOperator(Operator.OR)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression0_1();
					
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			else
			{
				break;
			}
		}
		return left;	
	}
	private ParseNode parse_expression0_1() 
	{		
		ParseNode left = parse_expression0_0();
		while (true){
			if (nowReading.isOperator(Operator.AND))
			{
				while(nowReading.isOperator(Operator.AND)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression0_0();
					
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			else
			{
				break;
			}
		}
		return left;	
	}	
	private ParseNode parse_expression0_0() 
	{		
		ParseNode left = parse_expression0();
		while (true){
			if (nowReading.isOperator(Operator.LESSTHAN))
			{
				while(nowReading.isOperator(Operator.LESSTHAN)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression0();
					
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			else if (nowReading.isOperator(Operator.LESSTHENEQUAL))
			{
				while(nowReading.isOperator(Operator.LESSTHENEQUAL)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression0();
					
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			else if (nowReading.isOperator(Operator.GREATERTHAN))
			{
				while(nowReading.isOperator(Operator.GREATERTHAN)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression0();
					
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			else if (nowReading.isOperator(Operator.GREATERTHANEQUAL))
			{
				while(nowReading.isOperator(Operator.GREATERTHANEQUAL)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression0();
					
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			else if (nowReading.isOperator(Operator.EQUAL))
			{
				while(nowReading.isOperator(Operator.EQUAL)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression0();
					
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			else if (nowReading.isOperator(Operator.NOTEQUAL))
			{
				while(nowReading.isOperator(Operator.NOTEQUAL)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression0();
					
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			else
			{
				break;
			}
		}
		return left;	
	}	
	// e0 -> e1 (ADD e1)*
	// e0 -> e1 (MINUS e1)*
	private ParseNode parse_expression0() 
	{				
		ParseNode left = parse_expression1();
		while (true){
		if (nowReading.isOperator(Operator.ADD))
		{
			while(nowReading.isOperator(Operator.ADD)) {
				Token opToken = nowReading;
				readToken();
				ParseNode right = parse_expression1();
				
				left = binopNode(opToken, left, right);
				continue;
			}
		}
		else if (nowReading.isOperator(Operator.MINUS))
		{
			while(nowReading.isOperator(Operator.MINUS)) {
				if (lastEaten instanceof OperatorToken && !(lastEaten.isOperator(Operator.CPARENTTHESIS))){
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression1();
					left = unopNode(opToken, right);
				}
				Token opToken = nowReading;
				readToken();
				ParseNode right = parse_expression1();
				
				left = binopNode(opToken, left, right);
				continue;
			}
		}
		else
		{
			break;
		}
		}
		return left;	
	}	
	// e1 -> e2 (MULTIPLY literal)*
	// e1 -> e2 (Divide literal)*
	private ParseNode parse_expression1() 
	{		
		ParseNode left = parse_expression2();
		while(true){
			if (nowReading.isOperator(Operator.MULTIPLY))
			{
				while(nowReading.isOperator(Operator.MULTIPLY)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression2();
		
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			if (nowReading.isOperator(Operator.DIVIDE))
			{
				while(nowReading.isOperator(Operator.DIVIDE)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression2();
		
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			if (nowReading.isOperator(Operator.MOD))
			{
				while(nowReading.isOperator(Operator.MOD)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_expression2();
		
					left = binopNode(opToken, left, right);
					continue;
				}
			}
			else
			{
				break;
			}			
		}
		return left;
	}
	
	private ParseNode parse_expression2() 
	{		
		while (nowReading.isOperator(Operator.MINUS)){
			ParseNode left;
			Token opToken = nowReading;
			readToken();
			if (nowReading.isOperator(Operator.MINUS)){
				left = parse_expression2();
			}
			else if (nowReading.isOperator(Operator.OPARENTTHESIS))
			{
				left = parse_expression3();
			}
			else
			{
				left = parse_literal();
			}
			ParseNode result = null;
			boolean passing = true;
			while (nowReading.isOperator(Operator.RESOLUTION)){
				if (passing == true){
					result = new UnaryNode(opToken);
					passing = false;
				}				
				opToken = nowReading;
				readToken();
				ParseNode right = parse_literal();
				left = binopNode(opToken, left, right);
				//left = unopNode(TokenMinus, left);
				result.appendChild(left);
				
			}
			if (result == null){
				left = unopNode(opToken, left);
				return left;
			}
			return result;	
			}		
		while (nowReading.isOperator(Operator.TOINT)){
			Token opToken = nowReading;
			readToken();
			ParseNode left = parse_expression0();
			ParseNode right = new NullNode(opToken);
			left = binopNode(opToken, left, right);
			return left;	
			}
		
		while (nowReading.isOperator(Operator.TOFLOAT)){
			Token opToken = nowReading;
			readToken();
			ParseNode left = parse_expression0();
			ParseNode right = new NullNode(opToken);
			left = binopNode(opToken, left, right);
			return left;	
			}	
		
		while (nowReading.isOperator(Operator.NEGATE)){
			Token opToken = nowReading;
			readToken();
			ParseNode left = parse_expression0();
			ParseNode right = new NullNode(opToken);
			left = binopNode(opToken, left, right);
			return left;	
			}
		while (nowReading.isOperator(Operator.CREATE)){
			Token opToken = nowReading;
			readToken();
			ParseNode left = parse_literal();
			ParseNode right = new NullNode(opToken);
			left = binopNode(opToken, left, right);
			return left;	
			}
		ParseNode left = parse_expression3();
		return left;
	}
	
	private ParseNode parse_expression3() 
	{	
		while (nowReading.isOperator(Operator.OPARENTTHESIS)){
			readToken();
			ParseNode left = parse_expression0_2();
			if (nowReading.isOperator(Operator.CPARENTTHESIS)){
				readToken();
				return left;
			}
		}
		ParseNode left = parse_expression4();
		if (nowReading.isOperator(Operator.OPARENTTHESIS)){
			ParseNode functionCall = new CallStatementNode(left);
			readToken();
			while (true){
				//ParseNode functionCall;				
				if (nowReading.isOperator(Operator.CPARENTTHESIS)){
					//functionCall = new CallStatementNode(left);
				}
				else{
					ParseNode left2 = parse_expression0_2();
					//functionCall = new CallStatementNode(left);
					functionCall.appendChild(left2);
				}				
				if (nowReading.isOperator(Operator.CPARENTTHESIS)){
					readToken();
					return functionCall;
				}
			}
		}
		return left;
	}
	private ParseNode parse_expression4() 
	{		
			ParseNode left = parse_literal();
			if (nowReading.isOperator(Operator.RESOLUTION))
			{
				while(nowReading.isOperator(Operator.RESOLUTION)) {
					Token opToken = nowReading;
					readToken();
					ParseNode right = parse_literal();
					left = binopNode(opToken, left, right);
					continue;
				}
			}
		return left;
	}
	
	
	// literal -> integerConstant | identifier |floatingConstant
	private ParseNode parse_literal() {
		Token token = nowReading;
		readToken();
		
		if(token instanceof IntegerConstantToken) {
			return new IntegerConstantNode(token);
		}
		if(token instanceof FloatingConstantToken) {
			return new FloatingConstantNode(token);
		}
		if(token instanceof BooleanToken) {
			return new BooleanNode(token);
		}
		if(token instanceof IdentifierToken) {
			return new IdentifierNode(token);
		}
		if(token instanceof NullTypeToken) {
			return new NullTypeNode(token);
		}

		syntaxError("expected literal but found " + lastEaten);	
		assert false;
		return null;
	}
	
	private boolean nowReadingAnExpressionStart() {
		return nowReading instanceof IntegerConstantToken ||
		       nowReading instanceof IdentifierToken ||
		       nowReading instanceof BooleanToken ||
		       nowReading instanceof FloatingConstantToken ||
		       nowReading.isOperator(Operator.OPARENTTHESIS) ||
		       nowReading.isOperator(Operator.RESOLUTION)	||
		       nowReading.isOperator(Operator.MINUS)||
		       nowReading.isOperator(Operator.NEGATE);
	}

	
//////////////////////////////////////////////////////////////////////////////////////////
// statement parsing
//////////////////////////////////////////////////////////////////////////////////////////


	private boolean nowReadingAStatementStart() {
		return nowReading instanceof IdentifierToken ||
			   nowReading instanceof KeywordToken ||
			   nowReading.isOperator(Operator.PRINT);
	}
	
	private ParseNode parse_statement() {
		if(nowReading instanceof KeywordToken) {
			if (nowReading.getLexeme() == "DO"){
				return parse_doStatement();
			}
			else if (nowReading.getLexeme() == "IF"){
				return parse_ifStatement();
			}
			else if (nowReading.getLexeme().equals("return")){
				//ParseNode returnExpression = new FunctionNode(nowReading);
				return parse_return();
			}
			return parse_declarationStatement();
		}
		if(nowReading instanceof IdentifierToken) {
			if (objectVector.contains(nowReading.getLexeme())){
				return parse_declarationStatement();
			}
			return parse_identifierStartedStatement();
		}		
		if(nowReading.isOperator(Operator.PRINT)) {
			return parse_printStatement();
		}	
		
		syntaxError("expected statement but found " + nowReading);
		return null;
	}
	private ParseNode parse_return(){
		readToken();
		Token assign = lastEaten;
		ParseNode result = new FunctionNode(assign);
		if (nowReading instanceof IntegerConstantToken || nowReading instanceof IdentifierToken || nowReading instanceof FloatingConstantToken ||
				nowReading instanceof BooleanToken || nowReading instanceof StringConstantToken || nowReading instanceof NullToken){
			ParseNode expression = parse_expression();
			result.appendChild(expression);
		}		
		return result;
	}
	private ParseNode parse_ifStatement() {
		readToken();
		ParseNode if_statement = null;
		ParseNode End_if_statement = null;
		ParseNode Else_if_statement = null;
		Token assign = lastEaten;
		ParseNode result = new IfStatementNode(assign);
		//expect(Operator.OPARENTTHESIS);
		if_statement = parse_expression0_2();
		//expect(Operator.CPARENTTHESIS);
		result.appendChild(if_statement);
		
		while(true){
			if_statement = parse_statement();
			result.appendChild(if_statement);
			if (nowReading.getLexeme() == "FI"){				
				End_if_statement = new NullNode(nowReading);
				readToken();
				break;
			}
			else if (nowReading.getLexeme() == "ELSE"){
				Else_if_statement = new IfStatementNode(nowReading);
				result.appendChild(Else_if_statement);
				readToken();
				while(true){
					if_statement = parse_statement();
					Else_if_statement.appendChild(if_statement);
					if (nowReading.getLexeme() == "FI"){
						End_if_statement = new NullNode(nowReading);
						readToken();
						break;
					}
				}
			}
			if (lastEaten.getLexeme() == "FI"){
				break;
			}
		}		
		result.appendChild(End_if_statement);
		return result;
	}
	
	private ParseNode parse_doStatement() {
		readToken();
		ParseNode do_statement = null;
		ParseNode loop_clause_statement = null;
		ParseNode End_do_statment = null;
		Token assign = lastEaten;
		ParseNode result = new DoStatementNode(assign);
		while(true){
		if (nowReading.getLexeme().equals("WHILE") || nowReading.getLexeme().equals("UNTIL")){
			ParseNode loop_statement = new DoStatementNode(nowReading);
			result.appendChild(loop_statement);
			readToken();
			//expect(Operator.OPARENTTHESIS);
			loop_clause_statement = parse_expression0_2();
			//expect(Operator.CPARENTTHESIS);
			loop_statement.appendChild(loop_clause_statement);
		}
			if (nowReading.getLexeme() == "OD"){
				End_do_statment = new DoStatementNode(nowReading);
				readToken();
				break;
			}
			else
			{
				do_statement = parse_statement();
				result.appendChild(do_statement);
			}
			
		}
		result.appendChild(End_do_statment);
		return result;
	}
	
	private ParseNode parse_identifierStartedStatement() {
		ParseNode identifier = parse_expression();
		ParseNode result = null;
		if(identifier instanceof CallStatementNode){
			result = new CallStatementNode(identifier);
			return result;
		}
		expect(Operator.ASSIGN);
		Token assign = lastEaten;
		ParseNode expression = parse_expression();
		
		
		if(identifier.getToken().isOperator(Operator.RESOLUTION)){
			result = new BinaryOperatorNode(identifier);
			ParseNode result_2 = new AssignmentStatementNode(assign);
			result.appendChild(result_2);
			result_2.appendChild(result.child(1));
			result_2.appendChild(expression);
			return result;
		}
		result = new AssignmentStatementNode(assign);
		result.appendChild(identifier);
		result.appendChild(expression);
		return result;
	}
	
	private ParseNode parse_declarationStatement() {
		Type type_assign = PrimitiveType.NULL;	
		
		if (nowReading.getLexeme() == "bool"){			
			type_assign = PrimitiveType.BOOLEAN;
		}
		else if (nowReading.getLexeme() == "int"){			
			type_assign = PrimitiveType.INTEGER;
		}
		else if (nowReading.getLexeme() == "float"){			
			type_assign = PrimitiveType.FLOAT;
		}
		else if (nowReading.getLexeme() == "string"){			
			type_assign = PrimitiveType.STRING;
		}
		else if (nowReading.getLexeme() == "@"){
			type_assign = PrimitiveType.AUTOTYPE;
		}
		else if (objectVector.contains(nowReading.getLexeme())){
			type_assign = ObjectType.OBJECT;
			
		}
		readToken();
		DeclarationNode declarationNode = new DeclarationNode(lastEaten);
		ParseNode identifier = parse_identifier();
		identifier.setType(type_assign);
		declarationNode.appendChild(identifier);
		
		if (nowReading.isOperator(Operator.ASSIGN)){
			expect(Operator.ASSIGN);
			Token assign = lastEaten;
			ParseNode expression = parse_expression();
			if (expression instanceof NullTypeNode){
				if (!(identifier.getType().equals(ObjectType.OBJECT))){
					compilerError("Null cannot be assigned to type " + identifier.getType());
				}
			}
			
			ParseNode result = new AssignmentStatementNode(assign);
			result.appendChild(declarationNode);
			result.appendChild(expression);
			result.setType(type_assign);
			return result;
		}
		
		Token assign = lastEaten;
		ParseNode result = new AssignmentStatementNode(assign);
		result.setType(type_assign);
		if (type_assign.equals(ObjectType.OBJECT)){
			Token nullToken;
			nullToken = NullTypeToken.make(lastEaten.getLocation(), "NULL");
			ParseNode nullAssign = new NullTypeNode(nullToken);
			identifier.appendChild(nullAssign);
		}
		result.appendChild(declarationNode);	
		
		return result;
	}
	
	
//////////////////////////////////////////////////////////////////////////////////////////
// print statement parsing
//////////////////////////////////////////////////////////////////////////////////////////

	private ParseNode parse_printStatement() {
		expect(Operator.PRINT);
		PrintStatementNode parent = new PrintStatementNode(lastEaten); 
		parse_printItemList(parent);
		parse_printStatementEnd(parent);
		return parent;
	}

	private void parse_printStatementEnd(PrintStatementNode parent) {
		if(nowReading.isOperator(Operator.NO_NEWLINE)) {
			parent.setNewline(false);
		}
		else if(nowReading.isOperator(Operator.PRINT)) {
			parent.setNewline(true);
		}
		else {
			syntaxError("Expected " + Operator.PRINT.getLexeme() + " or "
					                + Operator.NO_NEWLINE.getLexeme() +
					                " at the end of print statement.");
			return;
		}
		readToken();
	}
	
	private void parse_printItemList(ParseNode result) {
		while( nowReadingAPrintItemStart() ) {
			ParseNode printItem = parse_printItem();
			result.appendChild(printItem);
		}
	}
	
	private ParseNode parse_printItem() {
		if(nowReading instanceof StringConstantToken) {
			return parse_stringConstant();
		}
		else if (nowReadingAnExpressionStart()) {
			return parse_expression();
		}
		else {
			compilerError("no printItem to be found");
			assert false;
			return null;
		}
	}
	private boolean nowReadingAPrintItemStart() {
		return nowReading instanceof StringConstantToken ||
		       nowReadingAnExpressionStart();
	}
//////////////////////////////////////////////////////////////////////////////////////////
// eat an expected token
//////////////////////////////////////////////////////////////////////////////////////////

	private void expect(Class<? extends Token> tokenClass) {
		if(!tokenClass.isInstance(nowReading)) {
			syntaxError("expected " + tokenClass.getName() + " but found " + nowReading);
			assert false;
		}
		readToken();
			
	}
	private void expect(Operator operator) {
		if(!nowReading.isOperator(operator)) {
			syntaxError("expected " + operator + " but found " + nowReading);
			assert false;
		}
		readToken();
	}


//////////////////////////////////////////////////////////////////////////////////////////
// parse a node that corresponds directly to a single token type.
// to make things like parse_identifier simple, there's a bunch of
// hairy underlying code that uses reflection.
//////////////////////////////////////////////////////////////////////////////////////////
	
	private ParseNode parse_identifier() {
		return parse_uniformNamedNode("Identifier");
	}
	private ParseNode parse_stringConstant() {
		return parse_uniformNamedNode("StringConstant");
	}
	
	@SuppressWarnings("unchecked")
	private ParseNode parse_uniformNamedNode(String baseName) {
		try {
			String tokenClassName = "tokens." + baseName + "Token";
			String nodeClassName  = "parseTree." + baseName + "Node";	

			Class<? extends Token>     tokenClass = (Class<? extends Token>) Class.forName(tokenClassName);
			Class<? extends ParseNode> nodeClass  = (Class<? extends ParseNode>) Class.forName(nodeClassName);
			return parse_singleTokenNode(tokenClass, nodeClass);
		} catch(Exception e) {
			compilerError("Problem with uniform naming convention for base " + baseName);
			return null;
		}
	}
	private ParseNode parse_singleTokenNode(Class<? extends Token> tokenClass,
											Class<? extends ParseNode> nodeClass) {
		expect(tokenClass);
		return makeLastEatenInstanceOf(nodeClass);
	}
	private ParseNode makeLastEatenInstanceOf(Class<? extends ParseNode> nodeClass) {
		Constructor<? extends ParseNode> nodeConstructor = tokenArgumentConstructor(nodeClass);
		ParseNode result = getTokenInstance(nodeClass, nodeConstructor);
		return result;
	}
	private ParseNode getTokenInstance(Class<? extends ParseNode> nodeClass, Constructor<? extends ParseNode> nodeConstructor) {
		try {
			return nodeConstructor.newInstance(lastEaten);
		} catch (Exception e) {
			compilerError("unexpected inability to instantiate " + nodeClass.getName() + "(Token)");
			return null;
		}
	}
	private Constructor<? extends ParseNode> tokenArgumentConstructor(Class<? extends ParseNode> nodeClass) {
		try {
			Class<?>[] parameterTypes = {Token.class};
			return nodeClass.getConstructor(parameterTypes);
		} catch (Exception e) {
			compilerError("unexpected lack of constructor " + nodeClass.getName() + "(Token)");
			return null;
		}
	}


//////////////////////////////////////////////////////////////////////////////////////////
// error (non-)handling
//////////////////////////////////////////////////////////////////////////////////////////

	private void compilerError(String msg) {
		compilerError(msg, nowReading.getLocation());
	}
	private void compilerError(String msg, TextLocation location) {
		Error.reportCompilerError(location.toString() + ": " + msg);
	}


	private void syntaxError(String msg) {
		syntaxError(msg, nowReading.getLocation());
	}
	private void syntaxError(String msg, TextLocation location) {
		Error.reportError(location.toString() + ": " + msg);
	}

}
