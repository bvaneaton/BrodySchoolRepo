package inputHandler;

public interface InputHandler extends LineBasedIterator {
	public int lineNumber();
	public String fileName();
}
