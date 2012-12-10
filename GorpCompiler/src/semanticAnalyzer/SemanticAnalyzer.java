package semanticAnalyzer;

import parseTree.*;
import symbolTable.Binding;
import symbolTable.Scope;
import symbolTable.Scopes;
import tokens.*;
import errorHandler.Error;
import java.util.*;

public class SemanticAnalyzer {
	ParseNode ASTree;
	Hashtable<String, String> object_defs = new Hashtable<String, String>();
	Hashtable<String, ParseNode> parse_trees = new Hashtable<String, ParseNode>();
	Vector<String> object_types = new Vector<String>();
	
	Hashtable<String, ParseNode> function_parse_trees = new Hashtable<String, ParseNode>();
	Vector<String> function_types = new Vector<String>();
	Vector<String> function_name = new Vector<String>();
	Vector<String> declarations = new Vector<String>();
	Vector<ParseNode> object_fun_def = new Vector<ParseNode>();
	Vector<String> object_fun_name = new Vector<String>();
	
	public SemanticAnalyzer(ParseNode ASTree) {
		this.ASTree = ASTree;
	}
	
	public ParseNode analyze() {
		TypecheckVisitor visitor = new TypecheckVisitor();
		ASTree.accept(visitor);
		return ASTree;
	}

	
	// TypecheckVisitor also handles variable binding.
	// therefore it should also handle scopes.
	public class TypecheckVisitor extends ParseNodeVisitor.DefaultPostorder {

		///////////////////////////////////////////////////////////////////////////
		// bigger-than-statement constructs
		//
		@Override
		public boolean visitEnter(StatementListNode node) {
			Scopes.enterScope(node);
			return true;
		}
		@Override
		public boolean visitLeave(StatementListNode node) {
			Scopes.leaveScope();
			return true;
		}
		
		public boolean visitEnter(ExpressionList node) {
			//Scopes.enterScope(node);
			return true;
		}
		@Override
		public boolean visitLeave(ExpressionList node) {
			//Scopes.leaveScope();
			return true;
		}

		///////////////////////////////////////////////////////////////////////////
		// statements other than assign/declare
		//

		@Override
		public boolean visitLeave(PrintStatementNode node) {
			node.setType(PrimitiveType.UNDEFINED);
			return true;
		}
		public boolean visitEnter(ObjectNode node) {
			int i = 0;
			while (i < node.nChildren()){
				if (node.child(i) instanceof ObjectNode && node.getToken() instanceof KeywordToken){
					if (parse_trees.contains(node.child(i).getToken().getLexeme())){
						return true;
					}
					else
					{
						parse_trees.put(node.child(i).getToken().getLexeme(), (ParseNode)node.child(i));
					}					
				}
				i++;
			}
			return true;
		}
		
		public boolean visitLeave(FunctionNode node){
			if (node.getToken().getLexeme().equals("return")){
				if (node.nChildren() > 0){
					if (node.child(0) instanceof BinaryOperatorNode){
						return true;
					}
					if(node.child(0).getType() == null){
						node.child(0).setType(PrimitiveType.NULL);
						node.setType(PrimitiveType.NULL);
					}
					else
					{
						node.setType(node.child(0).getType());
					}
				}
			}
			return true;
		}
		
		public boolean visitEnter(FunctionNode node) {
			if (node.getToken().getLexeme().equals("return")){
				ParseNode tempNode = node;
				while (true){
					if (tempNode.getParent() instanceof FunctionNode){
						String indentifier = tempNode.getParent().getToken().getLexeme();
						String function_type = function_types.get(function_name.indexOf(indentifier));
						if (function_type == "void"){
							if (node.nChildren() > 0){
								functionReturnError(node);
								return true;
							}
							else {
								break;
							}
						}
						else{
							if (node.nChildren() == 0){
								noReturnType(node, function_type);
								return true;
							}
							else
							{
								break;
							}
						}
					}
					tempNode = tempNode.getParent();				
				}
			}
			if (node.getToken().getLexeme().equals("function")){
				if (node.nChildren() > 0){
					if (node.child(0) instanceof ObjectNode){
						return true;
					}
					String args = "";
					int p = 0;
					if (node.child(0).nChildren() > 1){
						if (node.child(0).child(1) instanceof ExpressionList){
							while (p < node.child(0).child(1).nChildren()){
							args = args + "-" + node.child(0).child(1).child(p).child(0).getToken().getLexeme();
							p++;
							}
						}
					}
					int s = 1;
					if (node.child(0).nChildren() > 1){
						if (!(node.child(0).child(1) instanceof ExpressionList)){
							while (s < node.child(0).nChildren()){
								args = args + "-" + node.child(0).child(s).child(0).getToken().getLexeme();
								s++;
							}
						}
					}
					String ident = node.child(0).getToken().getLexeme() + args;
					function_parse_trees.put(ident, node.child(0));
				}
				return true;
			}
			ParseNode tempNode = node;
			while (true){
				if (tempNode.getParent() instanceof ObjectNode){
					return true;
				}
				if (tempNode.getParent() instanceof ObjectBlock){
					return true;
				}
				if (tempNode.getParent() instanceof ProgramNode){
					break;
				}
				tempNode = tempNode.getParent();					
			}
			String args = "";
			int p = 0;
			if (node.nChildren() > 1){
				if (node.child(1) instanceof ExpressionList){
					while (p < node.child(1).nChildren()){
					args = args + "-" + node.child(1).child(p).child(0).getToken().getLexeme();
					p++;
					}
				}
			}
			String ident = node.getToken().getLexeme() + args;
			function_parse_trees.put(ident, node);
			return true;
		}

		public boolean visitEnter(ObjectBlock node) {
			object_types.add(node.getParent().getToken().getLexeme());
			parse_trees.put(node.getParent().getToken().getLexeme(), (ParseNode)node);
			Scopes.enterObjectScope(node);
			return true;
		}
		public boolean visitLeave(ObjectBlock node) {
			declarations.clear();
			Scopes.leaveScope();
			return true;
		}
		
