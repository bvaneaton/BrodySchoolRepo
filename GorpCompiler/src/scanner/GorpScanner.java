package scanner;

import inputHandler.*;
import tokens.*;
import errorHandler.Error;

public class GorpScanner implements Scanner {
	private static final int MAX_IDENTIFIER_LENGTH = 42;
	private static final String OPERATOR_START_CHARS = "+-*=#/()>>=<<===<>!|&.";
	
	private Token nextToken;
	private PushbackCharStream input;
	
	private static final int IF = 100, WHILE = 101, UNTIL = 102, DO = 103, 
	INT = 104, FLOAT = 105, BOOL = 106, STRING = 109, AUTOTYPE = 110, PROG = 111, 
	GORP = 112, FI = 113, OD = 114, ELSE = 115, OBJ = 116, JBO = 117, OBJECTS = 118, CREATE = 119,
	FUN = 120, NUF = 121,VOID = 122, FUNCTION = 123, RETURN = 124;
	
	public GorpScanner(PushbackCharStream input) {
		super();
		this.input = input;
		nextToken = null;
		createNextToken();
	}

	private void createNextToken() {
		LocatedChar ch = nextNonWhitespaceChar();
		boolean integerCheck = true;
		if(isNonZeroDigit(ch)) {
			
			int countPush = 0;
			LocatedChar checking[] = new LocatedChar[256];
			checking[0] = ch;
			while(isDigit(checking[countPush])){				
				countPush++;
				checking[countPush] = input.next();
				if (checking[countPush].getCharacter() == '.'){
					while (countPush > 0){
						input.pushback(checking[countPush]);
						countPush--;
					}
					integerCheck = false;
					scanFloat(ch);
					break;
				}
			}
			if(integerCheck == true){
				while (countPush > 0){
					input.pushback(checking[countPush]);
					countPush--;
				}
				scanInteger(ch);
			}			
		}
		else if (isZeroDigit(ch)){
			LocatedChar checking = input.peek();
			if (isPeriod(checking)){
				scanFloat(ch);	
			}
			else
			{
				scanInteger(ch);
			}
		}
		else if(isResolution(ch)){
			nextToken = OperatorToken.make(ch.getLocation(), ".", Operator.RESOLUTION);
		}
		else if(isFloatStart(ch)){
			scanFloat(ch);
		}
		else if(isIdentifierChar(ch)) {
			scanIdentifier(ch);
		}
		else if(isOperatorStart(ch)) {
			scanOperator(ch);
		}
		else if(isEndOfInput(ch)) {
			nextToken = NullToken.make(ch.getLocation());
		}
		else if(isStringConstantDelimiter(ch)) {
			scanStringConstant(ch);
		}
		else if (isAutoDec(ch)){
			nextToken = KeywordToken.make(ch.getLocation(), "@", AUTOTYPE);
		}
		else {
			lexicalError(ch, "");
			createNextToken();
		}
	}

	private LocatedChar nextNonWhitespaceChar() {
		LocatedChar ch = input.next();
		while(isWhitespace(ch)) {
			ch = input.next();
		}
		return ch;
	}

	
	
//////////////////////////////////////////////////////////////////////////////////////////
//Scan an operator
//////////////////////////////////////////////////////////////////////////////////////////

