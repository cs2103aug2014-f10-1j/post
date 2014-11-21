package stream;

import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.ImageIcon;

import logger.Loggable;
import logger.StreamLogger;
import logic.StreamLogic;
import model.StreamObject;
import model.StreamTask;
import ui.StreamUI;
import util.StreamConstants;
import util.StreamExternals;
import util.StreamUtil;
import exception.StreamIOException;
import exception.StreamParserException;
import fileio.StreamIO;

/**
 * <b>Stream</b> is the main product of the project. It is the amalgamation of
 * all different components from different packages, namely <b>StreamUI</b>,
 * <b>StreamObject</b>, <b>StreamIO</b>, and <b>StreamParser</b>.
 * 
 * @version V0.5
 */
public class Stream extends Loggable {

	StreamUI stui;
	StreamObject streamObject;
	StreamLogic streamLogic;

	private String filename;

	private static final String THANK_YOU = "Thank you for using STREAM!";

	public static final String VERSION = "V0.6";
	private static final String FILENAME = "stream";
	static final String SAVEFILE_EXTENSION = ".json";
	private static final String SAVEFILE_FORMAT = "%1$s" + SAVEFILE_EXTENSION;
	private static final String LOGFILE_FORMAT = "%1$s.txt";
	private static final SimpleDateFormat LOGFILE_DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	// @author A0118007R
	/**
	 * Stream Constructor to initialize the program.
	 */
	public Stream(String file) {
		initializeExtFiles();
		initStreamIO(file);
		initializeStream();
		load();
	}

	private void initializeStream() {
		stui = StreamUI.init(this);
		streamObject = StreamObject.init();
		streamLogic = StreamLogic.init(this, stui, streamObject);
	}

	// @author A0093874N
	private void initializeExtFiles() {
		ImageIcon headerText = new ImageIcon(getClass().getResource(
				"/img/header.png"));
		ImageIcon doneIcon = new ImageIcon(getClass().getResource(
				"/img/taskDoneIcon.png"));
		ImageIcon notDoneIcon = new ImageIcon(getClass().getResource(
				"/img/taskOngoingIcon.png"));
		ImageIcon overdueIcon = new ImageIcon(getClass().getResource(
				"/img/taskOverdueIcon.png"));
		ImageIcon inactiveIcon = new ImageIcon(getClass().getResource(
				"/img/taskInactiveIcon.png"));
		ImageIcon hiRankIcon = new ImageIcon(getClass().getResource(
				"/img/taskHighPriority.png"));
		ImageIcon medRankIcon = new ImageIcon(getClass().getResource(
				"/img/taskNormalPriority.png"));
		ImageIcon lowRankIcon = new ImageIcon(getClass().getResource(
				"/img/taskLowPriority.png"));
		ImageIcon startCalIcon = new ImageIcon(getClass().getResource(
				"/img/startdate.png"));
		ImageIcon nullStartCalIcon = new ImageIcon(getClass().getResource(
				"/img/nostartdate.png"));
		ImageIcon endCalIcon = new ImageIcon(getClass().getResource(
				"/img/enddate.png"));
		ImageIcon nullEndCalIcon = new ImageIcon(getClass().getResource(
				"/img/noenddate.png"));
		Font titleFont = null;
		Font consoleFont = null;
		try {
			titleFont = Font.createFont(Font.TRUETYPE_FONT, getClass()
					.getResourceAsStream("/fonts/Awesome Java.ttf"));
			consoleFont = Font.createFont(Font.TRUETYPE_FONT, getClass()
					.getResourceAsStream("/fonts/Ubuntu.ttf"));
		} catch (Exception shouldnthappen) {

		}
		StreamExternals.init(headerText, doneIcon, notDoneIcon, overdueIcon,
				inactiveIcon, hiRankIcon, medRankIcon, lowRankIcon,
				startCalIcon, nullStartCalIcon, endCalIcon, nullEndCalIcon,
				titleFont, consoleFont);
	}

	private void saveLogFile() throws StreamIOException {
		Calendar now = Calendar.getInstance();
		String logFileName = String.format(LOGFILE_FORMAT,
				LOGFILE_DATE_FORMAT.format(now.getTime()));
		StreamIO.saveLogFile(StreamLogger.getLogStack(), logFileName);
	}

