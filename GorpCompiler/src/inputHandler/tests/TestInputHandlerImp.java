package inputHandler.tests;

import inputHandler.InputHandlerImp;

import java.io.FileNotFoundException;

import junit.framework.TestCase;
import static inputHandler.tests.FixtureDefinitions.*;


public class TestInputHandlerImp extends TestCase {
	protected InputHandlerImp handler;

	protected void factory(String filename) throws FileNotFoundException {
		handler = new InputHandlerImp(filename);
	}
	

	public void testHappyPath() throws FileNotFoundException {
		String filename = SIMPLE_FIXTURE_FILENAME;
		
		factory(filename);
		assertEquals(0, handler.lineNumber());
		assertEquals(filename, handler.fileName());
		
		int lineNumber = 1;
		for(String lineExpected : simpleFixtureStrings) {
			assertTrue(handler.hasNext());
			String lineRead = handler.next();
			assertEquals(lineExpected, lineRead);
			assertEquals(lineNumber++, handler.lineNumber());
		}

		assertEquals(filename, handler.fileName());
		assertFalse(handler.hasNext());
	}
	
	public void testFileNotFound() {
		try {
			factory(NONEXISTENT_FILENAME);
			fail();
		}
		catch(FileNotFoundException e) {}
	}
}