	private void scanOperator(LocatedChar ch) {
		TextLocation location = ch.getLocation();
		
		switch(ch.getCharacter()) {	
		case '+':
			nextToken = OperatorToken.make(location, "+", Operator.ADD);
			break;
		case '-':
			nextToken = OperatorToken.make(location, "-", Operator.MINUS);
			break;
		case '*':
			nextToken = OperatorToken.make(location, "*", Operator.MULTIPLY);
			break;
		case '/':
			LocatedChar next = input.peek();
			boolean commentCheck = isComment(next);				
			if (commentCheck){				
				while(true){
					next = input.next();
					if (isNewline(next)){
						createNextToken();
						break;
					}
					else
					{
						continue;
					}
				}
			}
			else
			{
				nextToken = OperatorToken.make(location, "/", Operator.DIVIDE);
			}
			break;
		case '=':
			oneOrTwoCharacterOperator(location, "==", Operator.EQUAL, Operator.ASSIGN);
			break;
		case '(':
			nextToken = OperatorToken.make(location, "(", Operator.OPARENTTHESIS);
			break;
		case ')':
			nextToken = OperatorToken.make(location, ")", Operator.CPARENTTHESIS);
			break;
		case '>':
			oneOrTwoCharacterOperator(location, ">=", Operator.GREATERTHANEQUAL, Operator.GREATERTHAN);
			break;
		case '<':
			if (isNotEqual(ch)){
				input.next();
				nextToken = OperatorToken.make(location, "<>", Operator.NOTEQUAL);
			}
			else
			{
			oneOrTwoCharacterOperator(location, "<=", Operator.LESSTHENEQUAL, Operator.LESSTHAN);
			}
			break;
		case '|':
			nextToken = OperatorToken.make(location, "|", Operator.OR);
			break;
		case '!':
			nextToken = OperatorToken.make(location, "!", Operator.NEGATE);
			break;
		case '&':
			nextToken = OperatorToken.make(location, "&", Operator.AND);
			break;
		case '#':
			oneOrTwoCharacterOperator(location, "##", Operator.NO_NEWLINE, Operator.PRINT);
			break;

		default:
			throw new IllegalArgumentException("unexpected LocatedChar " + ch + "in scanOperator");
		}
	}
	private void oneOrTwoCharacterOperator(TextLocation location, String twoCharLexeme,
			Operator twoCharOperator, Operator oneCharOperator) {
		LocatedChar next = input.peek();
		if(next.getCharacter() == twoCharLexeme.charAt(1)) {
			input.next();
			nextToken = OperatorToken.make(location, twoCharLexeme, twoCharOperator);
		}
		else {
			String oneCharLexeme = twoCharLexeme.substring(0, 1);
			nextToken = OperatorToken.make(location, oneCharLexeme, oneCharOperator);
		}
	}

	
//////////////////////////////////////////////////////////////////////////////////////////
//Scan an identifier
//////////////////////////////////////////////////////////////////////////////////////////

	private void scanIdentifier(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(firstChar.getCharacter());
		appendSubsequentIdentifierChars(buffer);
		
		String lexeme = buffer.toString();
		if (lexeme.equals("mod")){
			nextToken = OperatorToken.make(firstChar.getLocation(), "%", Operator.MOD);	
		}
		else if(lexeme.equals("toint"))
		{
			nextToken = OperatorToken.make(firstChar.getLocation(), "toint", Operator.TOINT);	
		}
		else if(lexeme.equals("tofloat"))
		{
			nextToken = OperatorToken.make(firstChar.getLocation(), "tofloat", Operator.TOFLOAT);
		}
		else if(lexeme.equals("bool"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "bool", BOOL);
		}
		else if(lexeme.equals("int"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "int", INT);
		}
		else if(lexeme.equals("float"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "float", FLOAT);
		}
		else if(lexeme.equals("string"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "string", STRING);
		}
		else if(lexeme.equals("false"))
		{
			nextToken = BooleanToken.make(firstChar.getLocation(), "false");
		}
		else if(lexeme.equals("true"))
		{
			nextToken = BooleanToken.make(firstChar.getLocation(), "true");
		}
		else if (lexeme.equals("PROG"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "PROG", PROG);
		}
		else if (lexeme.equals("GORP"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "GORP", GORP);
		}
		else if (lexeme.equals("do"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "DO", DO);
		}
		else if (lexeme.equals("od"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "OD", OD);
		}
		else if (lexeme.equals("if"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "IF", IF);
		}
		else if (lexeme.equals("fi"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "FI", FI);
		}
		else if (lexeme.equals("fun"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "FUN", FUN);
		}
		else if (lexeme.equals("nuf"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "NUF", NUF);
		}
		else if (lexeme.equals("void"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "void", VOID);
		}
		else if (lexeme.equals("funcion"))
		{
			
			nextToken = KeywordToken.make(firstChar.getLocation(), "function", FUNCTION);
		}
		else if (lexeme.equals("return"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "return", RETURN);
		}
		else if (lexeme.equals("nuf"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "NUF", NUF);
		}
		else if (lexeme.equals("obj"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "OBJ", OBJ);
		}
		else if (lexeme.equals("jbo"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "JBO", JBO);
		}
		else if (lexeme.equals("objects"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "OBJECTS", OBJECTS);
		}
		else if (lexeme.equals("create"))
		{
			nextToken = OperatorToken.make(firstChar.getLocation(), "create", Operator.CREATE);
		}
		else if (lexeme.equals("while"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "WHILE", WHILE);
		}
		else if (lexeme.equals("until"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "UNTIL", UNTIL);
		}
		else if (lexeme.equals("else"))
		{
			nextToken = KeywordToken.make(firstChar.getLocation(), "ELSE", ELSE);
		}
		else if (lexeme.equals("null"))
		{
			nextToken = NullTypeToken.make(firstChar.getLocation(), "NULL");
		}
		else
		{
			nextToken = IdentifierToken.make(firstChar.getLocation(), lexeme);
		}
		if(lexeme.length() > MAX_IDENTIFIER_LENGTH) {
			identifierLexicalError(firstChar, lexeme);
		}
	}

