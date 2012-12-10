package parser;

import java.util.Stack;

public class ExtraPeekableStack<T> extends Stack<T> {
	private static final long serialVersionUID = -7272698990373023069L;
	
	/** Return the element <code>depth</code> elements down from the top.
	 *  The top of the stack is at depth 0.
	 * @param depth  how far down the stack to look
	 * @return stack element <code>depth</code> from the top.
	 * @throws ArrayIndexOutOfBoundsException if the index is too large or too small.
	 */
	public T peek(int depth) {
		int topIndex = size() - 1;
		int index = topIndex - depth;
		return this.get(index);
	}
}
