package parseTree;

// Hierarchical Visitor pattern
public interface ParseNodeVisitor {
	// return values for all methods indicate whether to visit the children of this node.
	
	// non-leaf nodes
	boolean visitEnter(ParseNode node);
	boolean visitLeave(ParseNode node);
	
	boolean visitEnter(PrintStatementNode node);
	boolean visitLeave(PrintStatementNode node);
	
	boolean visitEnter(AssignmentStatementNode node);
	boolean visitLeave(AssignmentStatementNode node);
	
	boolean visitEnter(BinaryOperatorNode node);
	boolean visitLeave(BinaryOperatorNode node);
	
	boolean visitEnter(UnaryNode node);
	boolean visitLeave(UnaryNode node);
	
	boolean visitEnter(StatementListNode node);
	boolean visitLeave(StatementListNode node);
	
	boolean visitEnter(ExpressionList node);
	boolean visitLeave(ExpressionList node);
	
	boolean visitEnter(CallStatementNode node);
	boolean visitLeave(CallStatementNode node);
	
	boolean visitEnter(ObjectStatementListNode node);
	boolean visitLeave(ObjectStatementListNode node);
	
	boolean visitEnter(ProgramBlock node);
	boolean visitLeave(ProgramBlock node);
	
	boolean visitEnter(ProgramNode node);
	boolean visitLeave(ProgramNode node);
	
	boolean visitEnter(DoStatementNode node);
	boolean visitLeave(DoStatementNode node);
	
	boolean visitEnter(IfStatementNode node);
	boolean visitLeave(IfStatementNode node);
	
	boolean visitEnter(DeclarationNode node);
	boolean visitLeave(DeclarationNode node);
	
	boolean visitEnter(ObjectNode node);
	boolean visitLeave(ObjectNode node);
	
	boolean visitEnter(ObjectBlock node);
	boolean visitLeave(ObjectBlock node);
	
	boolean visitEnter(FunctionNode node);
	boolean visitLeave(FunctionNode node);
	
	boolean visitEnter(FunctionBlock node);
	boolean visitLeave(FunctionBlock node);
	
	// leaf nodes	
	boolean visit(BooleanNode node);
	boolean visit(IdentifierNode node);
	boolean visit(IntegerConstantNode node);
	boolean visit(FloatingConstantNode node);
	boolean visit(StringConstantNode node);
	boolean visit(NullTypeNode node);
	boolean visit(NullNode node);
	boolean visit(TypeNode node);
	boolean visit(DoStatementNode node);
	

	// convenience implementations
	public static class Default implements ParseNodeVisitor
	{
		public boolean defaultVisitation(ParseNode node) {
			return true;
		}
		public boolean defaultVisitEnter(ParseNode node) {
			return defaultVisitation(node);
		}
		public boolean defaultVisitLeave(ParseNode node){
			return defaultVisitation(node);
		}
		public boolean defaultVisit(ParseNode node) {
			return defaultVisitation(node);
		}

		public boolean visitEnter(ParseNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(ParseNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(PrintStatementNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(PrintStatementNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(AssignmentStatementNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(AssignmentStatementNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(BinaryOperatorNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(BinaryOperatorNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(UnaryNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(UnaryNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(StatementListNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(StatementListNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(ExpressionList node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(ExpressionList node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(CallStatementNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(CallStatementNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(ObjectStatementListNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(ObjectStatementListNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(ProgramBlock node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(ProgramBlock node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(ProgramNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(ProgramNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(DoStatementNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(DoStatementNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(IfStatementNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(IfStatementNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(DeclarationNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(DeclarationNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(ObjectNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(ObjectNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(ObjectBlock node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(ObjectBlock node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(FunctionNode node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(FunctionNode node) {
			return defaultVisitLeave(node);
		}
		public boolean visitEnter(FunctionBlock node) {
			return defaultVisitEnter(node);
		}
		public boolean visitLeave(FunctionBlock node) {
			return defaultVisitLeave(node);
		}

		public boolean visit(IdentifierNode node) {
			return defaultVisit(node);
		}
		public boolean visit(IntegerConstantNode node) {
			return defaultVisit(node);
		}
		public boolean visit(FloatingConstantNode node) {
			return defaultVisit(node);
		}
		public boolean visit(StringConstantNode node) {
			return defaultVisit(node);
		}
		public boolean visit(BooleanNode node) {
			return defaultVisit(node);
		}
		public boolean visit(NullTypeNode node) {
			return defaultVisit(node);
		}
		public boolean visit(NullNode node) {
			return true;
		}
		public boolean visit(TypeNode node) {
			return true;
		}
		public boolean visit(DoStatementNode node) {
			return true;
		}
	}
	public static class DefaultPostorder extends Default {
		///////////////////////////////////////////////////////////////////////////
		// assert false if we've added a node type but not a way to visit it.
		//
		@Override
		public boolean defaultVisitLeave(ParseNode node) {
			return defaultPostorder(node);
		}
		@Override
		public boolean defaultVisit(ParseNode node) {
			return defaultPostorder(node);
		}
		public boolean defaultPostorder(ParseNode node) {
			String classname = this.getClass().getSimpleName();
			String nodename  = node.getClass().getSimpleName();
			assert false : "Node type unimplemented in " + classname + ": " + nodename;
			return true;
		}
	}
	public static class DefaultPreorder extends Default {
		///////////////////////////////////////////////////////////////////////////
		// assert false if we've added a node type but not a way to visit it.
		//
		@Override
		public boolean defaultVisitEnter(ParseNode node) {
			return defaultPreorder(node);
		}
		@Override
		public boolean defaultVisit(ParseNode node) {
			return defaultPreorder(node);
		}
		public boolean defaultPreorder(ParseNode node) {
			String classname = this.getClass().getSimpleName();
			String nodename  = node.getClass().getSimpleName();
			assert false : "Node type unimplemented in " + classname + ": " + nodename;
			return true;
		}
	}
}