	private void appendSubsequentIdentifierChars(StringBuffer buffer) {
		LocatedChar ch = input.next();
		while(isIdentifierChar(ch)) {
			buffer.append(ch.getCharacter());
			ch = input.next();
		}
		input.pushback(ch);
	}

	
//////////////////////////////////////////////////////////////////////////////////////////
//Scan numbers
//////////////////////////////////////////////////////////////////////////////////////////

	private void scanInteger(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(firstChar.getCharacter());
		appendSubsequentDigits(buffer);
		
		nextToken = IntegerConstantToken.make(firstChar.getLocation(), buffer.toString());
	}
	
	private void scanFloat(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();		
		buffer.append(firstChar.getCharacter());
		appendSubsequentFloat(buffer);
		
		nextToken = FloatingConstantToken.make(firstChar.getLocation(), buffer.toString());
	}

	private void appendSubsequentDigits(StringBuffer buffer) {
		if (input.equals(0)){
			
		}
		else
		{
		LocatedChar ch = input.next();
		
			while(isDigit(ch)) {
				buffer.append(ch.getCharacter());
				ch = input.next();
			}
				
		input.pushback(ch);
		}
	}
	private void appendSubsequentFloat(StringBuffer buffer) {
		LocatedChar ch = input.next();
		LocatedChar checker;
	
			while(isDigit(ch) || ch.getCharacter() == '.' || ch.getCharacter() == 'e' || ch.getCharacter() == 'E') {				
				if (ch.getCharacter() == 'e' || ch.getCharacter() == 'E'){
					checker = input.peek();
					if (checker.getCharacter() == '+' || checker.getCharacter() == '-'){
						buffer.append(ch.getCharacter());
						ch = input.next();
					}
					else{
						lexicalError(checker, "");
						break;
					}
				}
				buffer.append(ch.getCharacter());
				ch = input.next();
			}
				
		input.pushback(ch);
	}

	
//////////////////////////////////////////////////////////////////////////////////////////
// Scan string constants
//////////////////////////////////////////////////////////////////////////////////////////
	
	private void scanStringConstant(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(firstChar.getCharacter());
		appendSubsequentStringChars(buffer);
		
		String lexeme = buffer.toString();
		nextToken = StringConstantToken.make(firstChar.getLocation(), lexeme);
	}
	private void appendSubsequentStringChars(StringBuffer buffer) {
		LocatedChar ch = input.next();
		while(!isNewline(ch) && !isStringConstantDelimiter(ch)) {
			buffer.append(ch.getCharacter());
			ch = input.next();
		}
		
		if(isNewline(ch)) {
			stringLexicalError(ch, "string constant not closed by end of line");
			return;
		}
		if(buffer.length() > 255 + 2) {
			stringLexicalError(ch, "string constant exceeds 255-character limit");
		}
		buffer.append(ch.getCharacter());
	}

	

	
//////////////////////////////////////////////////////////////////////////////////////////
// LocatedCharacter predicates
//////////////////////////////////////////////////////////////////////////////////////////

