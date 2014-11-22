package fileio;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import logger.Loggable;
import model.StreamTask;

import org.json.JSONException;

import util.StreamConstants;
import exception.StreamIOException;

//@author A0096529N
/**
 * <h1>StreamIO - STREAM’s file input/output component.</h1>
 * <p>
 * Its purpose is to save and load STREAM’s state and also to save the log file.
 * </p>
 * 
 * <h3>Storage Format</h3>
 * <p>
 * Application state is serialized into JSON format.
 * </p>
 * 
 * <h3>Storage Location</h3>
 * <p>
 * Save location defaults to user's home directory, and may not be modified.
 * However, the file name can be changed to support multiple states.
 * </p>
 * 
 * <h3>API</h3>
 * <ul>
 * <li>StreamIO.save(Map&lt;String, StreamTask&gt; taskMap, List&lt;String&gt;
 * taskList)</li>
 * <li>StreamIO.load(Map&lt;String, StreamTask&gt; taskMap, List&lt;String&gt;
 * taskList)</li>
 * <li>StreamIO.setFilename()</li>
 * <li>StreamIO.setSaveLocation(String saveLocation)</li>
 * <li>StreamIO.saveLogFile(List&lt;String&gt; logMessages, String logFileName)</li>
 * </ul>
 * <p>
 * Refer to method documentation for details.
 * </p>
 */
public class StreamIO extends Loggable {

	static final String KEY_TASKMAP = "allTasks";
	static final String KEY_TASKLIST = "taskList";
	static final String KEY_STARTTIME = "startTime";
	static final String KEY_DEADLINE = "deadline";
	static final String KEY_NAME = "taskName";
	static final String KEY_DESCRIPTION = "taskDescription";
	static final String KEY_TAGS = "tags";
	static final String KEY_DONE = "done";
	static final String KEY_RANK = "rank";

	static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.ENGLISH);
	String STREAM_FILENAME = "default.json";

	static SaveIO saver = SaveIO.init();
	static LoadIO loader = LoadIO.init();
	
	private StreamIO(String filename) {
		this.STREAM_FILENAME = filename;
	}

	public static StreamIO init(String filename) {
		return new StreamIO(filename);
	}

	@Override
	public String getComponentName() {
		return "STREAMIO";
	}

	/**
	 * Reads and inflate the contents of serialized storage file into
	 * StreamObject.
	 * 
	 * @throws StreamIOException
	 *             when JSON conversion fail due file corruption or IO failures
	 *             when loading/accessing storage file.
	 */
	public void load(Map<String, StreamTask> taskMap, List<String> taskList)
			throws StreamIOException {
		assert (taskMap != null && taskList != null);
		try {
			File streamFile = new File(getStorageFile(STREAM_FILENAME));
			loader.load(streamFile, taskMap, taskList);
			logDebug("Loaded file: " + STREAM_FILENAME);
		} catch (JSONException e) {
			logDebug("JSON conversion failed: " + STREAM_FILENAME);
			throw new StreamIOException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage(), e);
		} catch (IOException e) {
			logDebug("File not found: " + STREAM_FILENAME);
			throw new StreamIOException("Could not load file - "
					+ e.getMessage(), e);
		}
	}

	/**
	 * Serializes and write the contents of StreamObject into storage file.
	 * 
	 * @param taskMap
	 *            map of tasks to be saved
	 * @param taskList
	 *            list of task names
	 * @throws StreamIOException
	 *             when JSON conversion fail due file corruption or IO failures
	 *             when loading/accessing storage file.
	 */
	public void save(Map<String, StreamTask> taskMap, List<String> taskList)
			throws StreamIOException {
		assert (taskMap != null && taskList != null);
		try {
			File streamFile = new File(getStorageFile(STREAM_FILENAME));
			saver.save(streamFile, taskMap, taskList);
			logDebug("Saved to file: " + getSaveLocation());
		} catch (JSONException e) {
			logDebug("JSON conversion failed during save - " + e.getMessage());
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		} catch (IOException e) {
			logDebug("IO failure during save - " + e.getMessage());
			throw new StreamIOException("Could not save to file - "
					+ e.getMessage(), e);
		}
	}

	/**
	 * Set the filename for saving.
	 * 
	 * @param saveFileName
	 *            filename of storage file to save.
	 */
	public void setFilename(String saveFileName) {
		this.STREAM_FILENAME = saveFileName;
	}

	/**
	 * Get the absolute path of save file's location
	 * 
	 * @return file path of the save location.
	 * @throws StreamIOException
	 */
	public String getSaveLocation() throws StreamIOException {
		return new File(getStorageFile(STREAM_FILENAME)).getAbsolutePath();
	}

	private String getUserHomeDirectory() {
		String dir = null;
		try {
			dir = System.getProperty("user.home");
		} catch (Exception e) {
			logError(String.format(
					StreamConstants.LogMessage.LOAD_FAIL_USER_HOME, e
							.getClass().getSimpleName(), e.getMessage()));
		}
		return dir == null ? "" : dir + File.separator;
	}

	private String getStreamDirectory() throws StreamIOException {
		String dir = getUserHomeDirectory() + "Documents" + File.separator
				+ "Stream" + File.separator;
		File streamDirectory = new File(dir);
		if (!streamDirectory.exists()) {
			if (!streamDirectory.mkdirs())
				throw new StreamIOException(
						StreamConstants.ExceptionMessage.ERR_CREATE_STREAM_DIR);
		}
		return dir;
	}

	private String getStorageFile(String filename) throws StreamIOException {
		return getStreamDirectory() + filename;
	}

	private String getLogsDirectory() throws StreamIOException {
		String dir = getStreamDirectory() + "Logs" + File.separator;
		File streamDirectory = new File(dir);
		if (!streamDirectory.exists()) {
			if (!streamDirectory.mkdirs())
				throw new StreamIOException(
						StreamConstants.ExceptionMessage.ERR_CREATE_LOG_DIR);
		}
		return dir;
	}

	private String getLogsStorageFile(String logFileName)
			throws StreamIOException {
		return getLogsDirectory() + logFileName;
	}

	//@author A0093874N
	/**
	 * Saves the log file upon exiting.
	 * 
	 * @param logMessages
	 *            - the list of log messages to be stored
	 * @param logFileName
	 *            - the name of the log file
	 * @throws StreamIOException
	 *             if IO failures encountered during accessing of log file.
	 */
	public void saveLogFile(List<String> logMessages, String logFileName)
			throws StreamIOException {
		try {
			saver.saveLogFile(logMessages, getLogsStorageFile(logFileName));
		} catch (IOException e) {
			throw new StreamIOException(
					StreamConstants.ExceptionMessage.ERR_SAVE_LOG, e);
		}
	}

}