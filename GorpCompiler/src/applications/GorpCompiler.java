package applications;

import inputHandler.InputHandler;
import inputHandler.PushbackCharStream;
import inputHandler.TerminatingInputHandler;

import java.io.FileNotFoundException;

import asmCodeGenerator.ASMCodeFragment;
import asmCodeGenerator.ASMCodeGenerator;

import parseTree.ParseNode;
import parser.GorpParser;
import scanner.GorpScanner;
import semanticAnalyzer.SemanticAnalyzer;
import errorHandler.Error;

public class GorpCompiler {
	private static final int EXIT_CODE_FOR_ERROR = 1;
	
	/** Checks syntax of an gorp1 file.
	 *  Prints filename and "done" if syntax is correct; prints errors if not.
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		checkArguments(args);
		compile(args[0]);
	}
	
	private static void checkArguments(String[] args) 
	{
		if(args.length < 1 || args.length > 1) {
			System.err.println("usage: GorpCompiler filename");
			System.err.println("    (use exactly one filename argument)");
			System.exit(EXIT_CODE_FOR_ERROR);
		}
	}
	
	
	/** analyzes a file specified by filename.
	 * @param filename the name of the file to be analyzed.
	 * @throws FileNotFoundException 
	 */
	public static void compile(String filename) throws FileNotFoundException 
	{
		InputHandler handler = TerminatingInputHandler.fromFilename(filename);
		PushbackCharStream charStream = PushbackCharStream.make(handler);
		GorpScanner scanner = new GorpScanner(charStream);
		
		GorpParser parser = new GorpParser(scanner);
		ParseNode syntaxTree = parser.parse_program();
		
		SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(syntaxTree);
		syntaxTree = semanticAnalyzer.analyze();
		
		if(!Error.hasErrors()) {
			ASMCodeGenerator codeGenerator = new ASMCodeGenerator(syntaxTree);
			ASMCodeFragment  code = codeGenerator.makeASM();

			System.out.print(code);
		}
		else {
			System.err.println("program has errors.  no executable created.");
		}
	}
}