		public boolean visitEnter(CallStatementNode node){
			if (node.getToken().isOperator(Operator.RESOLUTION)){
				if (!(function_parse_trees.containsKey(node.child(1).getToken().getLexeme()))){
					String args = "";
					int p = 2;
					if (node.nChildren() > 2){
						while (p < node.nChildren()){
							if (node.child(p) instanceof IntegerConstantNode || (node.child(p) instanceof IdentifierNode && node.child(p).getType().equals(PrimitiveType.INTEGER))){
								args = args + "-int";
							}
							else if (node.child(p) instanceof BooleanNode || (node.child(p) instanceof IdentifierNode && node.child(p).getType().equals(PrimitiveType.BOOLEAN))){
								args = args + "-bool";
							}
							else if (node.child(p) instanceof FloatingConstantNode || (node.child(p) instanceof IdentifierNode && node.child(p).getType().equals(PrimitiveType.FLOAT))){
								args = args + "-float";
							}
							p++;
						}					
					}
					String ident = node.child(1).getToken().getLexeme() + args;
					if (!(function_parse_trees.containsKey(ident))){
						undefinedFunction(node);
					}
				}
			}
			else if (!(function_parse_trees.containsKey(node.getToken().getLexeme()))){
				String args = "";
				int p = 0;
				if (node.nChildren() > 0){
					while (p < node.nChildren()){
						if (node.child(p) instanceof IntegerConstantNode || (node.child(p) instanceof IdentifierNode && node.child(p).getType().equals(PrimitiveType.INTEGER))){
							args = args + "-int";
						}
						else if (node.child(p) instanceof BooleanNode || (node.child(p) instanceof IdentifierNode && node.child(p).getType().equals(PrimitiveType.BOOLEAN))){
							args = args + "-bool";
						}
						else if (node.child(p) instanceof FloatingConstantNode || (node.child(p) instanceof IdentifierNode && node.child(p).getType().equals(PrimitiveType.FLOAT))){
							args = args + "-float";
						}
						p++;
					}					
				}
				String ident = node.getToken().getLexeme() + args;
				if (!(function_parse_trees.containsKey(ident))){
					undefinedFunction(node);
				}				
			}
			if (node.getParent() instanceof BinaryOperatorNode){
				ParseNode tempNode = node.getParent();
				while(true){
					if (tempNode instanceof AssignmentStatementNode){
						if (tempNode.child(0) instanceof DeclarationNode){
							String type_assign = tempNode.child(0).getToken().getLexeme();
							String identifier = node.getToken().getLexeme();
							int index = function_name.indexOf(identifier);
							String type = function_types.get(index);
							if (type_assign.equals(type)){
								return true;
							}
							else
							{
								incorrectTypeReturn(node, type_assign);
							}
						}
					}	
					if (tempNode instanceof ProgramBlock){
						return true;
					}
					if (tempNode instanceof FunctionBlock){
						return true;
					}
					if (tempNode instanceof FunctionNode){
						return true;
					}
					if (tempNode instanceof ProgramNode){
						return true;
					}
					tempNode = tempNode.getParent();
				}
			}
			if (node.getParent() instanceof AssignmentStatementNode){
				
			}
			return true;
		}
		
		public boolean visitLeave(CallStatementNode node) {
			String identifier = null;
			if (node.getToken().isOperator(Operator.RESOLUTION)){
				identifier = node.child(1).getToken().getLexeme();
				int index = function_name.indexOf(identifier);
				String type = function_types.get(index);
				if (type.equals("void")){
					node.setType(PrimitiveType.VOID);
					node.child(1).setType(PrimitiveType.VOID);
				}
				int p = 2;
				String args = "";
				if (node.nChildren() > 0){
					while (p < node.nChildren()){
						if (node.child(p).getType().equals(PrimitiveType.INTEGER)){
							args = args + "-int";
						}
						else if (node.child(p).getType().equals(PrimitiveType.FLOAT)){
							args = args + "-float";
						}
						else if (node.child(p).getType().equals(PrimitiveType.BOOLEAN)){
							args = args + "-bool";
						}
						p++;
					}
				}
				identifier = node.child(1).getToken().getLexeme() + args;
			}
			else
			{
				int p = 0;
				String args = "";
				if (node.nChildren() > 0){
					while (p < node.nChildren()){
						if (node.child(p).getType().equals(PrimitiveType.INTEGER)){
							args = args + "-int";
						}
						else if (node.child(p).getType().equals(PrimitiveType.FLOAT)){
							args = args + "-float";
						}
						else if (node.child(p).getType().equals(PrimitiveType.BOOLEAN)){
							args = args + "-bool";
						}
						p++;
					}
				}
				identifier = node.getToken().getLexeme() + args;
			}
			ParseNode tree = function_parse_trees.get(identifier);
			String fun_type = tree.child(0).getToken().getLexeme();
			if (fun_type.equals("int")){
				node.setType(PrimitiveType.INTEGER);
			}
			else if (fun_type.equals("float")){
				node.setType(PrimitiveType.FLOAT);
			}
			else if (fun_type.equals("bool")){
				node.setType(PrimitiveType.BOOLEAN);
			}
			ParseNode tempNode = node;
			if (tree.nChildren() < 1){
				return true;
			}
			if (tree.nChildren() == 1){
				int childrenCount = tempNode.nChildren();
				int m = 0;
				int functionExpression = tree.nChildren() - 1;
				while(m < childrenCount){
					String type = tempNode.child(m).getType().toString().toLowerCase();
					if (tree.child(functionExpression).child(0).getToken().getLexeme().equals("int")){
						if (type.equals("integer")){
								m++;
								functionExpression++;
								continue;
						}
						else
						{
							functionArgumentError(node);
							m++;
							functionExpression++;
						}							
					}
					else if (tree.child(functionExpression).child(0).getToken().getLexeme().equals("float")){
						if (type.equals("float")){
							m++;
							functionExpression++;
							continue;
					}
					else
					{
						functionArgumentError(node);
						m++;
						functionExpression++;
					}							
				}
					else if (tree.child(functionExpression).child(0).getToken().getLexeme().equals("bool")){
						if (type.equals("boolean")){
							m++;
							functionExpression++;
							continue;
					}
					else
					{
						functionArgumentError(node);
						m++;
						functionExpression++;
					}							
				}
						
				}
			}
			if (tree.child(1) instanceof ExpressionList){
				int childrenCount = tempNode.nChildren();
				int m = 0;
				if (node.getToken().isOperator(Operator.RESOLUTION)){
					m = m + 2;
				}
				int functionExpression = tree.child(1).nChildren() - 1;
				if (functionExpression == -1){
					return true;
				}
				while(m < childrenCount){
					String type = tempNode.child(m).getType().toString().toLowerCase();
					if (tree.child(1).child(functionExpression).child(0).getToken().getLexeme().equals("int")){
						if (type.equals("integer")){
								m++;
								functionExpression++;
								continue;
						}
						else
						{
							functionArgumentError(node);
							m++;
							functionExpression++;
						}							
					}
					else if (tree.child(1).child(functionExpression).child(0).getToken().getLexeme().equals("float")){
						if (type.equals("float")){
							m++;
							functionExpression++;
							continue;
					}
					else
					{
						functionArgumentError(node);
						m++;
						functionExpression++;
					}							
				}
					else if (tree.child(1).child(functionExpression).child(0).getToken().getLexeme().equals("bool")){
						if (type.equals("boolean")){
							m++;
							functionExpression++;
							continue;
					}
					else
					{
						functionArgumentError(node);
						m++;
						functionExpression++;
					}							
				}
						
				}
			}
			else
			{
				if (tempNode.nChildren() == 0){
					return true;
				}
				else
				{
					int childrenCount = tempNode.nChildren();
					int m = 0;
					int functionExpression = 1;
					while(m < childrenCount){
						String type = tempNode.child(m).getType().toString().toLowerCase();
						if (tree.child(functionExpression).child(0).getToken().getLexeme().equals("int")){
							if (type.equals("integer")){
									m++;
									functionExpression++;
									continue;
							}
							else
							{
								functionArgumentError(node);
								m++;
								functionExpression++;
							}							
						}
						else if (tree.child(functionExpression).child(0).getToken().getLexeme().equals("float")){
							if (type.equals("float")){
								m++;
								functionExpression++;
								continue;
						}
						else
						{
							functionArgumentError(node);
							m++;
							functionExpression++;
						}							
					}
						else if (tree.child(functionExpression).child(0).getToken().getLexeme().equals("bool")){
							if (type.equals("boolean")){
								m++;
								functionExpression++;
								continue;
						}
						else
						{
							functionArgumentError(node);
							m++;
							functionExpression++;
						}							
					}
							
					}
				}
			}
			return true;
		}
		
