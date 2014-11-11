package exception;

//@author A0096529N
/**
 * IO-related exceptions, e.g file corruption, loading or saving failures.
 * 
 * @version V0.5
 */
public class StreamIOException extends Exception {

	private static final long serialVersionUID = -824071103853366825L;

	public StreamIOException() {
		super();
	}

	public StreamIOException(String message) {
		super(message);
	}

	public StreamIOException(String message, Throwable cause) {
		super(message, cause);
	}
}