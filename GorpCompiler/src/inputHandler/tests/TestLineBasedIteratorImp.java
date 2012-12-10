package inputHandler.tests;

import java.io.FileNotFoundException;

import inputHandler.LineBasedIterator;
import inputHandler.LineBasedIteratorImp;
import junit.framework.TestCase;

import static inputHandler.tests.FixtureDefinitions.*;

public class TestLineBasedIteratorImp extends TestCase {
	protected LineBasedIterator handler;

	protected void factory(String filename) throws FileNotFoundException {
		handler = new LineBasedIteratorImp(filename);
	}
	
	
	public void testHappyPath() throws FileNotFoundException {
		factory(SIMPLE_FIXTURE_FILENAME);
		
		for(String lineExpected : simpleFixtureStrings) {
			assertTrue(handler.hasNext());
			String lineRead = handler.next();
			assertEquals(lineExpected, lineRead);
		}
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