		public boolean visitEnter(FunctionBlock node) {
			if (node.getToken().getLexeme().equals("NUF")){
				return true;
			}
			if (node.getToken().getLexeme().equals("return")){
				return true;
			}
			ParseNode tempNode = node;
			while(true){
				if (tempNode.getParent() instanceof ObjectBlock){
					object_fun_def.add(tempNode.child(0));
					object_fun_name.add(tempNode.getParent().getParent().getToken().getLexeme());
				}				
				if (tempNode.getParent() instanceof ProgramNode){
					break;
				}
				tempNode = tempNode.getParent();
			}
			function_types.add(node.child(0).child(0).getToken().getLexeme());
			function_name.add(node.child(0).getToken().getLexeme());
			int g = 0;
			boolean passing = true;
			while (g < object_fun_def.size()){
				ParseNode parse_tree = object_fun_def.get(g);
				if (parse_tree.getToken().getLexeme().equals(node.child(0).getToken().getLexeme())){
					ParseNode tempNode3 = node;
					while(true){
						if (tempNode3.getParent() instanceof ObjectNode){
							String object_name = tempNode3.getParent().getToken().getLexeme();
							int index_object = object_fun_name.indexOf(object_name);
							String name_fun = object_fun_def.get(index_object).getToken().getLexeme();
							if (name_fun == parse_tree.getToken().getLexeme()){
								passing = false;
							}
							else
							{
								incorrectFunction(node);
							}
						}				
						if (tempNode3.getParent() instanceof ProgramNode){
							break;
						}
						tempNode3 = tempNode3.getParent();
					}
					
				}
				g++;
			}
			if (function_parse_trees.containsKey((node.child(0).getToken().getLexeme()))){
				if (passing == false){
					
				}
				else
				{
					ParseNode tree = function_parse_trees.get(node.child(0).getToken().getLexeme());
					if (tree.child(0).getToken().getLexeme().equals(node.child(0).child(0).getToken().getLexeme())){
						
					}
					else{
						incorrectFunction(node);
					}
					if ((tree.nChildren() - 1) != node.child(0).child(1).nChildren()){
						incorrectFunction(node);
					}
					
				}
				String args = "";
				int p = 0;
				if (node.child(0).nChildren() > 1){
					if (node.child(0).child(1) instanceof ExpressionList){
						while (p < node.child(0).child(1).nChildren()){
						args = args + "-" + node.child(0).child(1).child(p).child(0).getToken().getLexeme();
						p++;
						}
					}
				}
				String ident = node.child(0).getToken().getLexeme() + args;
				function_parse_trees.put(ident, node);
			}
			//function_parse_trees.put(node.child(0).getToken().getLexeme(), node);
			Scopes.enterScope(node);
			return true;
		}
		public boolean visitLeave(FunctionBlock node) {
			if (node.getToken().getLexeme().equals("NUF")){
				return true;
			}
			else if (node.getToken().getLexeme().equals("return")){
				return true;
			}
			Scopes.leaveScope();
			return true;
		}

		///////////////////////////////////////////////////////////////////////////
		// assignment / variable declaration
		//
		@Override  // note complex interaction with visit(IdentifierNode)
		
