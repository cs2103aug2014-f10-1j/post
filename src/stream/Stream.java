package stream;

import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;

import logger.Loggable;
import logger.StreamLogger;
import logic.StreamLogic;
import model.StreamObject;
import ui.StreamUI;
import util.StreamUtil;
import exception.StreamIOException;
import exception.StreamRetrievalException;
import exception.StreamParserException;
import exception.StreamRestriction;
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
	private static Boolean isExtFilesInitialized = false;

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

	private static final String ERROR_PARSER = "Could not understand your command, "
			+ "please refer to the manual for list of commands.\nDetails: %1$s";
	private static final String ERROR_UNEXPECTED = "Oops! An unexpected error occured, "
			+ "please retry.\nDetails: %1$s";
	private static final String ERROR_RESTRICT = "Disallowed input: %1$s.";
	private static final String ERROR_RETRIEVE = "Task could not be retrieved, please retry. Details: %1$s.";
	private static final String ERROR_LOAD = "Load from file failed: %1$s. Creating new file.";
	private static final String ERROR_SAVE = "Save to file failed: %1$s.";
	private static final String ERROR_LOG = "%1$s: %2$s";
	private static final String MSG_LOAD = "File loaded: %1$s.";
	private static final String MSG_SAVE = "File saved to %1$s.";
	private static final String MSG_THANK_YOU = "Thank you for using STREAM!";

	@Override
	public String getComponentName() {
		return "STREAM";
	}

	//@author A0118007R
	/**
	 * Stream Constructor to initialize the program.
	 */
	public Stream(String file) {
		if (!isExtFilesInitialized) {
			initializeExtFiles();
		}
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
		isExtFilesInitialized = true;
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
			stio.load(stobj);
			stlog.refreshUI(null);
			showAndLogResult(String.format(MSG_LOAD, stio.getSaveLocation()));
		} catch (StreamIOException e) {
			showAndLogError(e, ERROR_LOAD);
		}
	}

	//@author A0093874N
	/*
	 * Inputs like unsort, recover, and dismiss cannot be triggered by user;
	 * only can be triggered by the machine as part of undo.
	 */
	private void filterForRestriction(String input) throws StreamRestriction {
		try {
			String keyword = input.split(" ")[0];
			switch (keyword) {
				case "unsort":
				case "dismiss":
				case "recover":
					throw new StreamRestriction(input);
				default:
			}
		} catch (IndexOutOfBoundsException e) {
			// let it pass...
		}
	}

	public void filterAndProcessInput(String input) {
		try {
			filterForRestriction(input);
			logDebug(StreamUtil.showAsTerminalInput(input));
			String result = stlog.parseAndExecute(input);
			if (result != null) {
				showAndLogResult(result);
			}
			save();
		} catch (StreamRestriction e) {
			showAndLogError(e, ERROR_RESTRICT);
		} catch (AssertionError e) {
			showAndLogError(e, ERROR_UNEXPECTED);
		} catch (StreamParserException e) {
			showAndLogError(e, ERROR_PARSER);
		} catch (StreamRetrievalException e) {
			showAndLogError(e, ERROR_RETRIEVE);
		} catch (Exception e) {
			showAndLogError(e, ERROR_UNEXPECTED);
		}
	}

	//@author A0096529N
	/**
	 * Saves the current StreamObject state using StreamIO
	 * 
	 * @return result the result of this operation
	 */
	void save() {
		try {
			stio.save(stobj);
			logDebug(String.format(MSG_SAVE, stio.getSaveLocation()));
		} catch (StreamIOException e) {
			logError(String.format(ERROR_SAVE, e.getMessage()));
		}
	}

	//@author A0118007R
	private void showAndLogResult(String logMessage) {
		stui.log(logMessage, false);
		logDebug(StreamUtil.showAsTerminalResponse(logMessage));
	}

	private void showAndLogError(Throwable e, String errorMessageTemplate) {
		stui.log(String.format(errorMessageTemplate, e.getMessage()), true);
		logError(StreamUtil.showAsTerminalResponse(String.format(ERROR_LOG, e
				.getClass().getSimpleName(), e.getMessage())));
	}

	//@author A0093874N
	public void exit() {
		showAndLogResult(MSG_THANK_YOU);
		System.out.println(MSG_THANK_YOU);
		save();
		try {
			saveLogFile();
		} catch (StreamIOException e) {
			// TODO what to do here?
		}
		System.exit(0);
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
