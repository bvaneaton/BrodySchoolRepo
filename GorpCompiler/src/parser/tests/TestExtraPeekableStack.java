package parser.tests;

import parser.ExtraPeekableStack;
import junit.framework.TestCase;

public class TestExtraPeekableStack extends TestCase {
	private static final int STACK_SIZE = 5;
	ExtraPeekableStack<Integer> stack;
	
	protected void setUp() throws Exception {
		super.setUp();
		stack = new ExtraPeekableStack<Integer>();	
		
		for(int i = 1; i<=STACK_SIZE; i++) {
			stack.push(i);
		}
	}
	
	public void testPeek() {
		for(int j = 0; j<STACK_SIZE; j++) {
			int peek = stack.peek(j);
			int expected = STACK_SIZE-j;
			assertEquals(expected, peek);
		}
	}
	
	public void testTooFar() {
		tryBadIndex(STACK_SIZE);
		tryBadIndex(STACK_SIZE+1);
	}
	public void testNotFarEnough() {
		tryBadIndex(-1);
		tryBadIndex(-5);
	}

	private void tryBadIndex(int index) {
		try {
			stack.peek(index);
			fail();
		}
		catch (ArrayIndexOutOfBoundsException e) {
		}
	}
}
