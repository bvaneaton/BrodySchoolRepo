package tokens;

public enum Operator 
{
	ADD("+"),
	MINUS("-"),
	MULTIPLY("*"),
	DIVIDE("/"),
	MOD("%"),
	COMMENT("//"),
	ASSIGN("="),
	OPARENTTHESIS("("),
	CPARENTTHESIS(")"),
	GREATERTHAN(">"),
	LESSTHAN("<"),
	GREATERTHANEQUAL(">="),
	LESSTHENEQUAL("<="),
	EQUAL("=="),
	NOTEQUAL("<>"),
	NEGATE("!"),
	OR("|"),
	CREATE("create"),
	RESOLUTION("."),
	AND("&"),
	TOFLOAT("tofloat"),
	TOINT("toint"),
	PRINT("#"),
	NO_NEWLINE("##"),
	NULLOP("\"\""), ;
	
	private final String lexeme;
	private Operator(String lexeme) {
		this.lexeme = lexeme;
	}
	public String getLexeme() {
		return lexeme;
	}
}
