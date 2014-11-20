package logger;

import logger.StreamLogger.LogLevel;

public abstract class Loggable {

	StreamLogger logger = StreamLogger.init(getComponentName());	
	
	public abstract String getComponentName();
	
	public void logDebug(String logMsg) {
		logger.log(LogLevel.DEBUG, logMsg);
	}
	
	public void logError(String logMsg) {
		logger.log(LogLevel.ERROR, logMsg);
	}
	
}
