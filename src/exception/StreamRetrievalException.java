package exception;

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
