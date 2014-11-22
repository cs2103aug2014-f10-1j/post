package stream;

import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.ImageIcon;

import logger.Loggable;
import logger.StreamLogger;
import logic.StreamLogic;
import model.StreamObject;
import model.StreamTask;
import ui.StreamUI;
import util.StreamConstants;
import util.StreamUtil;
import exception.StreamIOException;
import exception.StreamParserException;
import fileio.StreamIO;

/**
 * <b>Stream</b> is the main product of the project. It is the amalgamation of
 * all different main components from different packages, namely
 * <b>StreamUI</b>, <b>StreamObject</b>, <b>StreamLogic</b>, <b>StreamIO</b>,
 * and <b>StreamParser</b>. This is also the main class from which the
 * application is run from.
 */
public class Stream extends Loggable {

	StreamUI stui;
	StreamIO stio;
	StreamObject stobj;
	StreamLogic stlog;

	private String filename;

	private static final String THANK_YOU = "Thank you for using STREAM!";

	public static final String VERSION = "V0.6";
	private static final String FILENAME = "stream";
	static final String SAVEFILE_EXTENSION = ".json";
	private static final String SAVEFILE_FORMAT = "%1$s" + SAVEFILE_EXTENSION;
	private static final String LOGFILE_FORMAT = "%1$s.txt";
	private static final SimpleDateFormat LOGFILE_DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	public static ImageIcon HEADER;
	public static ImageIcon ICON_DONE;
	public static ImageIcon ICON_NOT_DONE;
	public static ImageIcon ICON_OVERDUE;
	public static ImageIcon ICON_INACTIVE;
	public static ImageIcon ICON_HI_RANK;
	public static ImageIcon ICON_MED_RANK;
	public static ImageIcon ICON_LOW_RANK;
	public static ImageIcon ICON_START_CAL;
	public static ImageIcon ICON_NULL_START_CAL;
	public static ImageIcon ICON_END_CAL;
	public static ImageIcon ICON_NULL_END_CAL;
	public static Font FONT_TITLE;
	public static Font FONT_CONSOLE;

	@Override
	public String getComponentName() {
		return "STREAM";
	}

	//@author A0118007R
	/**
	 * Stream Constructor to initialize the program.
	 */
	public Stream(String file) {
		initializeExtFiles();
		initializeStreamFilename(file);
		initializeStreamParams();
		load();
	}

	//@author A0093874N
	private void initializeExtFiles() {
		HEADER = new ImageIcon(getClass().getResource("/img/header.png"));
		ICON_DONE = new ImageIcon(getClass().getResource(
				"/img/taskDoneIcon.png"));
		ICON_NOT_DONE = new ImageIcon(getClass().getResource(
				"/img/taskOngoingIcon.png"));
		ICON_OVERDUE = new ImageIcon(getClass().getResource(
				"/img/taskOverdueIcon.png"));
		ICON_INACTIVE = new ImageIcon(getClass().getResource(
				"/img/taskInactiveIcon.png"));
		ICON_HI_RANK = new ImageIcon(getClass().getResource(
				"/img/taskHighPriority.png"));
		ICON_MED_RANK = new ImageIcon(getClass().getResource(
				"/img/taskNormalPriority.png"));
		ICON_LOW_RANK = new ImageIcon(getClass().getResource(
				"/img/taskLowPriority.png"));
		ICON_START_CAL = new ImageIcon(getClass().getResource(
				"/img/startdate.png"));
		ICON_NULL_START_CAL = new ImageIcon(getClass().getResource(
				"/img/nostartdate.png"));
		ICON_END_CAL = new ImageIcon(getClass().getResource("/img/enddate.png"));
		ICON_NULL_END_CAL = new ImageIcon(getClass().getResource(
				"/img/noenddate.png"));
		try {
			FONT_TITLE = Font.createFont(Font.TRUETYPE_FONT, getClass()
					.getResourceAsStream("/fonts/Awesome Java.ttf"));
			FONT_CONSOLE = Font.createFont(Font.TRUETYPE_FONT, getClass()
					.getResourceAsStream("/fonts/Ubuntu.ttf"));
		} catch (Exception shouldnthappen) {

		}
	}

	//@author A0096529N
	private void initializeStreamFilename(String file) {
		if (!file.endsWith(SAVEFILE_EXTENSION)) {
			filename = String.format(SAVEFILE_FORMAT, file);
		} else {
			filename = file;
		}
	}

	//@author A0118007R
	private void initializeStreamParams() {
		stui = StreamUI.init(this);
		stio = StreamIO.init(filename);
		stobj = StreamObject.init();
		stlog = StreamLogic.init(this, stui, stobj);
	}

	//@author A0096529N
	/**
	 * Loads the StreamObject state from a saved file, into the current
	 * streamObject instance. No new instance of StreamObject is created.
	 */
	void load() {
		try {
			HashMap<String, StreamTask> taskMap = new HashMap<String, StreamTask>();
			ArrayList<String> taskList = new ArrayList<String>();
			stio.load(taskMap, taskList);
			stobj.setTaskList(taskList);
			stobj.setTaskMap(taskMap);
			stlog.refreshUI();
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
			HashMap<String, StreamTask> allTasks = stobj.getTaskMap();
			ArrayList<String> taskList = stobj.getTaskList();
			stio.save(allTasks, taskList);
			result = "File saved to " + stio.getSaveLocation();
		} catch (StreamIOException e) {
			result = String.format(StreamConstants.LogMessage.LOAD_FAILED,
					e.getMessage());
			logDebug(result);
		}

		return result;
	}

	//@author A0093874N

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

	//@author A0118007R
	private void showAndLogError(String errorMessageForDoc,
			String errorMessageForUser) {
		stui.log(errorMessageForUser, true);
		logError(StreamUtil.showAsTerminalResponse(errorMessageForDoc));
	}

	//@author A0093874N
	public void processInput(String input) {
		try {
			String result = stlog.execute(input);
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
	
	private void saveLogFile() throws StreamIOException {
		Date now = Calendar.getInstance().getTime();
		String logFileName = String.format(LOGFILE_FORMAT,
				LOGFILE_DATE_FORMAT.format(now));
		stio.saveLogFile(StreamLogger.getLogStack(), logFileName);
	}

	public static void main(String[] args) {
		new Stream(FILENAME);
	}

}