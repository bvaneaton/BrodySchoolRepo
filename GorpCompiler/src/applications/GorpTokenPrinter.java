package applications;

import inputHandler.InputHandler;
import inputHandler.PushbackCharStream;
import inputHandler.TerminatingInputHandler;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import scanner.GorpScanner;
import tokens.Token;


public class GorpTokenPrinter {
	private static final int EXIT_CODE_FOR_ERROR = 1;
	
	/** Prints tokens from a gorp file.
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		checkArguments(args);
		scanFile(args[0], System.out);
	}
	
	private static void checkArguments(String[] args) {
		if(args.length < 1 || args.length > 1) {
			System.err.println("usage: GorpTokenPrinter filename");
			System.err.println("    (use exactly one filename argument)");
			System.exit(EXIT_CODE_FOR_ERROR);
		}
	}
	/** prints a file specified by filename to the given PrintStream.
	 *  Each line of the file is preceded by its one-based line number.
	 * @param filename the name of the file to be listed.
	 * @param out the PrintStream to list to.
	 * @throws FileNotFoundException 
	 */
	public static void scanFile(String filename, PrintStream out) throws FileNotFoundException {
		System.out.println("filename " + filename);
		InputHandler handler = TerminatingInputHandler.fromFilename(filename);
		PushbackCharStream charStream = PushbackCharStream.make(handler);
		GorpScanner scanner = new GorpScanner(charStream);
		
		while(scanner.hasNext()) {
			printNextToken(out, scanner);
		}
		printNextToken(out, scanner);		// prints NullToken
	}

	private static void printNextToken(PrintStream out, GorpScanner scanner) {
		Token token = scanner.next();
		out.println(token.toString());
//		out.println(token.fullString());
	}
}
