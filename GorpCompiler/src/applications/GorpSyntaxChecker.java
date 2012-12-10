package applications;

import inputHandler.InputHandler;
import inputHandler.PushbackCharStream;
import inputHandler.TerminatingInputHandler;

import java.io.FileNotFoundException;

import parser.GorpParser;
import scanner.GorpScanner;

public class GorpSyntaxChecker {
	private static final int EXIT_CODE_FOR_ERROR = 1;
	
	/** Checks syntax of an gorp1 file.
	 *  Prints filename and "done" if syntax is correct; prints errors if not.
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		checkArguments(args);
		parseFile(args[0]);
	}
	
	private static void checkArguments(String[] args) {
		if(args.length < 1 || args.length > 1) {
			System.err.println("usage: GorpSyntaxChecker filename");
			System.err.println("    (use exactly one filename argument)");
			System.exit(EXIT_CODE_FOR_ERROR);
		}
	}
	
	
	/** analyzes a file specified by filename.
	 * @param filename the name of the file to be analyzed.
	 * @throws FileNotFoundException 
	 */
	public static void parseFile(String filename) throws FileNotFoundException {
		System.out.println("filename " + filename);
		InputHandler handler = TerminatingInputHandler.fromFilename(filename);
		PushbackCharStream charStream = PushbackCharStream.make(handler);
		GorpScanner scanner = new GorpScanner(charStream);
		
		GorpParser parser = new GorpParser(scanner);
		parser.parse_program();

		System.out.println("done.");
	}
}
