package exception;

//@author A0096529N
/**
 * Task modification failures, e.g duplicate names, task with specified name not
 * found, ...
 * 
 * @version V0.5
 */
public class StreamModificationException extends Exception {

	private static final long serialVersionUID = 5826059852221730368L;

	public StreamModificationException() {
		super();
	}

	public StreamModificationException(String message) {
		super(message);
	}

	public StreamModificationException(String message, Throwable cause) {
		super(message, cause);
	}
}