package applications;

import inputHandler.InputHandler;
import inputHandler.PushbackCharStream;
import inputHandler.TerminatingInputHandler;

import java.io.FileNotFoundException;

import parseTree.ParseNode;
import parser.GorpParser;
import scanner.GorpScanner;
import semanticAnalyzer.SemanticAnalyzer;

public class GorpSemanticChecker {
	private static final int EXIT_CODE_FOR_ERROR = 1;
	
	/** Checks syntax of a gorp file.
	 *  Prints filename and "done" if syntax is correct; prints errors if not.
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		checkArguments(args);
		checkFileSemantics(args[0]);
	}
	
	private static void checkArguments(String[] args) {
		if(args.length < 1 || args.length > 1) {
			System.err.println("usage: GorpSemanticChecker filename");
			System.err.println("    (use exactly one filename argument)");
			System.exit(EXIT_CODE_FOR_ERROR);
		}
	}
	
	
	/** analyzes a file specified by filename.
	 * @param filename the name of the file to be analyzed.
	 * @throws FileNotFoundException 
	 */
	public static void checkFileSemantics(String filename) throws FileNotFoundException {
		System.out.println("filename " + filename);
		
		InputHandler handler = TerminatingInputHandler.fromFilename(filename);
		PushbackCharStream charStream = PushbackCharStream.make(handler);
		GorpScanner scanner = new GorpScanner(charStream);
		
		GorpParser parser = new GorpParser(scanner);
		ParseNode program = parser.parse_program();
		

		SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(program);
		ParseNode syntaxTree = semanticAnalyzer.analyze();
		
		String tree = syntaxTree.toString();
		System.out.println(tree);
		System.out.println("done.");
	}
}
