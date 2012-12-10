package asmCodeGenerator;

import static asmCodeGenerator.ASMCodeFragment.CodeType.*;
import static asmCodeGenerator.ASMOpcode.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Map;

import parseTree.*;
import semanticAnalyzer.ObjectType;
import semanticAnalyzer.PrimitiveType;
import semanticAnalyzer.Type;
import symbolTable.BaseAddress;
import symbolTable.Binding;
import symbolTable.Scope;
import tokens.Operator;
import tokens.Token;
import tokens.OperatorToken;

public class ASMCodeGenerator {
	ParseNode tree;
	Hashtable<String, String> object_defs = new Hashtable<String, String>();
	Hashtable<String, ParseNode> object_nodes = new Hashtable<String, ParseNode>();
	Hashtable<String, Integer> arguments_table = new Hashtable<String, Integer>();
	Hashtable<String, ParseNode> function_defs = new Hashtable<String, ParseNode>();
	Vector<ParseNode> object_fun_def = new Vector<ParseNode>();
	Vector<String> object_fun_name = new Vector<String>();
	Vector<String> live_obj = new Vector<String>();
	Vector<String> declarations = new Vector<String>();

	public ASMCodeGenerator(ParseNode tree) {
		super();
		this.tree = tree;
	}
	
	public ASMCodeFragment makeASM() {
		ASMCodeFragment program = makeProgramASM();
		ASMCodeFragment runtime = RunTime.getEnvironment();
		
		runtime.append(program);
		runtime.add(Halt);
		runtime.append(MemoryManager.codeForAfterApplication());
		return runtime;
	}

	private ASMCodeFragment makeProgramASM() {
		CodeVisitor visitor = new CodeVisitor();
		tree.accept(visitor);
		return visitor.removeRootCode(tree);
	}

	class CodeVisitor extends ParseNodeVisitor.DefaultPostorder {
		
		private Labeller labeller = new Labeller();
		private Map<ParseNode, ASMCodeFragment> codeMap;
		ASMCodeFragment code;
		
		public CodeVisitor() {
			codeMap = new HashMap<ParseNode, ASMCodeFragment>();
		}