	private boolean isStringConstantDelimiter(LocatedChar ch) {
		return ch.getCharacter() == '"';
	}
	private boolean isNewline(LocatedChar ch) {
		return ch.getCharacter() == '\n';
	}
	private boolean isDigit(LocatedChar lc) {
		return Character.isDigit(lc.getCharacter());
	}
	private boolean isNonZeroDigit(LocatedChar lc) {
		return isDigit(lc) && lc.getCharacter() != '0';
	}
	private boolean isPeriod(LocatedChar lc) {
		return lc.getCharacter() == '.';
	}
	private boolean isNotEqual(LocatedChar lc) {
		LocatedChar nextChar;
		nextChar = input.peek();
		return nextChar.getCharacter() == '>';
	}
	private boolean isZeroDigit(LocatedChar lc) {
		return isDigit(lc) && lc.getCharacter() == '0';
	}
	private boolean isResolution(LocatedChar lc) {
		LocatedChar nextChar;
		nextChar = input.peek();
		return lc.getCharacter() == '.' && !(isDigit(nextChar));
	}
	private boolean isFloatStart(LocatedChar lc) {
		LocatedChar nextChar;
		nextChar = input.peek();
		return lc.getCharacter() == '.' && isDigit(nextChar) || lc.getCharacter() == '0';
	}
	private boolean isWhitespace(LocatedChar lc) {
		return Character.isWhitespace(lc.getCharacter());
	}
	private boolean isComment(LocatedChar next) {
		char c = next.getCharacter();
		return c == '/';
	}
	private boolean isIndentifierDigitcase(LocatedChar lc) {
		char c = lc.getCharacter();
		return '0' <= c && c <= '9';
	}
	private boolean isUnderScorecase(LocatedChar lc) {
		char c = lc.getCharacter();
		return c == '_';
	}
	private boolean isLowercase(LocatedChar lc) {
		char c = lc.getCharacter();
		return 'a' <= c && c <= 'z';
	}
	private boolean isUppercase(LocatedChar lc) {
		char c = lc.getCharacter();
		return 'A' <= c && c <= 'Z';
	}
	private boolean isIdentifierChar(LocatedChar lc) {
		boolean lowerCheck = isLowercase(lc);
		boolean upperCheck = isUppercase(lc);
		boolean digitCheck = isIndentifierDigitcase(lc);
		boolean underScoreCheck = isUnderScorecase(lc);
		return (lowerCheck || upperCheck || digitCheck || underScoreCheck);
	}
	private boolean isEndOfInput(LocatedChar lc) {
		return lc == LocatedCharStream.FLAG_END_OF_INPUT;
	}
	private boolean isOperatorStart(LocatedChar lc) {
		char c = lc.getCharacter();
		return isInString(c, OPERATOR_START_CHARS);
	}
	private boolean isInString(char c, String string) {
		return string.indexOf(c) != -1 ;
	}
	private boolean isAutoDec(LocatedChar lc) {
		char c = lc.getCharacter();
		return c == '@';
	}
	
	
//////////////////////////////////////////////////////////////////////////////////////////
// Iterator<Token>
//////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		return !(nextToken instanceof NullToken);
	}

	@Override
	public Token next() {
		Token result = nextToken;
		createNextToken();
		return result;
	}
	
	/**
	 * remove is an unsupported operation.  It throws an UnsupportedOperationException.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	
	
//////////////////////////////////////////////////////////////////////////////////////////
//error-reporting
//////////////////////////////////////////////////////////////////////////////////////////

	private void lexicalError(LocatedChar ch, String place) {
		String placeLogString = "";
		if(place != null && place != "") {
			placeLogString = " in " + place;
		}

		Error.reportError("Lexical error: invalid character " + ch + placeLogString);
	}
	private void identifierLexicalError(LocatedChar ch, String lexeme) {
		Error.reportError("Lexical error: identifier " + lexeme + " too long at " + ch.getLocation());
	}
	private void stringLexicalError(LocatedChar ch, String message) {
		Error.reportError("Lexical error: " + message + " at " + ch.getLocation());
	}
}
