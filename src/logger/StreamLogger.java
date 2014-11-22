package logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//@author A0096529N
/**
 * <h1>StreamLogger - STREAM's logging component</h1>
 * 
 * <p>
 * Stores log messages in a synchronized list, allowing different component to
 * contribute appropriate log messages.
 * </p>
 * 
 * <h3>Example</h3>
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	// Initialize logger, similar to Object.getInstance() taught.
 * 	StreamLogger logger = StreamLogger.init(componentName);
 * 
 * 	// Use logger to add log to log stack
 * 	logger.log(LogLevel.DEBUG, logMessage);
 * }
 * </pre>
 * 
 * <p>
 * Refer to method documentation for details.
 * </p>
 */
public class StreamLogger {

	private String componentName;
	private static final ArrayList<String> logStack = new ArrayList<String>();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	private static final String LOG_FORMAT = "%1$s %2$s [%3$s] %4$s";

	public enum LogLevel {
		DEBUG, INFO, WARNING, ERROR, FATAL;
	}

	/**
	 * Lazy constructor for StreamLogger to obtain an instance.
	 * 
	 * @param componentName
	 *            standardized name of component
	 * @return StreamLogger instance for use to log
	 */
	public static StreamLogger init(String componentName) {
		StreamLogger logger = new StreamLogger();
		logger.componentName = componentName;
		return logger;
	}

	/**
	 * Adds log message to synchronized log stack.
	 * 
	 * @param logLevel
	 *            importance level of log message
	 * @param message
	 *            the log message to be logged
	 */
	public void log(LogLevel logLevel, String message) {
		synchronized (logStack) {
			logStack.add(String.format(LOG_FORMAT, getDate(),
					getLevel(logLevel), componentName, message));
		}
	}

	/**
	 * Returns a copy of the log stack
	 * 
	 * @return logStack list of log messages
	 */
	public static ArrayList<String> getLogStack() {
		return new ArrayList<String>(logStack);
	}

	private static String getDate() {
		return DATE_FORMAT.format(new Date());
	}

	private static String getLevel(LogLevel logLevel) {
		switch (logLevel) {
			case DEBUG:
				return "DEBUG";
			case INFO:
				return "INFO";
			case WARNING:
				return "WARNING";
			case ERROR:
				return "ERROR";
			case FATAL:
				return "FATAL";
		}
		return null;
	}

}