		////////////////////////////////////////////////////////////////////
        // Make the field "code" refer to a new fragment of different sorts.
		private void newAddressCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_ADDRESS);
			codeMap.put(node, code);
		}
		private void newValueCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_VALUE);
			codeMap.put(node, code);
		}
		private void newVoidCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_VOID);
			codeMap.put(node, code);
		}

	    ////////////////////////////////////////////////////////////////////
        // Get code from the map.
		private ASMCodeFragment getAndRemoveCode(ParseNode node) {
			ASMCodeFragment result = codeMap.get(node);	
			codeMap.remove(result);
			return result;
		}
	    public  ASMCodeFragment removeRootCode(ParseNode tree) {
			return getAndRemoveCode(tree);
		}		
		private ASMCodeFragment removeValueCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			makeFragmentValueCode(frag, node);
			return frag;
		}		
		private ASMCodeFragment removeAddressCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			assert frag.isAddress();
			return frag;
		}		
		private ASMCodeFragment removeVoidCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			assert frag.isVoid();
			return frag;
		}
	    ////////////////////////////////////////////////////////////////////
        // convert code to value-generating code.
		
		private void makeFragmentValueCode(ASMCodeFragment code, ParseNode node) {
			assert !code.isVoid();
			
			if(node.getType() == PrimitiveType.NULL){
				//System.out.print("Here");
			}
			else if(code.isAddress()) {
				turnAddressIntoValue(code, node);
			}
		}
		private void turnAddressIntoValue(ASMCodeFragment code, ParseNode node) {
			if(node.getType() == PrimitiveType.INTEGER) {
				code.add(LoadI);
			}	
			else if(node.getType() == PrimitiveType.FLOAT) {
				code.add(LoadF);
			}
			else if(node.getType() == PrimitiveType.STRING) {
				//special case: leave as is.  Operators and statements must handle.
			}
			else if(node.getType() == PrimitiveType.NULL) {
				//special case: leave as is.  Operators and statements must handle.
			}
			else if(node.getType() == PrimitiveType.BOOLEAN) {
				code.add(LoadI);
			}
			else {
				assert false : "node " + node;
			}
			code.markAsValue();
		}

		////////////////////////////////////////////////////////////////////
        // Statements and decls
		public boolean visit(NullTypeNode node) {
			newVoidCode(node);
			code.add(PushI, 0);
			return true;
		}
		public boolean visitLeave(StatementListNode node) {
			newVoidCode(node);
			
			for(ParseNode child : node.getChildren()) {			// non-routine-decls first
				ASMCodeFragment childCode = removeVoidCode(child);
				if (childCode == null){	
					code.add(Nop);
				}
				else
				{
				code.append(childCode);
				}
			}
			return true;
		}
		
		public boolean visitLeave(FunctionBlock node){
			newVoidCode(node);
			if (node.getToken().getLexeme().equals("NUF")){
				return true;
			}
			
			for(ParseNode child : node.getChildren()) {			// non-routine-decls first
				ASMCodeFragment childCode = removeVoidCode(child);
				if (childCode == null){	
					code.add(Nop);
				}
				else
				{
					code.append(childCode);
				}
			}
			declarations.clear();
			return true;
		}
		
		public boolean visitLeave(ProgramNode node){
			newVoidCode(node);	
			code.add(PushD, "$system-stack-pointer");
			code.add(PushD, "$system-frame-pointer");
			code.add(LoadI);
			code.add(StoreI);
			for(ParseNode child : node.getChildren()) {			// non-routine-decls first
				ASMCodeFragment childCode = removeVoidCode(child);
				if (childCode == null){	
					code.add(Nop);
				}
				else
				{
					code.append(childCode);
				}
			}
			return true;
		}
		public boolean visitEnter(ProgramNode node){
			return true;
		}
		
		
		public boolean visitEnter(FunctionBlock node){
			if (node.getToken().getLexeme().equals("NUF")){
				return true;
			}
			else if (node.getToken().getLexeme().equals("return")){
				return true;
			}
			else if(node.getToken().getLexeme().equals("function")){
				return true;
			}
			
			ParseNode tempNode = node;
			boolean passing = true;
			while (true){
				if (tempNode.getParent() instanceof ObjectBlock){
					String object_name = tempNode.getParent().getParent().getToken().getLexeme();
					object_fun_name.add(object_name);
					object_fun_def.add(tempNode);
					passing = false;
					
				}
				if (tempNode.getParent() instanceof ProgramNode){
					break;
				}
				tempNode = tempNode.getParent();
			}
			int p = 0;
			String args2 = "";
			if (node.nChildren() > 0){
				if (node.child(0) instanceof FunctionNode){
					if (node.child(0).nChildren() > 1){
						if (node.child(0).child(1) instanceof ExpressionList){
							while (p < node.child(0).child(1).nChildren()){
								args2 = args2 + "-" + node.child(0).child(1).child(p).child(0).getToken().getLexeme();
								p++;
							}
						}
					}
				}
			}
			if(passing == true){
			function_defs.put(node.child(0).getToken().getLexeme() + args2, node);
			}
			return true;
		}
		
		public boolean visitLeave(FunctionNode node){
			newVoidCode(node);
			if (node.getToken().getLexeme().equals("function")){
				if (node.nChildren() == 0){
				return true;
				}
				else{
					int u = 1;
					String argsy = "";
					if (node.nChildren() > 0){
						if (node.child(0).nChildren() > 1){
							if (!(node.child(0).child(1) instanceof ExpressionList)){
								while (u < node.child(0).nChildren()){
									argsy = argsy + "-" + node.child(0).child(u).child(0).getToken().getLexeme();
									u++;
								}
							}
						}
					}
					function_defs.put(node.child(0).getToken().getLexeme() + argsy, node);
					return true;
				}
				
			}
			if (node.getParent() instanceof ProgramNode){
				int p = 0;
				String args2 = "";
				if (node.nChildren() > 0){
					if (node.child(0) instanceof FunctionNode){
						if (node.child(0).nChildren() > 1){
							if (node.child(0).child(1) instanceof ExpressionList){
								while (p < node.child(0).child(1).nChildren()){
									args2 = args2 + "-" + node.child(0).child(1).child(p).child(0).getToken().getLexeme();
									p++;
								}
							}
						}
					}
				}
				function_defs.put(node.child(0).getToken().getLexeme() + args2, node);
			}
			String label2 = labeller.newLabel("returnFunction-", "");
			if (node.getToken().getLexeme().equals("return")){
				for(ParseNode child : node.getChildren()) {			// non-routine-decls first
					ASMCodeFragment childCode = removeVoidCode(child);
					if (childCode == null){	
						code.add(Nop);
					}
					else
					{
					code.append(childCode);
					if(child instanceof IdentifierNode){
						if (child.getParent().getToken().getLexeme().equals("return")){
							if (node.getType().equals(PrimitiveType.INTEGER)){
							code.add(LoadI);
							}
							else if (node.getType().equals(PrimitiveType.FLOAT)){
							code.add(LoadF);
							}
							else if (node.getType().equals(PrimitiveType.BOOLEAN)){
								code.add(LoadI);
								}
						}
					}
					}
				}
					code.add(Label, "--ident-return-address");
					code.add(PushD, "$system-frame-pointer");
					code.add(LoadI);
					code.add(PushI, -8);
					code.add(Add);
					code.add(LoadI);
					code.add(Return);
					code.add(Label, label2);
				return true;
			}
			int y = 1;
			String argsy2 = "";
			if (node.nChildren() > 1){
				if (!(node.child(1) instanceof ExpressionList)){
					while (y < node.nChildren()){
					argsy2 = argsy2 + "-" + node.child(y).child(0).getToken().getLexeme();
					y++;
					}
				}
			}
			String identifier = node.getToken().getLexeme() + argsy2;
			String label = labeller.newLabel("endFunction-", "");
			int size_of_function;
			if (node.getParent() instanceof ProgramNode){
				ParseNode tempNodey = node.getParent();
				int childSize = tempNodey.nChildren();
				int i = 0;
				while (i < childSize){
					if(tempNodey.child(i) instanceof FunctionBlock){
						int a = 0;
						String argsy3 = "";
						ParseNode parseTemp = tempNodey.child(i);
						if (parseTemp.child(0) instanceof FunctionNode){
							if (parseTemp.child(0).nChildren() > 1){
								if (parseTemp.child(0).child(1) instanceof ExpressionList){
									while (a < parseTemp.child(0).child(1).nChildren()){
										argsy3 = argsy3 + "-" + parseTemp.child(0).child(1).child(a).child(0).getToken().getLexeme();
										a++;
									}
								}
							}
						}
						String fun_check = tempNodey.child(i).child(0).getToken().getLexeme() + argsy3;
						if (fun_check.equals(identifier)){
							function_defs.put(fun_check, tempNodey.child(i));
							return true;
						}
					}
					i++;
				}
				size_of_function = tempNodey.child(i).getScope().getAllocatedSize();
			}
			else
			{	
				size_of_function = node.getParent().getScope().getAllocatedSize();
			}
			code.add(Jump, label);
			ParseNode tempNode = node;
			boolean passing = true;
			String object_name = null;
			while (true){
				if (tempNode.getParent() instanceof ObjectNode){
					object_name = tempNode.getParent().getToken().getLexeme();
					passing = false;
				}
				if (tempNode.getParent() instanceof ProgramNode){
					break;
				}
				tempNode = tempNode.getParent();
			}
			Vector<String> argTypes = new Vector<String>();
			if (node.nChildren() > 1){
				if (node.child(1) instanceof ExpressionList){
					int p = 0;
					int childrenNode = node.child(1).nChildren();
					while (p < childrenNode){
						argTypes.add(node.child(1).child(p).child(0).getToken().getLexeme());
						p++;
					}
				}
			}
			String args = "";
			int p = 0;
			while (p < argTypes.size()){
				args = args + "-" + argTypes.get(p);
				p++;
			}
			if (passing == true){		
			code.add(Label, identifier + args);
			}
			else
			{
			code.add(Label, identifier + args + "-" + object_name);
			}
			code.add(Label, "--ident-return-address");
			code.add(PushD, "$system-frame-pointer");
			code.add(LoadI);
			code.add(PushI, -8);
			code.add(Add);
			code.add(Exchange);
			code.add(StoreI);
			code.add(PushD, "$system-frame-pointer");
			code.add(LoadI);
			code.add(PushI, size_of_function + 8);		
			code.add(Subtract);
			code.add(PushD, "system-stack-pointer");
			code.add(Exchange);
			code.add(StoreI);	
			for(ParseNode child : node.getChildren()) {			// non-routine-decls first
				ASMCodeFragment childCode = removeVoidCode(child);
				if (childCode == null){	
					code.add(Nop);
				}
				else
				{
				code.append(childCode);
				}
			}
			code.add(Label, "--ident-return-address");
			code.add(PushD, "$system-frame-pointer");
			code.add(LoadI);
			code.add(PushI, -8);
			code.add(Add);
			code.add(LoadI);
			code.add(Return);
			code.add(Label, label);
			return true;
		}
		
		public boolean visitEnter(FunctionNode node){
			if (node.getParent() instanceof ProgramNode){
				return false;
			}
			return true;
		}
		public boolean visitEnter(CallStatementNode node){
			if (node.getToken().isOperator(Operator.RESOLUTION)){
				live_obj.add(node.child(0).getToken().getLexeme());
			}
			return true;
		}
		
		public boolean visitLeave(CallStatementNode node){
			newVoidCode(node);
			String args = "";
			if (node.getToken().isOperator(Operator.RESOLUTION)){
				String ident = node.child(0).getToken().getLexeme();
				IdentifierNode working_object = (IdentifierNode)object_nodes.get(ident);
				code.add(PushD, "$system-object-pointer");
				code.add(Label, "--ident-"+ident);
				Binding binding = working_object.getBinding();
				pushMemoryBlockFor(binding);
				addOffsetInBlockFor(binding);
				code.add(LoadI);
				code.add(StoreI);
			}
			code.add(PushD, "$system-stack-pointer");
			code.add(LoadI);
			ParseNode tempNode = node;
			int size = tempNode.getLocalScope().getStaticNestingLevel();
			while (size != 0){
				tempNode = tempNode.getParent();
				size = tempNode.getLocalScope().getStaticNestingLevel();
			}
			size = tempNode.getLocalScope().getAllocatedSize();
			code.add(PushI, size);		
			code.add(Subtract);
			code.add(PushD, "$system-stack-pointer");
			code.add(Exchange);
			code.add(StoreI);
			String function = null;
			if (node.getToken().isOperator(Operator.RESOLUTION)){
				int p = 2;
				String argsy = "";
				if (node.nChildren() > 0){
					while (p < node.nChildren()){
						if (node.child(p).getType().equals(PrimitiveType.INTEGER)){
							argsy = argsy + "-int";
						}
						else if (node.child(p).getType().equals(PrimitiveType.FLOAT)){
							argsy = argsy + "-float";
						}
						else if (node.child(p).getType().equals(PrimitiveType.BOOLEAN)){
							argsy = argsy + "-bool";
						}
						p++;
					}
				}
				String identifier2 = node.child(1).getToken().getLexeme() + argsy;
				function = identifier2;
			}
			else
			{
				int p = 0;
				String args2 = "";
				if (node.nChildren() > 0){
					while (p < node.nChildren()){
						if (node.child(p).getType().equals(PrimitiveType.INTEGER)){
							args2 = args2 + "-int";
						}
						else if (node.child(p).getType().equals(PrimitiveType.FLOAT)){
							args2 = args2 + "-float";
						}
						else if (node.child(p).getType().equals(PrimitiveType.BOOLEAN)){
							args2 = args2 + "-bool";
						}
						p++;
					}
				}
				String identifier2 = node.getToken().getLexeme() + args2;
				function = identifier2;
			}
			ParseNode functionCall = function_defs.get(function);
			if (functionCall.child(0).child(1) instanceof ExpressionList){
				int childrenSize = functionCall.child(0).child(1).nChildren();
				int total_size = 0;
				int position = 0;
				int childrenCounter = 0;
				Vector<String> arguments = new Vector<String>();
				int[] type_size_argument = new int[100];
				if (node.getToken().isOperator(Operator.RESOLUTION)){
					childrenSize += 2;
				}
				while(childrenCounter < childrenSize){
					String var_name = functionCall.child(0).child(1).child(childrenCounter).getToken().getLexeme();
					code.add(Label, "--ident-" + var_name);
					code.add(PushD, "$system-stack-pointer");
					code.add(LoadI);
					int type_size = -(functionCall.child(0).child(1).child(childrenCounter).getType().getSize());
					total_size += type_size;
					type_size_argument[childrenCounter] = -(type_size);
					position += -(type_size);
					arguments_table.put(var_name, total_size);
					arguments.add(var_name);
					//args = args  + "-" + functionCall.child(0).child(1).child(childrenCounter).child(0).getToken().getLexeme();
					code.add(PushI, total_size);
					code.add(Add);
					if (node.getToken().isOperator(Operator.RESOLUTION)){
						childrenCounter += 2;
					}
					ASMCodeFragment childrenCode = removeVoidCode(node.child(childrenCounter));
					if (node.getToken().isOperator(Operator.RESOLUTION)){
						childrenCounter -= 2;
					}
					code.append(childrenCode);
					if (node.child(childrenCounter) instanceof IdentifierNode){
						if (node.child(childrenCounter).getType().equals(PrimitiveType.INTEGER)){
							code.add(LoadI);
						}
						else if (node.child(childrenCounter).getType().equals(PrimitiveType.FLOAT)){
							code.add(LoadF);
						}
						else if (node.child(childrenCounter).getType().equals(PrimitiveType.BOOLEAN)){
							code.add(LoadI);
						}
					}
					if (functionCall.child(0).child(1).child(childrenCounter).getType().equals(PrimitiveType.INTEGER)){
						ParseNode tempNode2 = node;
						boolean pass = true;
						while (true){
							if (tempNode2.nChildren() > 0){
								if (tempNode2.child(0) instanceof BinaryOperatorNode){
									int h = 0;
									while (h < tempNode2.child(0).nChildren()){
										if (tempNode2.child(0).child(h) instanceof CallStatementNode){
											
											code.add(Label, "--return-value");
											code.add(PushD, "$system-stack-pointer");
											code.add(LoadI);
											code.add(PushI, -4);
											code.add(Add);
											code.add(Exchange);
											code.add(StoreI);
											code.add(Pop);
											pass = false;
											break;
										}
										h++;
									}							
								}
							}								
							break;
						}
						if (pass == true){
							code.add(StoreI);
						}						
					}
					else if (functionCall.child(0).child(1).child(childrenCounter).getType().equals(PrimitiveType.FLOAT)){
						ParseNode tempNode2 = node;
						boolean pass = true;
						while (true){
							if (tempNode2.nChildren() > 0){
								if (tempNode2.child(0) instanceof BinaryOperatorNode){
									int h = 0;
									while (h < tempNode2.child(0).nChildren()){
										if (tempNode2.child(0).child(h) instanceof CallStatementNode){
											code.add(Label, "--return-value");
											code.add(PushD, "$system-stack-pointer");
											code.add(LoadI);
											code.add(PushI, -4);
											code.add(Add);
											code.add(Exchange);
											code.add(Pop);
											pass = false;
											break;
										}
										h++;
									}							
								}
							}
							break;
						}
						if(pass == true){
						code.add(StoreF);
						}
					}
					else if (functionCall.child(0).child(1).child(childrenCounter).getType().equals(PrimitiveType.BOOLEAN)){
						ParseNode tempNode2 = node;
						boolean pass = true;
						while (true){
							if (tempNode2.nChildren() > 0){
								if (tempNode2.child(0) instanceof BinaryOperatorNode){
									int h = 0;
									while (h < tempNode2.child(0).nChildren()){
										if (tempNode2.child(0).child(h) instanceof CallStatementNode){
											code.add(Label, "--return-value");
											code.add(PushD, "$system-stack-pointer");
											code.add(LoadI);
											code.add(PushI, -4);
											code.add(Add);
											code.add(Exchange);
											code.add(StoreI);
											code.add(Pop);
											pass = false;
											break;
										}
										h++;
									}							
								}
							}
								
							break;
						}		
						if (pass == true){
							code.add(StoreI);
						}
					}
					childrenCounter++;
					if (node.getToken().isOperator(Operator.RESOLUTION)){
						childrenCounter += 2;
					}
				}
				int q = 0;
				while(q < arguments_table.size()){
					int value = arguments_table.get(arguments.get(q));					
					value = value + position + type_size_argument[q];
					arguments_table.remove(arguments.get(q));
					arguments_table.put(arguments.get(q), value);
					q++;
				}
				code.add(Label, "--ident-old-frame-pointer");
				code.add(PushD, "$system-stack-pointer");
				code.add(LoadI);
				total_size += -4;
				code.add(PushI, total_size);
				code.add(Add);
				code.add(PushD, "$system-frame-pointer");
				code.add(LoadI);
				code.add(StoreI);
				code.add(Label, "--ident-return-address");
				code.add(PushD, "$system-stack-pointer");
				code.add(LoadI);
				total_size += -4;
				code.add(PushI, total_size);
				code.add(Add);
				code.add(PushI, 0);
				code.add(LoadI);
				code.add(StoreI);
				code.add(Label, "--ident-object-address");
				code.add(PushD, "$system-stack-pointer");
				code.add(LoadI);
				total_size += -4;
				code.add(PushI, total_size);
				code.add(Add);
				code.add(PushD, "$system-object-pointer");
				code.add(LoadI);
				code.add(StoreI);
				code.add(PushD, "$system-frame-pointer");
				code.add(PushD, "$system-stack-pointer");
				code.add(LoadI);
				code.add(StoreI);
				code.add(PushD, "$system-frame-pointer");
				code.add(LoadI);
				code.add(PushI, (-total_size) - 12);		
				code.add(Subtract);
				code.add(PushD, "system-stack-pointer");
				code.add(Exchange);
				code.add(StoreI);
				code.add(PushD, "$system-frame-pointer");
				code.add(PushD, "$system-stack-pointer");
				code.add(LoadI);
				code.add(StoreI);
				boolean passing = true;
				String object_name = null;
				if (node.getToken().isOperator(Operator.RESOLUTION)){
					String temp_obj = node.child(0).getToken().getLexeme();
					object_name = object_defs.get(temp_obj);
					passing = false;
				}
				if (passing == true){
				code.add(Call, function);
				}
				else
				{
				code.add(Call, function + "-" + object_name);	
				}
				arguments_table.clear();
				int size_of_function = functionCall.getScope().getAllocatedSize();
				size_of_function += -(total_size);
				code.add(Label, "--ident-old-frame-pointer");
				code.add(PushD, "$system-frame-pointer");
				code.add(LoadI);
				code.add(PushI, -4);
				code.add(Add);
				code.add(LoadI);
				code.add(PushD, "$system-frame-pointer");
				code.add(Exchange);
				code.add(StoreI);
				code.add(PushD, "$system-stack-pointer");
				code.add(LoadI);
				code.add(PushI, size_of_function);		
				code.add(Add);
				code.add(PushD, "$system-stack-pointer");
				code.add(Exchange);
				code.add(StoreI);
				if (functionCall.child(0).child(0).getToken().getLexeme().equals("void")){
					
				}
				else
				{
					code.add(Label, "--return-value");
					code.add(PushD, "$system-stack-pointer");
					code.add(LoadI);
					code.add(PushI, -4);
					code.add(Add);
					code.add(Exchange);
					code.add(StoreI);
					code.add(Label, "--return-value");
					code.add(PushD, "$system-stack-pointer");
					code.add(LoadI);
					code.add(PushI, -4);
					code.add(Add);
					code.add(LoadI);
				}
				
			}
			else 
			{
				
					int childrenSize = functionCall.child(0).nChildren();
					int total_size = 0;
					int position = 0;
					int childrenCounter = 1;
					Vector<String> arguments = new Vector<String>();
					int[] type_size_argument = new int[100];
					if (node.getToken().isOperator(Operator.RESOLUTION)){
						childrenSize += 2;
					}
					while(childrenCounter < childrenSize){
						String var_name = functionCall.child(0).child(childrenCounter).getToken().getLexeme();
						code.add(Label, "--ident-" + var_name);
						code.add(PushD, "$system-stack-pointer");
						code.add(LoadI);
						int type_size = 0;
						if (functionCall.child(0).child(childrenCounter).child(0).getToken().getLexeme().equals("int")){
							type_size = -4;
						}
						else if (functionCall.child(0).child(childrenCounter).child(0).getToken().getLexeme().equals("float")){
							type_size = -8;
						}
						else if (functionCall.child(0).child(childrenCounter).child(0).getToken().getLexeme().equals("bool")){
							type_size = -4;
						}
						total_size += type_size;
						type_size_argument[childrenCounter] = -(type_size);
						position += -(type_size);
						arguments_table.put(var_name, total_size);
						arguments.add(var_name);
						//args = args  + "-" + functionCall.child(0).child(1).child(childrenCounter).child(0).getToken().getLexeme();
						code.add(PushI, total_size);
						code.add(Add);
						if (node.getToken().isOperator(Operator.RESOLUTION)){
							childrenCounter += 2;
						}
						ASMCodeFragment childrenCode = removeVoidCode(node.child(childrenCounter - 1));
						if (node.getToken().isOperator(Operator.RESOLUTION)){
							childrenCounter -= 2;
						}
						code.append(childrenCode);
						if (node.child(childrenCounter - 1) instanceof IdentifierNode){
							if (node.child(childrenCounter).getType().equals(PrimitiveType.INTEGER)){
								code.add(LoadI);
							}
							else if (node.child(childrenCounter - 1).getType().equals(PrimitiveType.FLOAT)){
								code.add(LoadF);
							}
							else if (node.child(childrenCounter - 1).getType().equals(PrimitiveType.BOOLEAN)){
								code.add(LoadI);
							}
						}
						if (functionCall.child(0).child(childrenCounter).child(0).getToken().getLexeme().equals("int")){
							ParseNode tempNode2 = node;
							boolean pass = true;
							while (true){
								if (tempNode2.nChildren() > 0){
									if (tempNode2.child(0) instanceof BinaryOperatorNode){
										int h = 0;
										while (h < tempNode2.child(0).nChildren()){
											if (tempNode2.child(0).child(h) instanceof CallStatementNode){
												
												code.add(Label, "--return-value");
												code.add(PushD, "$system-stack-pointer");
												code.add(LoadI);
												code.add(PushI, -4);
												code.add(Add);
												code.add(Exchange);
												code.add(StoreI);
												code.add(Pop);
												pass = false;
												break;
											}
											h++;
										}							
									}
								}								
								break;
							}
							if (pass == true){
								code.add(StoreI);
							}						
						}
						else if (functionCall.child(0).child(childrenCounter).child(0).getToken().getLexeme().equals("float")){
							ParseNode tempNode2 = node;
							boolean pass = true;
							while (true){
								if (tempNode2.nChildren() > 0){
									if (tempNode2.child(0) instanceof BinaryOperatorNode){
										int h = 0;
										while (h < tempNode2.child(0).nChildren()){
											if (tempNode2.child(0).child(h) instanceof CallStatementNode){
												code.add(Label, "--return-value");
												code.add(PushD, "$system-stack-pointer");
												code.add(LoadI);
												code.add(PushI, -4);
												code.add(Add);
												code.add(Exchange);
												code.add(Pop);
												pass = false;
												break;
											}
											h++;
										}							
									}
								}
								break;
							}
							if(pass == true){
							code.add(StoreF);
							}
						}
						else if (functionCall.child(0).child(childrenCounter).child(0).getToken().getLexeme().equals("bool")){
							ParseNode tempNode2 = node;
							boolean pass = true;
							while (true){
								if (tempNode2.nChildren() > 0){
									if (tempNode2.child(0) instanceof BinaryOperatorNode){
										int h = 0;
										while (h < tempNode2.child(0).nChildren()){
											if (tempNode2.child(0).child(h) instanceof CallStatementNode){
												code.add(Label, "--return-value");
												code.add(PushD, "$system-stack-pointer");
												code.add(LoadI);
												code.add(PushI, -4);
												code.add(Add);
												code.add(Exchange);
												code.add(StoreI);
												code.add(Pop);
												pass = false;
												break;
											}
											h++;
										}							
									}
								}
									
								break;
							}		
							if (pass == true){
								code.add(StoreI);
							}
						}
						childrenCounter++;
						if (node.getToken().isOperator(Operator.RESOLUTION)){
							childrenCounter += 2;
						}
					}
					int q = 0;
					while(q < arguments_table.size()){
						int value = arguments_table.get(arguments.get(q));					
						value = value + position + type_size_argument[q];
						arguments_table.remove(arguments.get(q));
						arguments_table.put(arguments.get(q), value);
						q++;
					}
					code.add(Label, "--ident-old-frame-pointer");
					code.add(PushD, "$system-stack-pointer");
					code.add(LoadI);
					total_size += -4;
					code.add(PushI, total_size);
					code.add(Add);
					code.add(PushD, "$system-frame-pointer");
					code.add(LoadI);
					code.add(StoreI);
					code.add(Label, "--ident-return-address");
					code.add(PushD, "$system-stack-pointer");
					code.add(LoadI);
					total_size += -4;
					code.add(PushI, total_size);
					code.add(Add);
					code.add(PushI, 0);
					code.add(LoadI);
					code.add(StoreI);
					code.add(Label, "--ident-object-address");
					code.add(PushD, "$system-stack-pointer");
					code.add(LoadI);
					total_size += -4;
					code.add(PushI, total_size);
					code.add(Add);
					code.add(PushD, "$system-object-pointer");
					code.add(LoadI);
					code.add(StoreI);
					code.add(PushD, "$system-frame-pointer");
					code.add(PushD, "$system-stack-pointer");
					code.add(LoadI);
					code.add(StoreI);
					code.add(PushD, "$system-frame-pointer");
					code.add(LoadI);
					code.add(PushI, (-total_size) - 12);		
					code.add(Subtract);
					code.add(PushD, "system-stack-pointer");
					code.add(Exchange);
					code.add(StoreI);
					code.add(PushD, "$system-frame-pointer");
					code.add(PushD, "$system-stack-pointer");
					code.add(LoadI);
					code.add(StoreI);
					boolean passing = true;
					String object_name = null;
					if (node.getToken().isOperator(Operator.RESOLUTION)){
						String temp_obj = node.child(0).getToken().getLexeme();
						object_name = object_defs.get(temp_obj);
						passing = false;
					}
					if (passing == true){
					code.add(Call, function);
					}
					else
					{
					code.add(Call, function + "-" + object_name);	
					}
					arguments_table.clear();
					int size_of_function = -(total_size + 12);
					size_of_function += -(total_size);
					code.add(Label, "--ident-old-frame-pointer");
					code.add(PushD, "$system-frame-pointer");
					code.add(LoadI);
					code.add(PushI, -4);
					code.add(Add);
					code.add(LoadI);
					code.add(PushD, "$system-frame-pointer");
					code.add(Exchange);
					code.add(StoreI);
					code.add(PushD, "$system-stack-pointer");
					code.add(LoadI);
					code.add(PushI, size_of_function);		
					code.add(Add);
					code.add(PushD, "$system-stack-pointer");
					code.add(Exchange);
					code.add(StoreI);
					if (functionCall.child(0).child(0).getToken().getLexeme().equals("void")){
						
					}
					else
					{
						code.add(Label, "--return-value");
						code.add(PushD, "$system-stack-pointer");
						code.add(LoadI);
						code.add(PushI, -4);
						code.add(Add);
						code.add(Exchange);
						code.add(StoreI);
						code.add(Label, "--return-value");
						code.add(PushD, "$system-stack-pointer");
						code.add(LoadI);
						code.add(PushI, -4);
						code.add(Add);
						code.add(LoadI);
					}
					
				}
			live_obj.clear();
			return true;
		}
		
		
		
		public boolean visitLeave(ObjectNode node) {
			newVoidCode(node);
			for(ParseNode child : node.getChildren()) {			// non-routine-decls first
				ASMCodeFragment childCode = removeVoidCode(child);
				if (childCode == null){	
					code.add(Nop);
				}
				else
				{
				code.append(childCode);
				}
			}
			
//			if(node.getParent() == null) {
//			}
			return true;
		}
	
		public boolean visitLeave(ObjectBlock node) {
			newVoidCode(node);
			
			for(ParseNode child : node.getChildren()) {			// non-routine-decls first
				ASMCodeFragment childCode = removeVoidCode(child);
				if (childCode == null){	
					code.add(Nop);
				}
				else
				{
				code.append(childCode);
				}
			}
			
//			if(node.getParent() == null) {
//			}
			return true;
		}
		public boolean visitLeave(DeclarationNode node) {
			newVoidCode(node);
			
			for(ParseNode child : node.getChildren()) {			// non-routine-decls first
				ASMCodeFragment childCode = removeVoidCode(child);
				if (childCode == null){	
					code.add(Nop);
				}
				else
				{
				code.append(childCode);
				}
			}
			return true;
		}
		
		public boolean visitLeave(UnaryNode node) {
			newVoidCode(node);
			
			for(ParseNode child : node.getChildren()) {			// non-routine-decls first
				ASMCodeFragment childCode = removeVoidCode(child);
				if (childCode == null){	
					code.add(Nop);
				}
				else
				{	
				
				code.append(childCode);	
				}
			}
			
//			if(node.getParent() == null) {
//			}
			return true;
		}