		public boolean visitEnter(AssignmentStatementNode node){
			if (node.nChildren() > 1){
				String identifier = null;
				if (node.child(1) instanceof CallStatementNode){
					if (node.child(1).getToken().isOperator(Operator.RESOLUTION)){
						identifier = node.child(1).child(1).getToken().getLexeme();
					}
					else
					{	
						identifier = node.child(1).getToken().getLexeme();
					}
					int index = function_name.indexOf(identifier);
					String node_type = null;
					if (node.child(0) instanceof DeclarationNode){
						if (node.getType().equals(PrimitiveType.INTEGER)){
							node_type = "int";
						}
						else if (node.getType().equals(PrimitiveType.FLOAT)){
							node_type = "float";
						}
						else if (node.getType().equals(PrimitiveType.BOOLEAN)){
							node_type = "bool";
						}
						String type = function_types.get(index);
						if (node_type.equals(type)){
							return true;
						}
						else
						{
							incorrectTypeReturn(node.child(1), node_type);
						}
					}
				}
			}
			return true;
		}
		public boolean visitLeave(AssignmentStatementNode node) {
			IdentifierNode identifierNode;
			boolean skip = false;
			if (node.nChildren() > 1){
				String identifier = null;
				if (node.child(1) instanceof CallStatementNode){
					if (node.child(1).getToken().isOperator(Operator.RESOLUTION)){
						identifier = node.child(1).child(1).getToken().getLexeme();
						if (node.child(0).getToken().getLexeme().equals("int")){
							node.child(0).setType(PrimitiveType.INTEGER);
						}
						else if (node.child(0).getToken().getLexeme().equals("float")){
							node.child(0).setType(PrimitiveType.FLOAT);
						} 
						else if (node.child(0).getToken().getLexeme().equals("bool")){
							node.child(0).setType(PrimitiveType.BOOLEAN);
						} 
					}
					else
					{
						identifier = node.child(1).getToken().getLexeme();
					}
					int index = function_name.indexOf(identifier);
					String node_type = null;
					if (node.child(0) instanceof DeclarationNode){
						if (node.child(1).getType().equals(PrimitiveType.INTEGER)){
							node.setType((PrimitiveType.INTEGER));
							node_type = "int";
						}
						else if (node.child(1).getType().equals(PrimitiveType.FLOAT)){
							node.setType((PrimitiveType.FLOAT));
							node_type = "float";
						}
						else if (node.child(1).getType().equals(PrimitiveType.BOOLEAN)){
							node.setType((PrimitiveType.BOOLEAN));
							node_type = "bool";
						}
					}
					else if (node.child(0).getType().equals(PrimitiveType.INTEGER)){
						node.setType((PrimitiveType.INTEGER));
						node_type = "int";
					}
					else if (node.child(0).getType().equals(PrimitiveType.FLOAT)){
						node.setType((PrimitiveType.FLOAT));
						node_type = "float";
					}
					else if (node.child(0).getType().equals(PrimitiveType.BOOLEAN)){
						node.setType((PrimitiveType.BOOLEAN));
						node_type = "bool";
					}
					String type = function_types.get(index);
					if (node.child(1).getToken().isOperator(Operator.RESOLUTION)){
						if (type.equals("int")){
							node.child(1).child(1).setType(PrimitiveType.INTEGER);
							node.child(1).setType(PrimitiveType.INTEGER);
						}
						else if (type.equals("float")){
							node.child(1).child(1).setType(PrimitiveType.FLOAT);
							node.child(1).setType(PrimitiveType.FLOAT);
						}
						else if (type.equals("bool")){
							node.child(1).child(1).setType(PrimitiveType.BOOLEAN);
							node.child(1).setType(PrimitiveType.BOOLEAN);
						}
					}
					if (node_type.equals(type)){
						//return true;						
					}
					else
					{
						incorrectTypeReturn(node.child(1), node_type);
					}
				}
			}
			
			if (node.child(0).nChildren() == 0 || node.child(0).child(0).getToken().getLexeme().equals("this")){
				node.getParent();
				ParseNode temp = node;
				if (temp.child(0) instanceof UnaryNode){
							int y = 0;
							while (y < temp.nChildren()){
								if (temp.child(0) instanceof UnaryNode){
									temp = temp.child(0);
									y = 0;
									continue;
								}
								if (temp.nChildren() == 2)
								{
									    if (temp.child(1) instanceof UnaryNode){
									    	temp = temp.child(1);
										y = 0;
										continue;
									}
								}
								y++;
							}					
				}
				node.setType(temp.child(0).getType());
				if (node.nChildren() == 2){
					if(node.child(1) instanceof NullTypeNode){
						if (node.child(0).getType().equals(ObjectType.OBJECT)){
							String object_type = object_defs.get(node.child(0).getToken().getLexeme());
							node.child(0).setObject(object_type);
							node.setObject(object_type);
							node.setType(ObjectType.OBJECT);
							node.child(1).setType(ObjectType.OBJECT);	
							node.child(1).setObject(object_type);
						}
						else
						{
							typeCheckError(node, node.child(0), node.child(1));
						}
					}
				}
				identifierNode = (IdentifierNode)(node.child((AssignmentStatementNode.LHS)));
			}
			else
			{
				identifierNode = (IdentifierNode)(node.child(0).child((AssignmentStatementNode.LHS)));
			}
			if (identifierNode.getParent().getParent().getToken().isOperator(Operator.ASSIGN) && !(identifierNode.getType().equals(PrimitiveType.UNDEFINED)))
			{
				skip = true;
			}
			if (node.nChildren() > 1){
				if (node.child(0).nChildren() == 0 || node.child(0).child(0).getToken().getLexeme().equals("this")){
					if (node.child(1).getType() != node.child(0).getType() && !(node.child(0).getType().equals(PrimitiveType.UNDEFINED))){
						typeCheckError(node, node.child(0), node.child(1));
					}
				}
				else if (node.child(1).getType() != node.child(0).child(0).getType() && !(node.child(0).child(0).getType().equals(PrimitiveType.UNDEFINED))){
					typeCheckError(node, node.child(0), node.child(1));
				}
			}
			if (skip == false)
			{
				if (node.child(0).nChildren() == 0 || node.child(0).child(0).getToken().getLexeme().equals("this")){
					if (identifierNode.getParent().getType().equals(PrimitiveType.INTEGER)){				
						if (node.getParent().getToken().isOperator(Operator.RESOLUTION)){							
						}
						else
						{
						defineVariableIfNecessary(identifierNode, node.getType());
						}
					}
					else if (identifierNode.getParent().getType().equals(PrimitiveType.FLOAT)){	
						if (node.getParent().getToken().isOperator(Operator.RESOLUTION)){
							
						}
						else
						{
						defineVariableIfNecessary(identifierNode, node.getType());
						}
					}
					else if (identifierNode.getParent().getType().equals(PrimitiveType.BOOLEAN)){				
						defineVariableIfNecessary(identifierNode, node.getType());
					}
				}
				else
				{
					if (identifierNode.getParent().getParent().getType().equals(PrimitiveType.INTEGER)){				
						defineVariableIfNecessary(identifierNode, node.getType());
					}
					else if (identifierNode.getParent().getParent().getType().equals(PrimitiveType.FLOAT)){				
						defineVariableIfNecessary(identifierNode, node.getType());
					}
					else if (identifierNode.getParent().getParent().getType().equals(PrimitiveType.BOOLEAN)){				
						defineVariableIfNecessary(identifierNode, node.getType());
					}
				}
			}
			if (node.getType() == PrimitiveType.AUTOTYPE){
				
				ParseNode expressionNode = node.child(1);			
				requireIdentifiersAreDefined(expressionNode);
				Type exprType = expressionNode.getType();
				defineVariableIfNecessary(identifierNode, exprType);
				node.setType(exprType);
			}
			else if (node.getType() == null){
				if (identifierNode.getType().equals(PrimitiveType.FLOAT))
				{
					node.setType(PrimitiveType.FLOAT);
				}
				else if (identifierNode.getType().equals(PrimitiveType.INTEGER))
				{
					node.setType(PrimitiveType.INTEGER);
				}
				else if (identifierNode.getType().equals(PrimitiveType.BOOLEAN))
				{
					node.setType(PrimitiveType.BOOLEAN);
				}
				else
				{
					assert false;
				}
			}			
			
			if (node.getType().equals(ObjectType.OBJECT)){
				if (node.nChildren() == 2){
					if (node.child(0).nChildren() == 1 || node.child(1).nChildren() == 1){
						if (node.child(0).nChildren() == 0 || node.child(0).child(0).getToken().getLexeme().equals("this"))
						{
							defineVariableIfNecessary(identifierNode, ObjectType.OBJECT);
							identifierNode.setType(ObjectType.OBJECT);
							node.setObject(node.child(1).child(0).getToken().getLexeme());
							object_defs.put(identifierNode.getToken().getLexeme(), node.child(1).child(0).getToken().getLexeme());
							return true;
						}
						else if (node.child(0).child(0) instanceof NullTypeNode){
							
					}
						defineVariableIfNecessary(identifierNode, ObjectType.OBJECT);
						identifierNode.setType(ObjectType.OBJECT);
						node.setObject(node.child(0).getToken().getLexeme());
						object_defs.put(identifierNode.getToken().getLexeme(), node.child(0).getToken().getLexeme());
						return true;
					}
				}
				if (node.nChildren() == 1){
					if (node.child(0).child(0).child(0) instanceof NullTypeNode){	
						ParseNode new_null = new NullTypeNode(node.child(0).child(0).child(0));
						node.child(0).child(0).removeLastChild();
						node.appendChild(new_null);
						defineVariableIfNecessary(identifierNode, ObjectType.OBJECT);
						node.child(0).child(0).setType(ObjectType.OBJECT);
						node.child(0).child(0).setObject(node.child(0).getToken().getLexeme());
						object_defs.put(identifierNode.getToken().getLexeme(), node.child(0).getToken().getLexeme());
						return true;
					}
				}				
			}
			if (node.nChildren() > 1)
			{
				if (node.child(0).getType() == null || node.child(1).getType() == null)
				{
					
				}
				else if(function_types.contains(object_defs.get(node.child(0).getToken().getLexeme()))){
					if (node.child(1).nChildren() > 1){
						String functionCall = node.child(1).child(1).getToken().getLexeme();
						if (function_name.contains(functionCall)){
							if (function_types.indexOf(object_defs.get(node.child(0).getToken().getLexeme())) == function_name.indexOf(functionCall)){
								if (node.child(1).child(0).getType().equals(ObjectType.OBJECT)){
									IdentifierNode this_node = new IdentifierNode(node.child(1).child(0).getToken());
									node.child(1).appendChild(this_node);
									IdentifierNode that_node = (IdentifierNode)node.child(1).child(0);
									this_node.setBinding(that_node.getBinding());
								}
							}
							else
							{
								typeCheckError(node, node.child(0), node.child(1));
							}
						}
					}
				}
				else if(node.child(0).getType().equals(ObjectType.OBJECT) && node.child(1).getType().equals(ObjectType.OBJECT)){
					String child_1 = object_defs.get(node.child(0).getToken().getLexeme());
					String child_2 = object_defs.get(node.child(1).getToken().getLexeme());
					if(!(child_1.equals(child_2))){
						typeCheckError(node, node.child(0), node.child(1));
					}

				}
			}
			identifierNode.setType(node.getType());
			if(object_types.contains(identifierNode.getToken().getLexeme())){
				if (!(identifierNode.getType().equals(ObjectType.OBJECT))){
					typeCheckError(node, identifierNode, identifierNode);
				}
			}
			return true;
		}
		
