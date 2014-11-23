package exception;

//@author A0093874N
/**
 * Exceptions arising due to passing restricted parameters, such as marking a
 * task as inactive.
 */
public class StreamRestriction extends Throwable {

	private static final long serialVersionUID = -4275354701724171L;

	public StreamRestriction() {
		super();
	}

	public StreamRestriction(String message) {
		super(message);
	}

	public StreamRestriction(String message, Throwable cause) {
		super(message, cause);
	}
}
