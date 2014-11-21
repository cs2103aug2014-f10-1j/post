package exception;

//@author A0118007R
/**
 * Exceptions arising due to failure in task parameter modification, e.g
 * modification failures, e.g duplicate names, invalid start/end time, ...
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