		private void defineVariableIfNecessary(IdentifierNode identifierNode, Type exprType) {
			if(isUndefined(identifierNode.getType())) {
				addBinding(identifierNode, exprType);
			}
			//else
			//{
			//	addBinding(identifierNode, exprType);
			//}
		}

		private void addBinding(IdentifierNode identifierNode, Type type) {
			Scope scope = identifierNode.getLocalScope();
			Binding binding = scope.createBinding(identifierNode, type);
			identifierNode.setBinding(binding);
		}
		
		private void getBinding(IdentifierNode identifierNode, ParseNode object_block, Type type){
			Scope scope = object_block.child(0).child(0).getLocalScope();
			symbolTable.SymbolTable symbol = scope.getSymbolTable();
			if(symbol.containsKey(identifierNode.getToken().getLexeme())){
				Binding binding = symbol.lookup(identifierNode.getToken().getLexeme());
				identifierNode.setType(binding.getType());
				identifierNode.setBinding(binding);
			}
		}
		///////////////////////////////////////////////////////////////////////////
		// do statement / if
		//
		@Override
		public boolean visitEnter(DoStatementNode node){	
			Scopes.enterScope(node);
			return true;
		}
		public boolean visitLeave(DoStatementNode node){	
			Scopes.leaveScope();
			return true;
		}
		public boolean visitEnter(IfStatementNode node){	
			Scopes.enterScope(node);
			if (node.getToken().getLexeme().equals("ELSE")){
				return true;
			}
			else if (node.getToken().getLexeme().equals("FI")){
				return true;
			}
			if (!(node.child(0) instanceof BinaryOperatorNode)){
				if (!(node.child(0) instanceof BooleanNode)){
					if (!(node.child(0) instanceof IdentifierNode))
				operatorTypeCheckError(node, false, "clause is not in the correct format");
				}
			}
			return true;			
		}
		public boolean visitLeave(IfStatementNode node){	
			Scopes.leaveScope();
			return true;			
		}
		///////////////////////////////////////////////////////////////////////////
		// operators
		//
		@Override
		public boolean visitLeave(BinaryOperatorNode node) {
			assert node.nChildren() == 2;
			if (node.getToken().isOperator(Operator.RESOLUTION)){
				if (node.child(0) instanceof FunctionNode){
					return true;
				}
			}
			ParseNode left = node.child(0);
			ParseNode right = node.child(1);
			if (right instanceof UnaryNode){
				right = getChildNegative(right);
				if (node.child(0).child(0).getToken().isOperator(Operator.RESOLUTION)){
					node.setType(node.child(0).child(0).child(1).getType());
				}
			}
			if (left instanceof UnaryNode){
				left = getChildNegative(left);
				if (node.child(0).child(0).getToken().isOperator(Operator.RESOLUTION)){
					node.setType(node.child(0).child(0).child(1).getType());
				}
			}
			if(left instanceof NullTypeNode){
				String working_object = object_defs.get(right.getToken().getLexeme());
				right.setObject(working_object);
				left.setObject(working_object);
				left.setType(right.getType());
			}
			if(right instanceof NullTypeNode){
				String working_object = object_defs.get(left.getToken().getLexeme());
				left.setObject(working_object);
				right.setObject(working_object);
				right.setType(left.getType());
			}
			
			if (node.getOperator().equals(Operator.ADD) && node.getParent() instanceof IfStatementNode){
				operatorTypeCheckError(node, false, "ADD found in if clause");
			}
			if (node.getOperator().equals(Operator.MINUS) && node.getParent() instanceof IfStatementNode){
				operatorTypeCheckError(node, false, "MINUS found in if clause");
			}
			if (node.getOperator().equals(Operator.MULTIPLY) && node.getParent() instanceof IfStatementNode){
				operatorTypeCheckError(node, false, "MULTIPLY found in if clause");
			}
			if (node.getOperator().equals(Operator.DIVIDE) && node.getParent() instanceof IfStatementNode){
				operatorTypeCheckError(node, false, "DIVIDE found in if clause");
			}
			
			if (node.getOperator().equals(Operator.CREATE)){
				if(node.getParent().getType() == null){
					if(parse_trees.containsKey(node.child(0).getToken().getLexeme())){
						node.child(0).setType(ObjectType.OBJECT);
						node.setType(ObjectType.OBJECT);
						node.getParent().setType(ObjectType.OBJECT);
					}
					else if (node.getParent().child(0).getType().equals(ObjectType.OBJECT)){
						node.getParent().setType(ObjectType.OBJECT);
					}
				}
				
				if(node.getParent().getType() == null){
					ParseNode parent = node.getParent();
					parent = parent.getParent();
					if (parent.getToken().isOperator(Operator.RESOLUTION)){
						if (parent.child(0).getType().equals(ObjectType.OBJECT)){
							String working_object = node.child(0).getToken().getLexeme();
							ParseNode setting_tree = parse_trees.get(working_object);
							//int i = 0;
							//while ( i < ASTree.nChildren()){
							//	if (left.getToken().getLexeme().equals(ASTree.child(i).getToken().getLexeme())){
									node.removeLastChild();
									node.setType(ObjectType.OBJECT);
									node.setObject(node.child(0).getToken().getLexeme());
									node.child(0).setType(ObjectType.OBJECT);
									node.child(0).setObject(node.child(0).getToken().getLexeme());
									//ParseNode new_object_append = ASTree.child(i);
									node.setTree(setting_tree);
									parent.setTree(setting_tree);
									String object_type = object_defs.get(working_object);
									if (object_type == null){
										return true;
									}
									if (!(node.child(0).getToken().getLexeme().equals(object_type))){
										typeCheckError(node, left, node.getParent().child(0));
									}
									//return true;
							//	}
							//	i++;
							//}
						}
					}
								
				}
				if (node.getParent().getType().equals(ObjectType.OBJECT)){
					//int i = 0;
					String working_object = node.getParent().child(0).getToken().getLexeme();
					ParseNode setting_tree = parse_trees.get(working_object);
					//while ( i < ASTree.nChildren()){
						//if (left.getToken().getLexeme().equals(ASTree.child(i).getToken().getLexeme())){
							node.removeLastChild();
							node.setType(ObjectType.OBJECT);
							node.setObject(node.child(0).getToken().getLexeme());
							node.child(0).setType(ObjectType.OBJECT);
							node.child(0).setObject(node.child(0).getToken().getLexeme());
							//ParseNode new_object_append = ASTree.child(i);
							node.setTree(setting_tree);
							String object_type = object_defs.get(working_object);
							if (object_type == null){
								return true;
							}
							if (!(node.child(0).getToken().getLexeme().equals(object_type))){
								typeCheckError(node, left, node.getParent().child(0));
							}
							//return true;
					//	}
					//	i++;
					////}
				}							
				return true;
			}
			
			if (node.getOperator().equals(Operator.RESOLUTION)){
				node.setType(ObjectType.OBJECT);
				ParseNode object_tree = null;
				String this_object;
				if (node.child(0).getToken().isOperator(Operator.RESOLUTION)){
				this_object = node.child(0).child(1).getToken().getLexeme();
				String working_object = object_defs.get(this_object);
				ParseNode working_tree = parse_trees.get(working_object);
				node.setTree(working_tree);
				node.setScope(working_tree.getScope());
				object_tree = working_tree;
				}
				else
				{
				this_object = node.child(0).getToken().getLexeme();
				String working_object = object_defs.get(this_object);
				ParseNode working_tree = parse_trees.get(working_object);
				node.setTree(working_tree);
				node.setScope(working_tree.getScope());
				object_tree = working_tree;
				}
						right.getParent().setType(ObjectType.OBJECT);
						right.setObject(this_object);
						Type type = right.getType();							
						getBinding((IdentifierNode)right, object_tree, type);
						return true;
			}
			requireIdentifiersAreDefined(left);
			requireIdentifiersAreDefined(right);
			
			Type leftType  = left.getType();
			Type rightType = right.getType();
				
			if(typesMatchForArithmetic(leftType, rightType)) {
				if (node.getOperator() == Operator.LESSTHAN){
					node.setType(PrimitiveType.BOOLEAN);
				}
				else if(node.getOperator() == Operator.GREATERTHAN){
					node.setType(PrimitiveType.BOOLEAN);
				}
				else if(node.getOperator() == Operator.GREATERTHANEQUAL){
					node.setType(PrimitiveType.BOOLEAN);
				}
				else if(node.getOperator() == Operator.LESSTHENEQUAL){
					node.setType(PrimitiveType.BOOLEAN);
				}
				else if(node.getOperator() == Operator.EQUAL){
					node.setType(PrimitiveType.BOOLEAN);
				}
				else if(node.getOperator() == Operator.NOTEQUAL){
					node.setType(PrimitiveType.BOOLEAN);
				}
				else
				{
					if (rightType == PrimitiveType.NULL){
						node.setType(leftType);
					}
					else
					{
					node.setType(nonErrorType(leftType, rightType));	// error-"healing" setType.
	//				node.setType(errorType(leftType, rightType));		// error-"exposing" setType.
					}
				}
			}
			else {
				typeCheckError(node, left, right);
				node.setType(PrimitiveType.ERROR);
			}
			return true;
		}	
		
