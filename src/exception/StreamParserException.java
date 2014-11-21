package exception;

//@author A0119401U
/**
 * Exceptions arising due to failure in command parsing, e.g unacceptable
 * command or command syntax or command arguments.
 */
public class StreamParserException extends Exception {

	private static final long serialVersionUID = 2454506083919846547L;

	public StreamParserException() {
		super();
	}

	public StreamParserException(String message) {
		super(message);
	}

	public StreamParserException(String message, Throwable cause) {
		super(message, cause);
	}
}