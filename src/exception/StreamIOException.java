package exception;

//@author A0096529N
/**
 * Exceptions arising due to failure in file input/output processes, e.g file
 * corruption, loading or saving failures, and JSON conversion failures.
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