		private ParseNode getChildNegative(ParseNode node){	
			while (true)
			{
				if (node.nChildren() > 0){
					node = node.child(0);
				}
				else
				{
					break;
				}
			}
			return node;
		}
		
		private void requireIdentifiersAreDefined(ParseNode node) {
			if(isUndefinedIdentifier(node)){
				useBeforeDefineError((IdentifierNode)(node));
				node.setType(PrimitiveType.ERROR);
			}
		}
		
		
		private boolean isUndefinedIdentifier(ParseNode node) {
			return node instanceof IdentifierNode &&
				   isUndefined(node.getType());
		}
		private boolean isNullNode(ParseNode node) {
			return node instanceof NullNode &&
					isNull(node.getType());
		}
		private boolean typesMatchForArithmetic(Type leftType, Type rightType) {
			return isIntegerOrError(leftType) &&
			       isIntegerOrError(rightType) ||
			       isFloatOrError(leftType) &&
			       isFloatOrError(rightType) ||
			       isIntegerOrError(leftType) &&
			       isNull(rightType) ||
			       isFloatOrError(leftType) &&
			       isNull(rightType) ||
			       isFloatOrError(leftType) &&
			       isObject(rightType) ||
			       isIntegerOrError(leftType) &&
			       isObject(rightType) ||
			       isFloatOrError(rightType) &&
			       isObject(leftType) ||
			       isIntegerOrError(rightType) &&
			       isObject(leftType) ||
			       isBoolean(rightType) &&
			       isObject(leftType) ||
			       isBoolean(leftType) &&
			       isObject(rightType) ||
			       isObject(leftType) &&
			       isObject(rightType) ||
			       isBoolean(leftType)&&
			       isNull(rightType) ||
			       isBoolean(leftType) &&
			       isBoolean(rightType);
		}

