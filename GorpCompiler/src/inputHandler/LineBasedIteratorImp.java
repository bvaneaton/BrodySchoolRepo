package inputHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


/** Simple line-based file reader.
 *  The lines returned DO NOT include the line terminator.
 * @author shermer
 *
 */
public class LineBasedIteratorImp implements LineBasedIterator {
	private BufferedReader reader;
	private String nextLine = null;

	public LineBasedIteratorImp(String filename) throws FileNotFoundException {
		this.reader = openFile(filename);
		preloadNextLine();
	}
	
//////////////////////////////////////////////////////////////////////////////
// interface: just an iterator

	@Override
	public boolean hasNext() {
		return nextLine != null;
	}

	@Override
	public String next() {
		String result = nextLine;
		preloadNextLine();
		return result;
	}

	/**
	 * remove is an unsupported operation.  It throws an UnsupportedOperationException.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	
//////////////////////////////////////////////////////////////////////////////
// private parts

	private BufferedReader openFile(String filename)
			throws FileNotFoundException {
		File f = new File(filename);
		FileInputStream fstream = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fstream);
		return new BufferedReader(isr);
	}
	
	private void preloadNextLine() {
		nextLine  = readOneLine();
	}
	private String readOneLine() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			System.err.println("Input file read error.");
			return null;
		}
	};
}