	// @author A0096529N
	private void initStreamIO(String file) {
		if (!file.endsWith(SAVEFILE_EXTENSION)) {
			filename = String.format(SAVEFILE_FORMAT, file);
		} else {
			filename = file;
		}
		StreamIO.setFilename(filename);
	}

	/**
	 * Loads the StreamObject state from a saved file, into the current
	 * streamObject instance. No new instance of StreamObject is created.
	 */
	void load() {
		try {
			HashMap<String, StreamTask> taskMap = new HashMap<String, StreamTask>();
			ArrayList<String> taskList = new ArrayList<String>();

			StreamIO.load(taskMap, taskList);
			streamObject.setTaskList(taskList);
			streamObject.setTaskMap(taskMap);
			streamLogic.refreshUI();
		} catch (StreamIOException e) {
			logDebug(String.format(StreamConstants.LogMessage.LOAD_FAILED,
					e.getMessage()));
		}
	}

	/**
	 * Saves the current StreamObject state using StreamIO
	 * 
	 * @return result the result of this operation
	 */
	String save() {
		String result = null;
		try {
			HashMap<String, StreamTask> allTasks = streamObject.getTaskMap();
			ArrayList<String> taskList = streamObject.getTaskList();
			StreamIO.save(allTasks, taskList);
			result = "File saved to " + StreamIO.getSaveLocation();
		} catch (StreamIOException e) {
			result = String.format(StreamConstants.LogMessage.LOAD_FAILED,
					e.getMessage());
			logDebug(result);
		}

		return result;
	}
	
	//@author A0093874N

	@Override
	public String getComponentName() {
		return "STREAM";
	}

	public void exit() {
		showAndLogResult(THANK_YOU);
		System.out.println(THANK_YOU);
		save();
		try {
			saveLogFile();
		} catch (StreamIOException e) {
			System.out.println(e.getMessage());
		}
		System.exit(0);
	}
	
	private void showAndLogResult(String logMessage) {
		stui.log(logMessage, false);
		logDebug(StreamUtil.showAsTerminalResponse(logMessage));
	}

	// @author A0118007R
	private void showAndLogError(String errorMessageForDoc,
			String errorMessageForUser) {
		stui.log(errorMessageForUser, true);
		logError(StreamUtil.showAsTerminalResponse(errorMessageForDoc));
	}

	// @author A0093874N
	public void processInput(String input) {
		try {
			String result = streamLogic.execute(input);
			if (result != null) {
				showAndLogResult(result);
			}
			save();
		} catch (AssertionError e) {
			showAndLogError(String.format(StreamConstants.LogMessage.ERRORS,
					"AssertionError", e.getMessage()),
					String.format(StreamConstants.LogMessage.UNEXPECTED_ERROR,
							e.getMessage()));
		} catch (StreamParserException e) {
			showAndLogError(String.format(StreamConstants.LogMessage.ERRORS, e
					.getClass().getSimpleName(), e.getMessage()),
					String.format(StreamConstants.LogMessage.PARSER_ERROR,
							e.getMessage()));
		} catch (Exception e) {
			showAndLogError(String.format(StreamConstants.LogMessage.ERRORS, e
					.getClass().getSimpleName(), e.getMessage()),
					String.format(StreamConstants.LogMessage.UNEXPECTED_ERROR,
							e.getMessage()));
		}
	}

	/*
	 * Inputs like unsort, recover, and dismiss cannot be triggered by user;
	 * only can be triggered by the machine as part of undo.
	 */
	private Boolean isRestrictedInput(String input) {
		try {
			String keyword = input.split(" ")[0];
			switch (keyword) {
				case "unsort":
				case "dismiss":
				case "recover":
					return true;
				default:
					return false;
			}
		} catch (IndexOutOfBoundsException e) {
			// shouldn't happen but let's play safe
			return false;
		}
	}

	public void filterAndProcessInput(String input) {
		assert (input != null) : String.format(
				StreamConstants.LogMessage.ERRORS, "AssertionError",
				StreamConstants.Assertion.NULL_INPUT);
		logDebug(StreamUtil.showAsTerminalInput(input));
		if (isRestrictedInput(input)) {
			// TODO update this
			showAndLogError(StreamConstants.LogMessage.CMD_UNKNOWN,
					StreamConstants.LogMessage.CMD_UNKNOWN);
		} else {
			processInput(input);
		}
	}

	public static void main(String[] args) {
		new Stream(FILENAME);
	}

}