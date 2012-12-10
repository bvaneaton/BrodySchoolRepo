package inputHandler.tests;

import inputHandler.InputHandler;
import inputHandler.TerminatingInputHandler;

import java.io.FileNotFoundException;

import junit.framework.TestCase;
import static inputHandler.tests.FixtureDefinitions.*;


public class TestTerminatingInputHandler extends TestCase {
	protected InputHandler handler;

	protected void factory(String filename, String terminator) throws FileNotFoundException {
		handler = TerminatingInputHandler.fromFilename(filename, terminator);
	}
	

	public void testHappyPath() throws FileNotFoundException {
		happyPath("\n");
	}
	public void testHappyPath2() throws FileNotFoundException {
		happyPath("aa");
	}
	
	public void happyPath(String terminator) throws FileNotFoundException {
		String filename = SIMPLE_FIXTURE_FILENAME;
		
		factory(filename, terminator);
		assertEquals(0, handler.lineNumber());
		assertEquals(filename, handler.fileName());
		
		int lineNumber = 1;
		for(String lineExpected : simpleFixtureStrings) {
			assertTrue(handler.hasNext());
			String lineRead = handler.next();
			assertEquals(lineExpected + terminator, lineRead);
			assertEquals(lineNumber++, handler.lineNumber());
		}

		assertEquals(filename, handler.fileName());
		assertFalse(handler.hasNext());
	}
	
	public void testFileNotFound() {
		try {
			factory(NONEXISTENT_FILENAME, "b");
			fail();
		}
		catch(FileNotFoundException e) {}
	}
}