		private boolean isIntegerOrError(Type type) {
			return  isInteger(type) || isError(type);
		}
		private boolean isFloatOrError(Type type) {
			return  isFloat(type) || isError(type);
		}
		private boolean isError(Type type) {
			return type==PrimitiveType.ERROR;
		}
		private boolean isObject(Type type) {
			return type==ObjectType.OBJECT;
		}
		private boolean isInteger(Type type) {
			return type==PrimitiveType.INTEGER;
		}
		private boolean isFloat(Type type) {
			return type==PrimitiveType.FLOAT;
		}
		private boolean isBoolean(Type type) {
			return type==PrimitiveType.BOOLEAN;
		}
		private boolean isUndefined(Type type) {
			return type==PrimitiveType.UNDEFINED;
		}
		private boolean isNull(Type type) {
			return type==PrimitiveType.NULL;
		}

		
		private Type nonErrorType(Type leftType, Type rightType) {
			return isError(rightType) ? leftType : rightType;		// if both are ERROR, this returns ERROR.
		}
		@SuppressWarnings("unused")
		private Type errorType(Type leftType, Type rightType) {
			return isError(rightType) ? rightType : leftType;		// if either are ERROR, this returns ERROR.
		}

		
		///////////////////////////////////////////////////////////////////////////
		// leaves of the tree: identifier, intconst, stringconst
		//
		