////////////////////////////////////////////////////////////////////
        // Statements and decls
		public boolean visitLeave(DoStatementNode node) {
			newVoidCode(node);	
			if (node.getToken().getLexeme().equals("OD")){
				return true;
			}
			if (node.getToken().getLexeme().equals("WHILE")){
				return true;
			}
			if (node.getToken().getLexeme().equals("UNTIL")){
				return true;
			}
			final int MAXCHILDREN = node.nChildren();
			String label = labeller.newLabel("doLoop-", "");
			String label2 = labeller.newLabel("endDoLoop-", "");
			int i = 0;		
			
			code.add(Label, label);
			
			while (i < MAXCHILDREN){
				if (node.child(i).getToken().getLexeme().equals("OD")){
					break;
				}
				if (node.child(i) instanceof AssignmentStatementNode){
					AssignmentStatementNode assign = (AssignmentStatementNode)node.child(i);				
					ASMCodeFragment childCode = removeValueCode(assign);
					code.append(childCode);
				}
				if (node.child(i) instanceof BinaryOperatorNode && !((node.child(i).getParent().getToken().getLexeme().equals("WHILE") && !(node.child(i).getToken().getLexeme().equals("UNTIL"))))){
					BinaryOperatorNode assign = (BinaryOperatorNode)node.child(i);				
					ASMCodeFragment childCode = removeValueCode(assign);
					code.append(childCode);
				}
				if (node.child(i) instanceof PrintStatementNode){
					PrintStatementNode assign = (PrintStatementNode)node.child(i);				
					ASMCodeFragment childCode = removeVoidCode(assign);
					code.append(childCode);
				}
				if (!(node.child(i).getToken().getLexeme().equals("WHILE")) && !(node.child(i).getToken().getLexeme().equals("UNTIL")) && node.child(i) instanceof DoStatementNode){
					DoStatementNode assign = (DoStatementNode)node.child(i);				
					ASMCodeFragment childCode = removeVoidCode(assign);
					code.append(childCode);
				}
				if (node.child(i) instanceof IfStatementNode){
					IfStatementNode assign = (IfStatementNode)node.child(i);				
					ASMCodeFragment childCode = removeVoidCode(assign);
					code.append(childCode);
				}
				if (node.child(i) instanceof CallStatementNode){
					CallStatementNode assign = (CallStatementNode)node.child(i);				
					ASMCodeFragment childCode = removeVoidCode(assign);
					code.append(childCode);
				}
				if (node.child(i).getToken().getLexeme().equals("WHILE")){
					if (node.child(i).child(0) instanceof BooleanNode)
					{
						BooleanNode assign = (BooleanNode)node.child(i).child(0);	
						ASMCodeFragment childCode = removeValueCode(assign);
						code.append(childCode);					
						code.add(JumpFalse, label2);
					}
					else
					{	
					BinaryOperatorNode assign = (BinaryOperatorNode)node.child(i).child(0);		
					ASMCodeFragment childCode = removeValueCode(assign);
					code.append(childCode);					
					code.add(JumpFalse, label2);
					}
				}
				if (node.child(i).getToken().getLexeme().equals("UNTIL")){
					if (node.child(i).child(0) instanceof BooleanNode)
					{
						BooleanNode assign = (BooleanNode)node.child(i).child(0);	
						ASMCodeFragment childCode = removeValueCode(assign);
						code.append(childCode);					
						code.add(JumpTrue, label2);
					}
					else
					{	
					BinaryOperatorNode assign = (BinaryOperatorNode)node.child(i).child(0);		
					ASMCodeFragment childCode = removeValueCode(assign);
					code.append(childCode);					
					code.add(JumpTrue, label2);
					}
				}
				i++;
			}
			code.add(Jump, label);
			code.add(Label, label2);
			return true;
		}
		
		public boolean visitLeave(IfStatementNode node) {
			newVoidCode(node);				
			if (node.getToken().getLexeme().equals("FI")){
				return true;
			}
			final int MAXCHILDREN = node.nChildren();
			String label = labeller.newLabel("iftrue-", "");
			String label2 = labeller.newLabel("else-", "");
			String label3 = labeller.newLabel("end-", "");
			boolean elseStatementCheck = false;
			int i = 0;	
			int j = 0;
			
			if (node.child(0) instanceof BinaryOperatorNode){
				BinaryOperatorNode assign = (BinaryOperatorNode)node.child(0);				
				ASMCodeFragment childCode = removeValueCode(assign);
				code.append(childCode);				
				i++;
				if(node.child(0).child(0).getToken().isOperator(Operator.RESOLUTION))
				{
					Type this_type = node.child(0).child(1).getType();
					if (this_type.equals(PrimitiveType.FLOAT))
					{
						code.add(JumpFZero, label2);
					}
					else
					{	
						//node.child(0).child(1).getType();
						code.add(JumpFalse, label2);
					}
				}
				else if (node.child(0).getType().equals(PrimitiveType.FLOAT)){
					code.add(JumpFZero, label2);
				}
				else{
					code.add(JumpFalse, label2);
				}					
				
			}	
			else if (node.child(0) instanceof BooleanNode){
				BooleanNode assign = (BooleanNode)node.child(0);	
				ASMCodeFragment childCode = removeVoidCode(assign);
				code.append(childCode);		
				code.add(JumpFalse, label2);
				i++;
			}
			
			code.add(Label, label);
				while (i < MAXCHILDREN){
					if (node.child(i) instanceof AssignmentStatementNode){
						AssignmentStatementNode assign = (AssignmentStatementNode)node.child(i);				
						ASMCodeFragment childCode = removeValueCode(assign);
						code.append(childCode);
					}
					if (node.child(i) instanceof BinaryOperatorNode){
						BinaryOperatorNode assign = (BinaryOperatorNode)node.child(i);				
						ASMCodeFragment childCode = removeValueCode(assign);
						code.append(childCode);
					}
					if (node.child(i) instanceof PrintStatementNode){
						PrintStatementNode assign = (PrintStatementNode)node.child(i);				
						ASMCodeFragment childCode = removeVoidCode(assign);
						code.append(childCode);
					}
					if (node.child(i) instanceof DoStatementNode){
						DoStatementNode assign = (DoStatementNode)node.child(i);				
						ASMCodeFragment childCode = removeVoidCode(assign);
						code.append(childCode);
					}
					if (node.child(i) instanceof CallStatementNode){
						CallStatementNode assign = (CallStatementNode)node.child(i);				
						ASMCodeFragment childCode = removeVoidCode(assign);
						code.append(childCode);
					}
					if (node.child(i) instanceof FunctionNode){
						FunctionNode assign = (FunctionNode)node.child(i);				
						ASMCodeFragment childCode = removeVoidCode(assign);
						code.append(childCode);
					}
					if (node.child(i) instanceof IfStatementNode && !(node.child(i).getToken().getLexeme().equals("ELSE"))){
						IfStatementNode assign = (IfStatementNode)node.child(i);				
						ASMCodeFragment childCode = removeVoidCode(assign);
						code.append(childCode);
					}			
					if ((node.child(i).getToken().getLexeme().equals("ELSE"))){	
						elseStatementCheck = true;
						code.add(Jump, label3);
						code.add(Label, label2);
						IfStatementNode ifElse = (IfStatementNode)node.child(i);
						final int MAXCHILDRENELSE = ifElse.nChildren();					
						while(j < MAXCHILDRENELSE){
							if (ifElse.child(j) instanceof AssignmentStatementNode){
								AssignmentStatementNode assign = (AssignmentStatementNode)ifElse.child(j);				
								ASMCodeFragment childCode = removeValueCode(assign);
								code.append(childCode);
							}
							if (ifElse.child(j) instanceof BinaryOperatorNode){
								BinaryOperatorNode assign = (BinaryOperatorNode)ifElse.child(j);				
								ASMCodeFragment childCode = removeValueCode(assign);
								code.append(childCode);
							}
							if (ifElse.child(j) instanceof PrintStatementNode){
								PrintStatementNode assign = (PrintStatementNode)ifElse.child(j);				
								ASMCodeFragment childCode = removeVoidCode(assign);
								code.append(childCode);
							}
							if (ifElse.child(j) instanceof DoStatementNode){
								DoStatementNode assign = (DoStatementNode)ifElse.child(j);				
								ASMCodeFragment childCode = removeVoidCode(assign);
								code.append(childCode);
							}
							if (ifElse.child(j) instanceof IfStatementNode){
								IfStatementNode assign = (IfStatementNode)ifElse.child(j);				
								ASMCodeFragment childCode = removeVoidCode(assign);
								code.append(childCode);
							}
							if (ifElse.child(j) instanceof CallStatementNode){
								CallStatementNode assign = (CallStatementNode)ifElse.child(j);				
								ASMCodeFragment childCode = removeVoidCode(assign);
								code.append(childCode);
							}
							if (ifElse.child(j) instanceof FunctionNode){
								FunctionNode assign = (FunctionNode)ifElse.child(j);				
								ASMCodeFragment childCode = removeVoidCode(assign);
								code.append(childCode);
							}
							j++;
						}
						code.add(Jump, label3);
					}
					i++;
				}
			if (elseStatementCheck == false){
				code.add(Label, label2);
			}
			code.add(Label, label3);
			return true;
		}
		
		public boolean visitLeave(ProgramBlock node) {
			newVoidCode(node);
			for(ParseNode child : node.getChildren()) {			// non-routine-decls first
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
			return true;
		}
		
		public boolean visitLeave(BinaryOperatorNode node) {
			newValueCode(node);
			boolean checking = true;
			if (node.getToken().isOperator(Operator.CREATE)){
				//ASMCodeFragment	arg1 = removeValueCode(node.child(0));
				int i = 0;
				String working_object = node.child(0).getToken().getLexeme();
				while ( i < tree.nChildren()){
					String new_object = tree.child(i).getToken().getLexeme();
					if (working_object.equals(new_object)){	
						break;
					}
					i++;
				}
				Scope scope = null;;
				if(tree.child(i).getParent().child(0) instanceof ObjectBlock){
					if (tree.child(i).getParent().getToken().getLexeme().equals(node.child(0).getToken().getLexeme()))
					scope = tree.child(i).getScope();
				}
				else
				{
					scope = tree.child(i).child(0).getScope();
				}
				if(node.getParent() instanceof AssignmentStatementNode){
					if (node.getParent().child(0) instanceof DeclarationNode){
						String this_object = node.getParent().child(0).child(0).getToken().getLexeme();
						object_defs.put(this_object, working_object);
						object_nodes.put(this_object, node.getParent().child(0).child(0));
					}
					else if (node.getParent().child(0) instanceof IdentifierNode){
						String this_object = node.getParent().child(0).getToken().getLexeme();
						object_defs.put(this_object, working_object);
						object_nodes.put(this_object, node.getParent().child(0).child(0));
					}
				}
				int scope_size = scope.getAllocatedSize();
				symbolTable.SymbolTable symbol = scope.getSymbolTable();	
				code.add(PushI, scope_size);
				code.add(Call, MemoryManager.MEM_MANAGER_ALLOCATE);
				return true;
			}
			if (node.getToken().isOperator(Operator.RESOLUTION)){
				ASMCodeFragment	arg1 = removeValueCode(node.child(0));
				newVoidCode(node);
				int pass = 0;
				if (node.getParent() instanceof PrintStatementNode){
					if (node.getToken().isOperator(Operator.RESOLUTION)){
						code.append(arg1);	
						if(node.getToken().isOperator(Operator.RESOLUTION) && node.child(0).getToken().isOperator(Operator.RESOLUTION)){
							
						}
						else
						{
						code.add(LoadI);
						}
						ASMCodeFragment childCode = printCode(node.child(1));
						code.append(childCode);
					}
					return true;
				}
				else if (node.getParent() instanceof BinaryOperatorNode){
					if (node.getToken().isOperator(Operator.RESOLUTION)){
						code.append(arg1);		
						if(node.getToken().isOperator(Operator.RESOLUTION) && node.child(0).getToken().isOperator(Operator.RESOLUTION)){
							
						}
						else
						{
						code.add(LoadI);
						}
						ASMCodeFragment childCode = removeVoidCode(node.child(1));
						code.append(childCode);
						code.add(Pop);
						Scope local_scope;
						if (node.getToken().isOperator(Operator.RESOLUTION)){
							local_scope = node.getScope();
						}
						else
						{
						    local_scope = node.getLocalScope();
						}						
						Binding b = local_scope.getSymbolTable().lookup(node.child(1).getToken().getLexeme());
						int offSet = b.getMemoryLocation().getOffset();
						code.add(PushI, offSet);
						code.add(Add);
						if(node.child(1).getType().equals(PrimitiveType.INTEGER)){
							code.add(LoadI);
						}
						else if(node.child(1).getType().equals(ObjectType.OBJECT)){
							code.add(LoadI);
						}
						else if(node.child(1).getType().equals(PrimitiveType.FLOAT)){
							code.add(LoadF);
						}	
						else if(node.child(1).getType().equals(PrimitiveType.BOOLEAN)){
							code.add(LoadI);
						}
					}
					return true;
				}
				else if (node.getParent() instanceof AssignmentStatementNode){
					if (node.getToken().isOperator(Operator.RESOLUTION)){
						code.append(arg1);		
						if(node.getToken().isOperator(Operator.RESOLUTION) && node.child(0).getToken().isOperator(Operator.RESOLUTION)){
							
						}
						else
						{
						code.add(LoadI);
						}
						ASMCodeFragment childCode = removeVoidCode(node.child(1));
						code.append(childCode);							
						code.add(Pop);
						Scope local_scope;
						if (node.getToken().isOperator(Operator.RESOLUTION)){
							local_scope = node.getScope();
						}
						else
						{
						    local_scope = node.getLocalScope();
						}						
						Binding b = local_scope.getSymbolTable().lookup(node.child(1).getToken().getLexeme());
						int offSet = b.getMemoryLocation().getOffset();
						code.add(PushI, offSet);
						code.add(Add);
						code.add(LoadI);
					}
					return true;
				}
				if (node.getToken().isOperator(Operator.RESOLUTION)){
					if (node.getParent() instanceof UnaryNode){
						code.append(arg1);
						code.add(LoadI);
						ASMCodeFragment arg2 = removeVoidCode(node.child(1));
						code.append(arg2);							
						code.add(Pop);
						Scope local_scope;
						if (node.getToken().isOperator(Operator.RESOLUTION)){
							local_scope = node.getScope();
						}
						else
						{
						    local_scope = node.getLocalScope();
						}						
						Binding b = local_scope.getSymbolTable().lookup(node.child(1).getToken().getLexeme());
						int offSet = b.getMemoryLocation().getOffset();
						code.add(PushI, offSet);
						code.add(Add);
						code.add(LoadI);
						code.add(Negate);
						return true;
					}
				}
				for(ParseNode child : node.child(2).getChildren()) {
					
					if (pass == 0){
						code.append(arg1);
						if(node.getToken().isOperator(Operator.RESOLUTION) && node.child(0).getToken().isOperator(Operator.RESOLUTION)){
							
						}
						else{
							code.add(LoadI);	
						}
					}
					ASMCodeFragment childCode = removeVoidCode(child);
					code.append(childCode);
					pass++;
				}
				return true;
				
			}
			ASMCodeFragment	arg1 = removeValueCode(node.child(0));
			ASMCodeFragment arg2 = removeValueCode(node.child(1));
			if(node.getType().equals(ObjectType.OBJECT)){
				node.setType(node.child(0).getType());
			}
			else if (node.child(1) instanceof NullTypeNode)
			{
				arg1.add(LoadI);
			}			
			else if (node.getType().equals(PrimitiveType.BOOLEAN))
			{
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
					if(temp instanceof UnaryNode){
						if (temp.child(0).getToken().isOperator(Operator.RESOLUTION)){
							temp.setType(temp.child(0).child(1).getType());
						}
						else
						{
							temp.setType(temp.child(0).getType());
						}
					}
					if (temp.child(0).getType().equals(PrimitiveType.BOOLEAN) && temp.child(0) instanceof BinaryOperatorNode)
					{
						arg2.add(LoadI);
					}
					if(temp.nChildren() == 2)
					{
						if (temp.child(0).getType().equals(ObjectType.OBJECT) && temp.child(1).getType().equals(ObjectType.OBJECT)){
							if (temp.child(0) instanceof NullTypeNode){
								arg2.add(LoadI);
							}
							else if (temp.child(1) instanceof NullTypeNode){
								arg1.add(LoadI);
							}
							else
							{
								String child_1 = object_defs.get(temp.child(0).getToken().getLexeme());
								String child_2 = object_defs.get(temp.child(1).getToken().getLexeme());
								if (child_1.equals(child_2)){
									arg1.add(LoadI);
									arg2.add(LoadI);
								}
								else
								{
									errorHandler.Error new_error = new errorHandler.Error();
									new_error.reportCompilerError("Incorrect object types in boolean statement");
								}
							}
						}
					}
			}
			if (arg2 == null){
				code.append(arg1);
			}
			else
			{
			
			}
			ParseNode temp_parse;
			temp_parse = node;
			int z = 0;
			while(z < temp_parse.nChildren()){
				if (temp_parse.child(0) instanceof UnaryNode || temp_parse instanceof UnaryNode){
					if (temp_parse.child(0).child(0).getToken().isOperator(Operator.RESOLUTION)){
						temp_parse = temp_parse.child(0);
						z = 0;
						continue;
					}
					else 
					{
						if (temp_parse.child(0).nChildren() == 1){
							node.setType(temp_parse.child(0).child(0).getType());
						}
						else{
							node.setType(temp_parse.child(0).child(1).getType());
						}
					}
				}
				if (node.child(1) instanceof UnaryNode){
					if (temp_parse.child(0).child(0).getToken().isOperator(Operator.RESOLUTION)){
						temp_parse = temp_parse.child(0);
						z = 0;
						continue;
					}
					else 
					{
						node.setType(temp_parse.child(0).child(1).getType());
					}
				}
				z++;
			}
			if (node.getType().equals(ObjectType.OBJECT)){
				if (!(node.getParent().getToken().isOperator(Operator.CREATE)) && !(node.getParent().getToken().isOperator(Operator.RESOLUTION)) || (node.getParent().getToken().isOperator())){
					if (node.child(0).getToken().isOperator(Operator.RESOLUTION)){
						node.setType(node.child(0).child(1).getType());
					}
					if(!(node.child(0).getToken().isOperator(Operator.CREATE)) && !(node.child(0).getToken().isOperator(Operator.RESOLUTION))){
						if (node.child(0).getToken().isOperator()){
							node.setType(node.child(0).getType());
						}
						
					}
				}
			}
			ParseNode temp = node;
			if(temp.nChildren() == 2){
				int g = 0;
				int counter = 0;
				while(g < temp.nChildren()){					
					if (temp.child(0) instanceof UnaryNode){
						if (temp.child(0).child(0) instanceof UnaryNode){
							temp = temp.child(0);
							g = 0;
							counter++;
							continue;
						}
						else if(temp.child(0) instanceof UnaryNode){
							temp = temp.child(0);							
							counter++;
							g = 0;
							continue;
						}
						else if (temp.child(0).child(0).getType().equals(PrimitiveType.INTEGER) && temp.child(0).child(0) instanceof IdentifierNode){
							code.append(arg1);
							//code.append(arg2);
							code.add(LoadI);
							int i = 0;
							while (i <= counter){
								code.add(Negate);
								i++;
							}							
							break;
						}
						else if (temp.child(0).child(0).getType().equals(PrimitiveType.FLOAT) && temp.child(0).child(0) instanceof IdentifierNode){
							code.append(arg1);
							code.add(LoadF);
							int i = 0;
							while (i <= counter){
								code.add(FNegate);
								i++;
							}
							break;
						}
					}
					if (temp.nChildren() == 2)
					{
						if (temp.child(1) instanceof UnaryNode){
							if (temp.child(1).child(0) instanceof UnaryNode){
								temp = temp.child(1);							
								counter++;
								g = 0;
								continue;
							}
							else if(temp.child(0) instanceof UnaryNode){
								temp = temp.child(0);							
								counter++;
								g = 0;
								continue;
							}
							else if (temp.child(1).child(0).getType().equals(PrimitiveType.INTEGER) && temp.child(1).child(0) instanceof IdentifierNode){
								code.append(arg2);
								code.add(LoadI);
								int i = 0;
								while (i <= counter){
									code.add(Negate);
									i++;
								}		
								break;
							}
							else if (temp.child(1).child(0).getType().equals(PrimitiveType.FLOAT) && temp.child(1).child(0) instanceof IdentifierNode){
								code.append(arg2);
								code.add(LoadF);
								int i = 0;
								while (i <= counter){
									code.add(FNegate);
									i++;
								}	
								break;
							}
						}
					}
					g++;
				}
					if (temp.nChildren() == 1){
						if (temp.child(0).getType().equals(PrimitiveType.INTEGER)  && temp.child(0) instanceof IdentifierNode){
							code.append(arg1);
							code.add(LoadI);
							int i = 0;
							while (i < counter){
								code.add(Negate);
								i++;
							}	
							code.append(arg2);
							checking = false;
						}
						 if (temp.child(0).getType().equals(PrimitiveType.FLOAT)  && temp.child(0) instanceof IdentifierNode){
							code.append(arg1);
							code.add(LoadF);
							int i = 0;
							while (i < counter){
								code.add(FNegate);
								i++;
							}
							code.append(arg2);
							checking = false;
					}
				}
			}
			if (node.getToken().isOperator(Operator.NEGATE)){
				//code.append(arg1);
				//code.add(Negate);
			}
			else if (checking == true){
				code.append(arg1);
				code.append(arg2);
			}
			if (node.getParent() instanceof IfStatementNode){
				if (node.child(0).getType().equals(PrimitiveType.INTEGER))
				{
					code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.INTEGER));
				}
				else if(node.child(0).getType().equals(PrimitiveType.FLOAT))
				{
					code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.FLOAT));
				}
				else if(node.child(0).getType().equals(PrimitiveType.BOOLEAN))
				{
					code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.INTEGER));
				}
			}
			else
			{
				if (node.getType().equals(PrimitiveType.INTEGER))
				{
					code.add(opcodeForOperator(node.getOperator(), PrimitiveType.INTEGER));
				}
				else if(node.getType().equals(PrimitiveType.FLOAT))
				{
					code.add(opcodeForOperator(node.getOperator(), PrimitiveType.FLOAT));
				}
				else if(node.getType().equals(PrimitiveType.BOOLEAN))
				{
					ParseNode tempy = node;
					if (tempy.child(1) instanceof UnaryNode){
							int y = 0;
							while (y < tempy.nChildren()){
								if (tempy.child(0) instanceof UnaryNode){
									tempy = tempy.child(0);
									y = 0;
									continue;
								}
								if (tempy.nChildren() == 2)
								{
									    if (tempy.child(1) instanceof UnaryNode){
										tempy = tempy.child(1);
										y = 0;
										continue;
									}
								}
								y++;
							}						
						if (tempy.child(0).getType().equals(PrimitiveType.INTEGER))
						{
							code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.INTEGER));
						}
						else if (tempy.child(0).getType().equals(ObjectType.OBJECT))
						{
							code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.INTEGER));
						}
						else if(tempy.child(0).getType().equals(PrimitiveType.FLOAT))
						{
							code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.FLOAT));
						}
					}
					else if (tempy.child(1).getType().equals(PrimitiveType.INTEGER))
					{
						code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.INTEGER));
					}
					else if (tempy.child(1).getType().equals(ObjectType.OBJECT))
					{
						code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.INTEGER));
					}
					else if(tempy.child(1).getType().equals(PrimitiveType.FLOAT))
					{
						code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.FLOAT));
					}
				}
				if (node.child(0) instanceof UnaryNode){
					if (node.child(0).child(0).getType().equals(PrimitiveType.BOOLEAN) && node.child(1).getType().equals(PrimitiveType.BOOLEAN)){
						code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.INTEGER));
					}
				}
				else if (node.child(0).getType().equals(PrimitiveType.BOOLEAN) && node.child(1).getType().equals(PrimitiveType.BOOLEAN)){
					code.add(opcodeForOperatorBool(node.getOperator(), PrimitiveType.INTEGER));
				}
			}			
			if (node.getParent() instanceof UnaryNode){
				code.add(Negate);
			}
			if (node.getToken().isOperator(Operator.NEGATE) && node.getType().equals(PrimitiveType.BOOLEAN)){
				code.add(opcodeForBooleanOperatorInt(node.getOperator()));
			}
			
			return true;
		}	
		
		private ASMOpcode opcodeForOperatorBool(Operator operator, Type type) {	
			if(type == PrimitiveType.INTEGER) {
				return opcodeForBooleanOperatorInt(operator);
				
			}
			if(type == PrimitiveType.FLOAT) {
				return opcodeForBooleanOperatorFloat(operator);
			}
			return null;
		}
		
		private ASMOpcode opcodeForOperator(Operator operator, Type type) {	
			if(type == PrimitiveType.INTEGER) {
				return opcodeForIntegerOperator(operator);
				
			}
			if(type == PrimitiveType.FLOAT) {
				return opcodeForFloatOperator(operator);
			}
			if (type == PrimitiveType.BOOLEAN) {
				return opcodeForBooleanOperatorInt(operator);
			}
			return null;
		}

		private ASMOpcode opcodeForIntegerOperator(Operator operator) {
			switch(operator) {
			case ADD: 	   		return Add;
			case MINUS:			return Subtract;
			case MULTIPLY: 		return Multiply;
			case DIVIDE:		return DivideInt();
			case TOFLOAT:		return ConvertF;
			case MOD:			return Remainder;
			

			default:
				assert false : "unimplemented operator in opcodeForIntegerOperator";
			}
			return null;
		}
		private ASMOpcode opcodeForFloatOperator(Operator operator) {
			switch(operator) {
			case ADD: 	   		return FAdd;
			case MINUS:			return FSubtract;
			case MULTIPLY: 		return FMultiply;
			case DIVIDE:		return DivideFloat();
			case TOINT:			return ConvertI;
			default:
				assert false : "unimplemented operator in opcodeForFloatOperator";
			}
			return null;
		}
		
		private ASMOpcode opcodeForBooleanOperatorInt(Operator operator) {
			switch(operator) {
			case OR: 	   		return OrInt();
			case AND:			return AndInt();
			case NEGATE: 		return BNegate;
			case LESSTHAN:		return LessThanInt();
			case GREATERTHAN:	return GREATERTHANInt();
			case GREATERTHANEQUAL:	return GREATERTHANEQUALInt();
			case LESSTHENEQUAL:	return LESSTHENEQUALInt();
			case EQUAL:			return EQUALInt();
			case NOTEQUAL:		return NOTEQUALInt();
			
			default:
				assert false : "unimplemented operator in opcodeForBooleanOperator";
			}
			return null;
		}
		private ASMOpcode opcodeForBooleanOperatorFloat(Operator operator) {
			switch(operator) {
			case OR: 	   		return OrFloat();
			case AND:			return AndFloat();
			case NEGATE: 		return FNegate;
			case LESSTHAN:		return LessThanFloat();
			case GREATERTHAN:	return GREATERTHANFloat();
			case GREATERTHANEQUAL:	return GREATERTHANEQUALFloat();
			case LESSTHENEQUAL:	return LESSTHENEQUALFloat();
			case EQUAL:			return EQUALFloat();
			case NOTEQUAL:		return NOTEQUALFloat();
			
			default:
				assert false : "unimplemented operator in opcodeForBooleanOperator";
			}
			return null;
		}
		private ASMOpcode NOTEQUALFloat(){	
			String label = labeller.newLabel("NotEqual-", "");
			String label2 = labeller.newLabel("NotEqualEnd-", "");
			code.add(FSubtract);	
			code.add(JumpFZero, label);
			code.add(PushI, 1);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(PushI, 0);
			code.add(Label, label2);
			return Nop;
		}
		
		private ASMOpcode EQUALFloat(){	
			String label = labeller.newLabel("Equal-", "");
			String label2 = labeller.newLabel("EqualEnd-", "");
			code.add(FSubtract);	
			code.add(JumpFZero, label);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(PushI, 1);
			code.add(Label, label2);
			return Nop;
		}
		
		private ASMOpcode LESSTHENEQUALFloat(){	
			String label = labeller.newLabel("LessThanEqual-", "");
			String label2 = labeller.newLabel("LessThanEqualEnd-", "");
			code.add(FSubtract);	
			code.add(Duplicate);
			code.add(JumpFNeg, label);
			code.add(Duplicate);
			code.add(JumpFZero, label);
			code.add(Pop);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(Pop);
			code.add(PushI, 1);
			code.add(Label, label2);
			return Nop;
		}
		private ASMOpcode GREATERTHANEQUALFloat(){	
			String label = labeller.newLabel("GreaterThanEqual-", "");
			String label2 = labeller.newLabel("GreaterThanEqualEnd-", "");
			code.add(FSubtract);	
			code.add(Duplicate);
			code.add(JumpFPos, label);
			code.add(Duplicate);
			code.add(JumpFZero, label);
			code.add(Pop);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(Pop);
			code.add(PushI, 1);
			code.add(Label, label2);
			return Nop;
		}
		
		private ASMOpcode GREATERTHANFloat(){	
			String label = labeller.newLabel("GreaterThan-", "");
			String label2 = labeller.newLabel("GreaterThanEnd-", "");
			code.add(FSubtract);	
			code.add(JumpFPos, label);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(PushI, 1);
			code.add(Label, label2);
			return Nop;
		}
		
		private ASMOpcode LessThanFloat(){	
			String label = labeller.newLabel("LessThan-", "");
			String label2 = labeller.newLabel("LessThanEnd-", "");
			code.add(FSubtract);	
			code.add(JumpFNeg, label);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(PushI, 1);
			code.add(Label, label2);
			return Nop;
		}
		
		private ASMOpcode OrFloat(){	
			String label = labeller.newLabel("TrueOr1-", "");
			String label1 = labeller.newLabel("TrueOr-", "");
			String label2 = labeller.newLabel("FalseOr-", "");
			code.add(Exchange);
			code.add(JumpFPos, label);
			code.add(JumpFPos, label1);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(Pop);
			code.add(PushI, 1);	
			code.add(Jump, label2);
			code.add(Label, label1);
			code.add(PushI, 1);
			code.add(Label, label2);	
			return Nop;
		}
		
		private ASMOpcode AndFloat(){	
			String label = labeller.newLabel("FalseAnd1-", "");
			String label1 = labeller.newLabel("FalseAnd-", "");
			String label2 = labeller.newLabel("TrueAnd-", "");
			code.add(Exchange);
			code.add(JumpFZero, label);
			code.add(JumpFZero, label1);
			code.add(PushI, 1);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(Pop);
			code.add(PushI, 0);	
			code.add(Jump, label2);
			code.add(Label, label1);
			code.add(PushI, 0);
			code.add(Label, label2);	
			return Nop;
		}
		
		private ASMOpcode NOTEQUALInt(){	
			String label = labeller.newLabel("NotEqual-", "");
			String label2 = labeller.newLabel("NotEqualEnd-", "");
			code.add(Subtract);	
			code.add(JumpFalse, label);
			code.add(PushI, 1);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(PushI, 0);
			code.add(Label, label2);
			return Nop;
		}
		
		private ASMOpcode EQUALInt(){	
			String label = labeller.newLabel("Equal-", "");
			String label2 = labeller.newLabel("EqualEnd-", "");
			code.add(Subtract);	
			code.add(JumpFalse, label);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(PushI, 1);
			code.add(Label, label2);
			return Nop;
		}
		
		private ASMOpcode LESSTHENEQUALInt(){	
			String label = labeller.newLabel("LessThanEqual-", "");
			String label2 = labeller.newLabel("LessThanEqualEnd-", "");
			code.add(Subtract);	
			code.add(Duplicate);
			code.add(JumpNeg, label);
			code.add(Duplicate);
			code.add(JumpFalse, label);
			code.add(Pop);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(Pop);
			code.add(PushI, 1);
			code.add(Label, label2);
			return Nop;
		}
		private ASMOpcode GREATERTHANEQUALInt(){	
			String label = labeller.newLabel("GreaterThanEqual-", "");
			String label2 = labeller.newLabel("GreaterThanEqualEnd-", "");
			code.add(Subtract);	
			code.add(Duplicate);
			code.add(JumpPos, label);
			code.add(Duplicate);
			code.add(JumpFalse, label);
			code.add(Pop);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(Pop);
			code.add(PushI, 1);
			code.add(Label, label2);
			return Nop;
		}
		
		private ASMOpcode GREATERTHANInt(){	
			String label = labeller.newLabel("GreaterThan-", "");
			String label2 = labeller.newLabel("GreaterThanEnd-", "");
			code.add(Subtract);	
			code.add(JumpPos, label);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(PushI, 1);
			code.add(Label, label2);
			return Nop;
		}
		
		private ASMOpcode LessThanInt(){	
			String label = labeller.newLabel("LessThan-", "");
			String label2 = labeller.newLabel("LessThanEnd-", "");
			code.add(Subtract);	
			code.add(JumpNeg, label);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(PushI, 1);
			code.add(Label, label2);
			return Nop;
		}
		
		private ASMOpcode OrInt(){	
			String label = labeller.newLabel("TrueOr1-", "");
			String label1 = labeller.newLabel("TrueOr-", "");
			String label2 = labeller.newLabel("FalseOr-", "");
			code.add(Exchange);
			code.add(JumpTrue, label);
			code.add(JumpTrue, label1);
			code.add(PushI, 0);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(Pop);
			code.add(PushI, 1);	
			code.add(Jump, label2);
			code.add(Label, label1);
			code.add(PushI, 1);
			code.add(Label, label2);	
			return Nop;
		}
		
		private ASMOpcode AndInt(){	
			String label = labeller.newLabel("FalseAnd1-", "");
			String label1 = labeller.newLabel("FalseAnd-", "");
			String label2 = labeller.newLabel("TrueAnd-", "");
			code.add(Exchange);
			code.add(JumpFalse, label);
			code.add(JumpFalse, label1);
			code.add(PushI, 1);
			code.add(Jump, label2);
			code.add(Label, label);
			code.add(Pop);
			code.add(PushI, 0);	
			code.add(Jump, label2);
			code.add(Label, label1);
			code.add(PushI, 0);
			code.add(Label, label2);	
			return Nop;
		}

		private ASMOpcode DivideInt(){
			code.add(Duplicate);
			code.add(JumpFalse, "ERROR-PRINT");
			code.add(Jump, "NOT-NEGATIVE");
			code.add(Label, "ERROR-PRINT");
				assert false : "Divide by zero";
			code.add(Jump, "NOP");
			code.add(Label, "NOT-NEGATIVE");		
				code.add(Divide);
			code.add(Label, "NOP");	
			return Nop;	
		}
		
		private ASMOpcode DivideFloat(){
			code.add(Duplicate);
			code.add(JumpFZero, "ERROR-PRINT");
			code.add(Jump, "NOT-NEGATIVE");
			code.add(Label, "ERROR-PRINT");
				assert false : "Divide by zero";
			code.add(Jump, "NOP");
			code.add(Label, "NOT-NEGATIVE");		
				code.add(FDivide);
			code.add(Label, "NOP");	
			return Nop;
		}
		
		private ASMOpcode opcodeForMod(){
			code.add(Duplicate);
			code.add(JumpFalse, "ERROR-PRINT");
			code.add(Jump, "NOT-NEGATIVE");
			code.add(Label, "ERROR-PRINT");
				assert false : "Mod with zero";
			code.add(Jump, "NOP");
			code.add(Label, "NOT-NEGATIVE");
				code.add(Duplicate);
				code.add(StoreC);
				code.add(Exchange);				
				code.add(Duplicate);				
				code.add(Label, "MODLOOP");				
				code.add(LoadC);
				code.add(Subtract);
				code.add(Duplicate);
				code.add(JumpFalse, "NEGATIVE");
				code.add(Duplicate);
				code.add(Jump, "MODLOOP");
			code.add(Label, "NEGATIVE");
			code.add(Duplicate);
			code.add(LoadC);
			code.add(Add);
			code.add(Jump, "NOP");			
			code.add(Label, "NOP");	
			return Nop;
		}
		
		public boolean visitLeave(AssignmentStatementNode node) {
			newVoidCode(node);		
			ASMCodeFragment rvalue;
			String label = labeller.newLabel(" -system-error-runtime- ", "Runtime Error");
			String label2 = labeller.newLabel(" continue ", "");
			if (node.nChildren() < 2){	
				return true;
			}
			else
			{
				
				if (node.nChildren() == 2){
					if (node.child(1) instanceof NullTypeNode){
						if(node.child(0) instanceof DeclarationNode)
						{
						String working_object = node.child(0).getToken().getLexeme();
						String this_object = node.child(0).child(0).getToken().getLexeme();
						object_defs.put(this_object, working_object);
						}
					}
					if (!(node.child(0).getType() == null || node.child(1).getType() == null))
					{
						if(node.child(0).getType().equals(ObjectType.OBJECT) && node.child(1).getType().equals(ObjectType.OBJECT)){
							String child_1 = object_defs.get(node.child(0).getToken().getLexeme());
							String child_2 = object_defs.get(node.child(1).getToken().getLexeme());
							if (!(child_1 == null || child_2 == null)){
								if (!(child_1.equals(child_2))){
									code.add(Label, label);
									code.add(PushD, label);
									code.add(DLabel, label);
									code.add(DataC, 69);
									code.add(DataC, 114);
									code.add(DataC, 111);
									code.add(DataC, 114);
									code.add(DataC, 114);
									code.add(PushD, "$print-format-string");
									code.add(Printf);
									return true;
								}
								else
								{
									node.setType(node.child(0).getType());
									ASMCodeFragment lvalue = removeAddressCode(node.child(0));
									rvalue = removeValueCode(node.child(1));
									code.append(lvalue);
									code.append(rvalue);
									code.add(LoadI);
									code.add(StoreI);
									return true;
								}
							}
						}
					}
				}
				if (node.getParent() instanceof ObjectBlock){
					return true;
				}
					if (node.getParent().getToken().isOperator(Operator.RESOLUTION)){
						/*if (node.getParent().nChildren() > 1){
							ASMCodeFragment thisFrag = removeVoidCode(node.getParent().child(0));
							code.append(thisFrag);
							code.add(LoadI);
							code.add(Duplicate);
							code.add(JumpFalse, label);
						}*/
					node.setType(node.child(0).getType());
					ASMCodeFragment lvalue = removeAddressCode(node.getParent().child(1));
					rvalue = removeValueCode(node.child(1));
					code.append(lvalue);
					code.add(Pop);
					Scope local_scope = node.getLocalScope();				
					
					Binding b = local_scope.getSymbolTable().lookup(node.child(0).getToken().getLexeme());
					int offSet = b.getMemoryLocation().getOffset();
					code.add(PushI, offSet);
					code.add(Add);
					code.append(rvalue);
					
				}
				else
				{
				ASMCodeFragment lvalue = removeVoidCode(node.child(0));	
				if (node.child(1) instanceof NullTypeNode){
					rvalue = removeVoidCode(node.child(1));
					code.append(lvalue);
					code.append(rvalue);
					code.add(StoreI);					
					return true;
				}
				else
				{
					rvalue = removeValueCode(node.child(1));	
				}
				if (node.getType().equals(ObjectType.OBJECT)){
					code.append(rvalue);
					code.append(lvalue);
					code.add(Exchange);
					assignGivenLROnStack(node.getType());
					return true;
				}				
				code.append(lvalue);
				code.append(rvalue);
						
				
				}
				
				if (node.child(1).getToken().getLexeme() == "toint")
				{				
					node.setType(PrimitiveType.INTEGER);
				}
				else if (node.child(1).getToken().getLexeme() == "tofloat")
				{
					node.setType(PrimitiveType.FLOAT);
				}
				assignGivenLROnStack(node.getType());
				/*code.add(Jump, label2);
				code.add(Label, label);
				code.add(PushD, label);
				code.add(DLabel, label);
				code.add(DataC, 69);
				code.add(DataC, 114);
				code.add(DataC, 111);
				code.add(DataC, 114);
				code.add(DataC, 114);
				code.add(PushD, "$print-format-string");
				code.add(Printf);
				code.add(Label, label2);*/
				return true;
			}
		}

		private void assignGivenLROnStack(Type type) {
			assert type == PrimitiveType.INTEGER;

			code.add(opcodeForStore(type));
		}

		private ASMOpcode opcodeForStore(Type type) {
			if(type == PrimitiveType.INTEGER || type == ObjectType.OBJECT) {
				return StoreI;
			}
			if(type == PrimitiveType.BOOLEAN) {
				return StoreI;
			}
			if(type == PrimitiveType.FLOAT) {
				return StoreF;
			}
			if(type == PrimitiveType.STRING) {
				assert false;
			}
			return null;
		}

		public boolean visitLeave(PrintStatementNode node) {
			newVoidCode(node);			
			ParseNode temp;
			for(ParseNode child: node.getChildren()) {
				if (child instanceof UnaryNode){
					temp = child;
					int u = 0;
					while(u < temp.nChildren()){
						if (temp.child(0).getToken().isOperator(Operator.RESOLUTION)){
							child.setType(temp.child(0).child(1).getType());
						}
						else if(temp.child(0) instanceof UnaryNode)
						{
							temp = temp.child(0);
							u = 0;
							continue;
						}
						else {
							child.setType(temp.child(0).getType());
						}
						u++;
					}
				}
				if (child.getType().equals(ObjectType.OBJECT)){
					ASMCodeFragment childCode = removeValueCode(child);
					code.append(childCode);
						if(child.getParent() instanceof BinaryOperatorNode){
							
						}
						else
						{
							//code.add(LoadI);
						}
					//
				}
				else
				{
				ASMCodeFragment childCode = printCode(child);
				code.append(childCode);
				}
			}
			
			if(node.getNewline()) {
				printNewline();
			}
			return true;
		}
		private ASMCodeFragment printCode(ParseNode child) {
			String label = labeller.newLabel("boolean-true-label-", "");
			String label4 = labeller.newLabel("boolean-false-label-", "");
			String label1 = labeller.newLabel("true-bool-", "");
			String label2 = labeller.newLabel("false-label-", "");
			String label3 = labeller.newLabel("end-bool-label-", "");
			ASMCodeFragment printCode = new ASMCodeFragment(GENERATES_VOID);
			int child_spot = 0;
			int i = 0;
			ParseNode parent_child = child.getParent();
			while(i < parent_child.nChildren()){
				if (child.getToken().getLexeme().equals(parent_child.child(i).getToken().getLexeme())){
					child_spot = i;
					if(child_spot == 0){
						break;
					}
					if (parent_child.child(child_spot - 1).getType().equals(ObjectType.OBJECT)){
						String format = printFormatFor(child);
						printCode.append(removeAddressCode(child));
						printCode.add(Pop);
						Scope local_scope = child.getLocalScope();
						Binding b = local_scope.getSymbolTable().lookup(child.getToken().getLexeme());
						int offSet = b.getMemoryLocation().getOffset();
						printCode.add(PushI, offSet);
						printCode.add(Add);
						if (child.getType().equals(PrimitiveType.FLOAT)){
							printCode.add(LoadF);
						}
						else if (child.getType().equals(PrimitiveType.INTEGER)){
							printCode.add(LoadI);
						}
						if (child.getType().equals(PrimitiveType.BOOLEAN)){
							ASMCodeFragment printCode2 = new ASMCodeFragment(GENERATES_VOID);
							printCode2.add(JumpFalse, label2);
							printCode2.add(Label, label1);
							printCode2.add(PushD, label);
							printCode2.add(DLabel, label);
							printCode2.add(DataC, 116);
							printCode2.add(DataC, 114);
							printCode2.add(DataC, 117);
							printCode2.add(DataC, 101);	
							printCode2.add(DataC, 0);
							printCode2.add(PushD, "$print-format-string");
							printCode2.add(Printf);
							printCode2.add(Jump, label3);
							printCode2.add(Label, label2);
							printCode2.add(Label, label2);
							printCode2.add(PushD, label4);
							printCode2.add(DLabel, label4);
							printCode2.add(DataC, 102);
							printCode2.add(DataC, 97);
							printCode2.add(DataC, 108);
							printCode2.add(DataC, 115);
							printCode2.add(DataC, 101);
							printCode2.add(DataC, 0);
							printCode2.add(PushD, "$print-format-string");
							printCode2.add(Printf);
							printCode2.add(Label, label3);
							printCode.append(printCode2);
						}
						printCode.add(PushD, format);
						printCode.add(Printf);
						return printCode;
					}
					break;
				}
				i++;
			}
			
			String format = printFormatFor(child);
			printCode.append(removeValueCode(child));
			if (child.getType().equals(PrimitiveType.BOOLEAN)){
				ASMCodeFragment printCode2 = new ASMCodeFragment(GENERATES_VOID);
				printCode2.add(JumpFalse, label2);
				printCode2.add(Label, label1);
				printCode2.add(PushD, label);
				printCode2.add(DLabel, label);
				printCode2.add(DataC, 116);
				printCode2.add(DataC, 114);
				printCode2.add(DataC, 117);
				printCode2.add(DataC, 101);	
				printCode2.add(DataC, 0);
				printCode2.add(PushD, "$print-format-string");
				printCode2.add(Printf);
				printCode2.add(Jump, label3);
				printCode2.add(Label, label2);
				printCode2.add(Label, label2);
				printCode2.add(PushD, label4);
				printCode2.add(DLabel, label4);
				printCode2.add(DataC, 102);
				printCode2.add(DataC, 97);
				printCode2.add(DataC, 108);
				printCode2.add(DataC, 115);
				printCode2.add(DataC, 101);
				printCode2.add(DataC, 0);
				printCode2.add(PushD, "$print-format-string");
				printCode2.add(Printf);
				printCode2.add(Label, label3);
				printCode.append(printCode2);
			}
			else
			{
			printCode.add(PushD, format);
			printCode.add(Printf);
			}			
			return printCode;
		}
		
		private ASMCodeFragment printNewline() {
			code.add(PushD, RunTime.NEWLINE_PRINT_FORMAT);
			code.add(Printf);
			return code;
		}

		private String printFormatFor(ParseNode node) {
			if(node.getType() == PrimitiveType.INTEGER) {
				return RunTime.INTEGER_PRINT_FORMAT;
			}
			if(node.getType() == ObjectType.OBJECT) {
				return RunTime.INTEGER_PRINT_FORMAT;
			}
			if(node.getType() == PrimitiveType.FLOAT) {
				return RunTime.FLOATING_PRINT_FORMAT;
			}
			if(node.getType() == PrimitiveType.STRING) {
				return RunTime.STRING_PRINT_FORMAT;
			}
			if(node.getType() == PrimitiveType.BOOLEAN) {
				return RunTime.BOOLEAN_PRINT_FORMAT;
			}
			return null;
		}
		
		public boolean visit(IdentifierNode node) {
			newAddressCode(node);
			ParseNode tempNode = node;
			while(true){
				if (tempNode instanceof ProgramNode){
					break;
				}
				if (tempNode.getParent() instanceof FunctionNode){
					if (tempNode.getParent().getToken().getLexeme().equals("return")){
						while(true){
							if (tempNode.getParent() instanceof FunctionNode){
								if (tempNode.getParent().getToken().getLexeme().equals("return")){
									tempNode = tempNode.getParent();
									continue;
								}
								
								boolean passing = true;
								ParseNode temping = node;
								ParseNode tree = null;
								while (true){
									if (temping.getParent() instanceof ObjectNode){
										String ident = temping.getParent().getToken().getLexeme();
										tree = object_fun_def.get(object_fun_name.indexOf(ident));
										passing = false;
										break;
									}
									if (temping.getParent() instanceof ProgramNode){
										break;
									}
									temping = temping.getParent();
								}
								int p = 0;
								String args2 = "";
								if (tempNode.getParent().nChildren() > 1){
									if (tempNode.getParent().child(1) instanceof ExpressionList){
										while (p < tempNode.getParent().child(1).nChildren()){
											if (tempNode.getParent().child(1).child(p).getType().equals(PrimitiveType.INTEGER)){
												args2 = args2 + "-int";
											}
											else if (tempNode.getParent().child(1).child(p).getType().equals(PrimitiveType.FLOAT)){
												args2 = args2 + "-float";
											}
											else if (tempNode.getParent().child(1).child(p).getType().equals(PrimitiveType.BOOLEAN)){
												args2 = args2 + "-bool";
											}
											p++;
										}
									}
								}
								String identifier2 = tempNode.getParent().getToken().getLexeme() + args2;
								String function_name = identifier2;
								if (passing == true){
									tree = function_defs.get(function_name);
								}
								ParseNode expression_list = tree.child(0).child(1);
								String var_name = node.getToken().getLexeme();
								int childrenSize = expression_list.nChildren();
								int total_size = 0;
								int spot = -1;
								int q = 0;
								while (q < childrenSize){
									if (expression_list.child(q).getToken().getLexeme().equals(var_name)){
										spot = q;
									}					
									total_size += expression_list.child(q).getType().getSize();
									q++;
								}
								
								if (spot == -1){
									if (node.nChildren() > 0){
										if (node.child(0).getToken().getLexeme().equals("this")){
											 code.add(Label, "--ident-object-address");
											 code.add(PushD, "$system-frame-pointer");
											 code.add(LoadI);
											 code.add(PushI, -12);
											 code.add(Add);
											 code.add(LoadI);
											 Binding binding = node.getBinding();
											 code.add(Label, identifierLabel(node));
											 pushMemoryBlockFor(binding);
											 addOffsetInBlockFor(binding);
											 code.add(Pop);
											 addOffsetInBlockFor(binding);
											 return true;
										}
									}
									Binding binding = node.getBinding();
									code.add(Label, identifierLabel(node));
									pushMemoryBlockFor(binding);
									addOffsetInBlockFor(binding);
									return true;
								}
								int m = 0;
								spot = spot + 1;
								while (m < spot){
									total_size -= expression_list.child(m).getType().getSize();
									m++;
								}				
								code.add(Label, identifierLabel(node));
								code.add(PushD, "$system-frame-pointer");
								code.add(LoadI);
								code.add(PushI, total_size);
								code.add(Add);
							}
							tempNode = tempNode.getParent();
							if (tempNode instanceof FunctionBlock){
								break;
							}
							if (tempNode instanceof ProgramNode){
								break;
							}
						}
						return true;
					}
					int p = 0;
					String args2 = "";
					String function_name = null;
					if (tempNode instanceof ExpressionList){
						if (tempNode.nChildren() > 0){
							while (p < tempNode.nChildren()){
								args2 = args2 + "-" + tempNode.child(p).child(0).getToken().getLexeme();
								p++;
							}
						}
						function_name = tempNode.getParent().getToken().getLexeme() + args2;
					}
					else
					{
						ParseNode tempNode2 = tempNode;
						int e = 0;
						String identy = "";
						if (tempNode.getParent() instanceof FunctionNode){
							tempNode2 = tempNode2.getParent();
							if (tempNode2.nChildren() > 1){
								if (tempNode2.child(1) instanceof ExpressionList){
									if (tempNode2.child(1).nChildren() > 0){
										while (e < tempNode2.child(1).nChildren() ){
										identy = identy + "-" + tempNode2.child(1).child(e).child(0).getToken().getLexeme();
										e++;
										}
									}
								}
							}
							
						}
						function_name = tempNode.getParent().getToken().getLexeme() + identy;
					}
					
					ParseNode tempObjNode = node;
					boolean passing = true;
					ParseNode tree = null;;
					while(true){
						if (tempObjNode.getParent() instanceof ObjectNode){
							String object_name = tempObjNode.getParent().getToken().getLexeme();
							ParseNode obj_tree;
							int j = 0;
							
							while(j < object_fun_name.size()){
								if (object_fun_name.get(j).equals(object_name)){
									obj_tree = object_fun_def.get(j);
									String object_check = "";
									int q = 0;
									if (obj_tree.nChildren()>0){
										if (obj_tree.child(0) instanceof FunctionNode){
											if (obj_tree.child(0).nChildren() > 1){
												if (obj_tree.child(0).child(1) instanceof ExpressionList){
													while (q < obj_tree.child(0).child(1).nChildren()){
														object_check = object_check + "-" + obj_tree.child(0).child(1).child(q).child(0).getToken().getLexeme();
														q++;
													}
												}
											}
										}
									}
									object_check = tempNode.getParent().getToken().getLexeme() + object_check;
									if (object_check.equals(function_name)){
										tree = obj_tree;
										passing = false;
									}
								}
								j++;
							}
							
						}
						if (tempObjNode.getParent() instanceof ProgramNode){
							break;
						}
						tempObjNode = tempObjNode.getParent();
					}
					if (passing == true){
					 tree = function_defs.get(function_name);
					}
					ParseNode expression_list = tree.child(0).child(1);
					String var_name = node.getToken().getLexeme();
					int childrenSize = expression_list.nChildren();
					int total_size = 0;
					int spot = -1;
					int q = 0;
					while (q < childrenSize){
						if (expression_list.child(q).getToken().getLexeme().equals(var_name)){
							spot = q;
						}					
						total_size += expression_list.child(q).getType().getSize();
						q++;
					}
					if (spot == -1){
						if (node.nChildren() > 0){
							if (node.child(0).getToken().getLexeme().equals("this")){
								 code.add(PushD, "$system-object-pointer");
								 code.add(LoadI);
								 Binding binding = node.getBinding();
								 code.add(Label, identifierLabel(node));
								 pushMemoryBlockFor(binding);
								 addOffsetInBlockFor(binding);
								 code.add(Pop);
								 addOffsetInBlockFor(binding);
								 return true;
							}
						}
						Binding binding = node.getBinding();
						code.add(Label, identifierLabel(node));
						pushMemoryBlockFor(binding);
						addOffsetInBlockFor(binding);
						return true;
					}
					
					int m = 0;
					spot = spot + 1;
					while (m < spot){
						if (expression_list.nChildren() == 0){
							m++;
						}
						else
						{
						total_size -= expression_list.child(m).getType().getSize();
						m++;
						}
					}				
					code.add(Label, identifierLabel(node));
					code.add(PushD, "$system-frame-pointer");
					code.add(LoadI);
					code.add(PushI, total_size);
					code.add(Add);
					return true;
				}
				tempNode = tempNode.getParent();
				if (tempNode instanceof FunctionBlock){
					break;
				}
			}
			
			Binding binding = node.getBinding();
			code.add(Label, identifierLabel(node));
			pushMemoryBlockFor(binding);
			addOffsetInBlockFor(binding);
			return true;
		}

		private String identifierLabel(IdentifierNode node) {
			return "--ident-" + node.getToken().getLexeme();
		}

		private void pushMemoryBlockFor(Binding binding) {
			BaseAddress baseAddress = binding.getMemoryLocation().getBaseAddress();
			code.add(PushD, baseAddress.getASMVariableBlock());
			code.add(LoadI);
		}

		private void addOffsetInBlockFor(Binding binding) {
			int offset = binding.getMemoryLocation().getOffset();
			code.add(PushI, offset);
			code.add(Add);
		}

		public boolean visit(IntegerConstantNode node) {
			newValueCode(node);		
			ParseNode check;
			check = node;
			code.add(PushI, node.getValue());
			while(true)
			{
				if (check.getParent() instanceof UnaryNode){
					check = check.getParent();
					code.add(Negate);
				}
				else
				{
					break;
				}				
			}
			return true;
		}
		public boolean visit(BooleanNode node) {
			newValueCode(node);
			
			if (node.getToken().getLexeme() == "false"){
				code.add(PushI,0);
			}
			else
			{
				code.add(PushI, 1);
			}			
			return true;
		}
		public boolean visit(NullNode node) {
			//newValueCode(node);			
			//code.add(PushI, node.getValue());
			return true;
		}
		public boolean visit(FloatingConstantNode node) {
			newValueCode(node);
			ParseNode check;
			check = node;
			code.add(PushF, node.getValue());
			while(true)
			{
				if (check.getParent() instanceof UnaryNode){
					check = check.getParent();
					code.add(FNegate);
				}
				else
				{
					break;
				}				
			}
			return true;
		}
		public boolean visit(StringConstantNode node) {
			newValueCode(node);

			String label = labeller.newLabel("string-constant-", "");
			code.add(PushD, label);
			code.add(DLabel, label);
			code.add(DataS, node.getValue());
			return true;
		}
	}
}
