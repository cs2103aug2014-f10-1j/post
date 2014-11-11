package logic;

import util.StreamLogger;
import util.StreamLogger.LogLevel;

//@author A0096529N
/**
 * Base logic class that comes with basic logging
 * implementation infrastructure.
 * 
 * <ul>
 * <li>logDebug(String message)</li>
 * <li>logInfo(String message)</li>
 * <li>logWarning(String message)</li>
 * <li>logError(String message)</li>
 * <li>logFatal(String message)</li>
 * </ul>
 * 
 */
public abstract class BaseLogic {
	protected StreamLogger logger = StreamLogger.init(getLoggerComponentName());
	
	/**
	 * For setting up logging infrastructure.
	 * 
	 * @return componentTag the inherited logic
	 * class' log tag.
	 */
	protected abstract String getLoggerComponentName();
	protected void logDebug(String message) {
		logger.log(LogLevel.DEBUG, message);
	}
	protected void logInfo(String message) {
		logger.log(LogLevel.INFO, message);
	}
	protected void logWarning(String message) {
		logger.log(LogLevel.WARNING, message);
	}
	protected void logError(String message) {
		logger.log(LogLevel.ERROR, message);
	}
	protected void logFatal(String message) {
		logger.log(LogLevel.FATAL, message);
	}
}