		@Override	// note complex interaction with visit(AssignmentStatementNode)
		public boolean visit(IdentifierNode node) {				
			Binding binding;
			if (node.getParent() instanceof DeclarationNode){
				ParseNode tempNode = node;
				while (true){
					if (tempNode.getParent() instanceof FunctionBlock){
						while(true){
							if (tempNode.getParent() instanceof ObjectBlock){
								declarations.add(node.getToken().getLexeme());
							}
							if (tempNode.getParent() instanceof ProgramNode){
								break;
							}
							tempNode = tempNode.getParent();
						}
						
					}
					if (tempNode.getParent() instanceof ProgramNode){
						break;
					}
					tempNode = tempNode.getParent();
				}
				binding = Binding.nullInstance();
				node.setType(binding.getType());
				node.setBinding(binding);
				return true;
			}
			
			if(node.getParent() instanceof ExpressionList){
				node.setType(PrimitiveType.UNDEFINED);
				Type node_type = null;
				if (node.child(0).getToken().getLexeme().equals("int")){
					node_type = PrimitiveType.INTEGER;
				}
				else if (node.child(0).getToken().getLexeme().equals("float")){
					node_type = PrimitiveType.FLOAT;
				}
				else if (node.child(0).getToken().getLexeme().equals("bool")){
					node_type = PrimitiveType.BOOLEAN;
				}
				defineVariableIfNecessary(node, node_type);
			}
			
			if (node.getParent() instanceof PrintStatementNode){
				ParseNode tempNode2 = node;
				while (true){
					if (node.getParent() instanceof ExpressionList){
						declarations.add(node.getToken().getLexeme());
					}
					if (tempNode2.getParent() instanceof FunctionBlock){
						while(true){
							if (tempNode2.getParent() instanceof ObjectBlock){
								if (declarations.contains(node.getToken().getLexeme())){
									break;
								}
								else
								{
									Token thisToken = KeywordToken.make(node.getToken().getLocation(), "this", 0);
									ParseNode objectThis = new ObjectNode(thisToken);
									node.appendChild(objectThis);
								}
							}
							if (tempNode2.getParent() instanceof ProgramNode){
								break;
							}
							tempNode2 = tempNode2.getParent();
						}
						
					}
					if (tempNode2.getParent() instanceof ProgramNode){
						break;
					}
					tempNode2 = tempNode2.getParent();
				}
				if (node.getParent().child(0).getType() == null)
				{
					
				
				}
				else
				{
					int p = 0;
					while(p < node.getParent().nChildren()){
						if (node.getParent().child(p) instanceof IdentifierNode){
							if (node.getParent().child(p).getType() == null){
								binding = node.findVariableBinding();
								node.setType(binding.getType());
								node.setBinding(binding);
								return true;
							}
							else if (node.getParent().child(p).getType().equals(ObjectType.OBJECT)){
								String object_this = node.getParent().child(p).getToken().getLexeme();
								if (object_defs.containsKey(object_this)){
									String object_this_2 = object_defs.get(object_this);
									ParseNode object_tree = parse_trees.get(object_this_2);									
								getBinding(node, object_tree, node.getType());
								return true;
									
								}
							}
						}
						p++;
					}
					
				String this_object = object_defs.get(node.getParent().child(0).getToken().getLexeme());
				ParseNode object_tree = parse_trees.get(this_object);						
				getBinding(node, object_tree, node.getType());
				return true;
				}
			}
			ParseNode tempNode2 = node;
			while (true){
				if (node.getParent() instanceof ExpressionList){
					declarations.add(node.getToken().getLexeme());
				}
				if (tempNode2.getParent() instanceof FunctionBlock){
					while(true){
						if (tempNode2.getParent() instanceof ObjectBlock){
							if (declarations.contains(node.getToken().getLexeme())){
								break;
							}
							else
							{
								Token thisToken = KeywordToken.make(node.getToken().getLocation(), "this", 0);
								ParseNode objectThis = new ObjectNode(thisToken);
								node.appendChild(objectThis);
							}
						}
						if (tempNode2.getParent() instanceof ProgramNode){
							break;
						}
						tempNode2 = tempNode2.getParent();
					}
					
				}
				if (tempNode2.getParent() instanceof ProgramNode){
					break;
				}
				tempNode2 = tempNode2.getParent();
			}
			binding = node.findVariableBinding();
			node.setType(binding.getType());
			node.setBinding(binding);
			return true;
		}
		@Override
		public boolean visit(IntegerConstantNode node) {
			node.setType(PrimitiveType.INTEGER);
			return true;
		}
		@Override
		public boolean visit(FloatingConstantNode node) {
			node.setType(PrimitiveType.FLOAT);
			return true;
		}
		@Override
		public boolean visit(StringConstantNode node) {
			node.setType(PrimitiveType.STRING);
			return true;
		}
		public boolean visit(BooleanNode node) {
			node.setType(PrimitiveType.BOOLEAN);
			return true;
		}
		public boolean visit(NullNode node) {
			node.setType(PrimitiveType.NULL);
			return true;
		}
		
		
		
		///////////////////////////////////////////////////////////////////////////
		// Error reporting
		//
		
		private void typeCheckError(ParseNode node, ParseNode left, ParseNode right) {
			operatorTypeCheckError(node, true, left.getType() + ", " + right.getType());
		}
		//private void divideZeroError(ParseNode node, ParseNode left, ParseNode right) {
		//	Token nodeToken = node.getToken();
			//if (right. == "0"){
			//	Error.reportError("Divide by zero at " + nodeToken.getLocation());
			//}
		//}
		private void operatorTypeCheckError(ParseNode node, boolean plural, String typesString) {
			Token nodeToken = node.getToken();
			String typeWordString = plural ? "types" : "type";
			Error.reportError("operator " + nodeToken.getLexeme() + 
					  " not defined for " + typeWordString + " " + typesString +
					  " at " + nodeToken.getLocation());
		}

		private void useBeforeDefineError(IdentifierNode identifierNode) {
			Token token = identifierNode.getToken();
			Error.reportError("variable " + token.getLexeme() + " used before defined at " + token.getLocation());
		}
		private void incorrectFunction(ParseNode node) {
			Token token = node.child(0).getToken();
			Error.reportError("function block for " + token.getLexeme() + " is not the same as the function definition" + token.getLocation());
		}
		private void undefinedFunction(ParseNode node) {
			Token token = node.getToken();
			Error.reportError("function " + token.getLexeme() + " is not defined before use" + token.getLocation());
		}
		private void incorrectTypeReturn(ParseNode node, String type) {
			Token token = node.getToken();
			Error.reportError("function " + token.getLexeme() + " cannot return type " + type + token.getLocation());
		}
		private void noReturnType(ParseNode node, String type) {
			Token token = node.getToken();
			Error.reportError("function " + token.getLexeme() + " must return type " + type + token.getLocation());
		}
		private void functionReturnError(ParseNode node) {
			Token token = node.getToken();
			Error.reportError("function " + token.getLexeme() + " cannot return this argument " + token.getLocation());
		}
		private void functionArgumentError(ParseNode node) {
			Token token = node.getToken();
			Error.reportError("function " + token.getLexeme() + " cannot pass these arguments, incorrect type. " + token.getLocation());
		}
	}
}
