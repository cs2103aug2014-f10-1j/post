package exception;

//@author A0093874N
/**
 * Exceptions arising due to retrieval failure, e.g asking for task number 5
 * when there are only 3 tasks or task name that is not added.
 */
public class StreamRetrievalException extends Exception {

	private static final long serialVersionUID = -3890719265729220267L;

	public StreamRetrievalException() {
		super();
	}

	public StreamRetrievalException(String message) {
		super(message);
	}

	public StreamRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}
}
