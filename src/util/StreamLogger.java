package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//@author A0096529N
/**
 * <h1>StreamLogger - Stream logger component</h1>
 * 
 * <p>
 * Stores logs in a synchronized list for various components.
 * </p>
 * 
 * <h2>Example</h2>
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
 * 
 * @version V0.5
 */
public class StreamLogger {
	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	private String componentName;
	private static final List<String> logStack = new ArrayList<String>();
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
					getLevel(logLevel), componentName.toUpperCase(), message));
		}
	}

	/**
	 * Returns a copy of the log stack
	 * 
	 * @return logStack list of log messages
	 */
	public static List<String> getLogStack() {
		return new ArrayList<String>(logStack);
	}

	private static String getDate() {
		return format.format(new Date());
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
