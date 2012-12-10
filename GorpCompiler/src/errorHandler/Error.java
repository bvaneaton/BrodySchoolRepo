package errorHandler;

import java.io.PrintStream;

public class Error {
	private static boolean hasErrors = false;
	private static PrintStream out = System.err;
	
	public static void reportError() {
		hasErrors = true;
	}
	public static void reportError(String message) {
		out.println(message);
		hasErrors = true;
	}
	public static boolean hasErrors() {
		return hasErrors;
	}

	public static void reportCompilerError(String message) {
		reportError("compiler error: " + message);
		throw new ErrorException(message);
	}
	
	public static class ErrorException extends RuntimeException {
		public ErrorException(String msg) {
			super(msg);
		}
		private static final long serialVersionUID = 1962939861246347041L;
	}
	
}
