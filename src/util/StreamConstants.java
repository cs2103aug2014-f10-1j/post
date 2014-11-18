package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;

/**
 * A class to contain all constants. Things such as messages (error/log/...),
 * parameters (UI sizes, ...), Stream-related fields (components, ...) can be
 * found here. Contains other nested classes for better organization.
 * 
 * @version V0.5
 */
public class StreamConstants {

	public static final String[] MODIFICATION_ATTRIBUTES = { "-name", "-desc",
			"-start", "-from", "-due", "-by", "-tag", "-untag", "-settags",
			"-rank", "-mark", "-to" };

	public static class ComponentTag {
		public static final String STREAM = "stream";
		public static final String STREAMUI = "ui";
		public static final String STREAMLOGIC = "streamLogic";
		public static final String STREAMTASK = "taskLogic";
		public static final String STREAMSTACK = "stackLogic";
		public static final String STREAMIO = "io";
		public static final String STREAMPARSER = "parser";
	}

	public static class LogMessage {
		public static final String CMD_RECEIVED = "Command received [%1$s]";
		public static final String ADD = "Added \"%1$s\"";
		public static final String DELETE = "Deleted \"%1$s\"";
		public static final String VIEW = "Viewed the details for \"%1$s\"";
		public static final String DESC = "Changed description for \"%1$s\" to \"%2$s\"";
		public static final String RANK = "Changed ranking for \"%1$s\" to \"%2$s\"";
		public static final String CLEAR = "Cleared all tasks";
		public static final String RECOVER = "Recovered %1$s task(s)";
		public static final String UNSORT = "Restored to previous task sorting";
		public static final String NAME = "Changed name for \"%1$s\" to \"%2$s\"";
		public static final String MARK = "\"%1$s\" marked as %2$s";
		public static final String ERRORS = "%1$s: %2$s";
		public static final String UNDO_FAIL = "No previous action to undo";
		public static final String UNDO_SUCCESS = "Reverted the previous action";
		public static final String DUE_NEVER = "Due date for \"%1$s\" is removed";
		public static final String DUE = "Due date for \"%1$s\" set to \"%2$s\"";
		public static final String START = "Start date for \"%1$s\" set to \"%2$s\"";
		public static final String START_NOT_SPECIFIED = "Start Time for \"%1$s\" is not specified";
		public static final String CMD_UNKNOWN = "Unknown command entered";
		public static final String TAGS_TO_ADD = "Tags to add \"%1$s\": %2$s";
		public static final String TAGS_TO_REMOVE = "Tags to remove \"%1$s\": %2$s";
		public static final String TAGS_NOT_ADDED = "Tags not added to \"%1$s\": %2$s";
		public static final String TAGS_NOT_REMOVED = "Tags not removed \"%1$s\": %2$s";
		public static final String TAGS_ADDED = "Tags added to \"%1$s\": %2$s";
		public static final String NO_TAGS_ADDED = "No new tags added";
		public static final String TAGS_REMOVED = "Tags removed from \"%1$s\": %2$s";
		public static final String NO_TAGS_REMOVED = "No tags removed";
		public static final String SEARCH = "Searching for \"%1$s\", %2$s queries found";
		public static final String FILTER = "Filtering for tasks %1$s, %2$s queries found";
		public static final String LOAD_FAILED = "Load from file failed, %1$s";
		public static final String SAVE_FAILED = "Save to file failed, %1$s";
		public static final String MODIFY = "Modified %1$s";
		public static final String NEW_MODIFICATION = "Modify \"%1$s\" attribute \"%2$s\" contents \"%3$s\"";
		public static final String UI_LOOKANDFEEL_FAIL = "Could not set look and feel";
		public static final String PUSH_INVERSE_COMMAND = "Pushed inverse command \"%1$s\"";
		public static final String PUSH_ORDER = "Pushed inverse order \"%1$s\"";
		public static final String PUSH_INVERSE_TASK = "Pushed inverse task \"%1$s\"";
		public static final String POP_INVERSE_COMMAND = "Pop inverse command \"%1$s\"";
		public static final String POP_ORDER = "Pop inverse order \"%1$s\"";
		public static final String POP_INVERSE_TASK = "Pop inverse task \"%1$s\"";
		public static final String SET_DEADLINE = "Set deadline for \"%1$s\" on \"%2$s\"";
		public static final String SET_STARTTIME = "Set start time for \"%1$s\" on \"%2$s\"";
		public static final String REORDER_TASKS = "Reordered tasks %1$s";
		public static final String ADDED_TASK = "Added task \"%1$s\"";
		public static final String ADD_DUPLICATE_TASK = "Task name already present \"%1$s\"";
		public static final String RECOVERED_TASK = "Recovered task \"%1$s\"";
		public static final String CLEARED_TASKS = "Removed all tasks";
		public static final String UPDATE_TASK_NAME_DUPLICATE = "Task name not available \"%1$s\"";
		public static final String UPDATE_TASK_NAME = "Updated task name \"%1$s\" to \"%2$s\"";
		public static final String FILTERED_TASKS = "Filter tasks with criteria \"%1$s\", result: %2$s";
		public static final String SEARCHED_TASKS = "Searched tasks with keyphrase \"%1$s\", result: %2$s";
		public static final String PARSER_ERROR = "Could not understand your command, "
				+ "please refer to the manual for list of commands.\nDetails: %1$s";
		public static final String EMPTY_INPUT_ERROR = "Please enter a command!";
		public static final String UNEXPECTED_ERROR = "Oops! An unexpected error occured, please retry.\nDetails: %1$s";
		public static final String LOADED_USER_HOME = "Loaded user home dir: %1$s";
		public static final String LOAD_FAIL_USER_HOME = "Could not load user home dir.";
	}

	public static class Assertion {
		public static final String NULL_INPUT = "Null input value detected";
		public static final String NOT_CLEARED = "Task clearance failed";
		public static final String NOT_ADDED = "Task addition failed";
		public static final String NOT_DELETED = "Task deletion failed";
		public static final String NOT_EQUAL = "The two lists are not equal";
		public static final String NO_PREV_PAGE = "Cannot navigate backward from first page";
		public static final String NO_NEXT_PAGE = "Cannot navigate forward from last page";
		public static final String SIZE_DIFFERENT = "The size of the indices and tasks arrays are different";
		public static final String TOO_MANY_PAGES = "The requested page exceeds the total number of pages";
		public static final String TASK_DUPLICATE_TAG = "Duplicate tag";
		public static final String TASK_TAG_NOTFOUND = "Delete tag not found";
		public static final String TASK_TAG_NOTUPPERCASE = "Tag not in uppercase";
		public static final String EMPTY_INVERSE_COMMAND = "Empty inverse command";
		public static final String EMPTY_INVERSE_ORDER = "Empty inverse order";
		public static final String NULL_INVERSE_TASK = "Null inverse task";
	}

	public static class ExceptionMessage {
		public static final String ERR_TAG_DOES_NOT_EXIST = "Error: The tag \"%1$s\" does not exist.";
		public static final String ERR_TASK_ALREADY_EXISTS = "\"%1$s\" already exists in the tasks list.";
		public static final String ERR_TASK_DOES_NOT_EXIST = "The task \"%1$s\" does not exist.";
		public static final String ERR_NEW_TASK_NAME_NOT_AVAILABLE = "The name \"%1$s\" is not available.";
		public static final String ERR_CREATEFILE = "Test file could not be created "
				+ "- %1$s\nDelete the file if already present.";
		public static final String ERR_TASK_MODIFICATION = "Could not modify file: %1$s";
		public static final String ERR_CREATE_STREAM_DIR = "Could not create Stream directory";
		public static final String ERR_SAVE_LOG = "Could not save log file";
		public static final String ERR_CREATE_LOG_DIR = "Could not create Logs directory";
		public static final String ERR_DEADLINE_BEFORE_STARTTIME = "Error: deadline is not changed. Ensure that you have entered a valid deadline";
		public static final String ERR_STARTTIME_AFTER_DEADLINE = "Error: start time is not changed. Ensure that you have entered a valid start time";
		public static final String ERR_UI_FADE_THREAD = "Error on UI fade, %1$s: %2$s";

	}

	public static class UI {
		public static final Color COLOR_LOG_MSG = Color.BLACK;
		public static final Color COLOR_ERR_MSG = Color.RED;
		public static final Color COLOR_HELP_MSG = Color.GRAY;
		public static final Color COLOR_LOGGER = new Color(228, 228, 228);
		public static final Color COLOR_FEEDBACK = Color.WHITE;
		public static final Color COLOR_MONTH = Color.WHITE;
		public static final Color COLOR_TASKPANEL = new Color(228, 228, 228);

		public static Font FONT_TITLE = StreamExternals.FONT_TITLE.deriveFont(
				Font.PLAIN, 100);
		public static Font FONT_INDEX = StreamExternals.FONT_TITLE.deriveFont(
				Font.PLAIN, 70);
		public static Font FONT_CONSOLE = StreamExternals.FONT_CONSOLE
				.deriveFont(Font.PLAIN, 13);
		public static Font FONT_LOGGER = StreamExternals.FONT_CONSOLE
				.deriveFont(Font.PLAIN, 13);
		public static Font FONT_FOOTER = StreamExternals.FONT_CONSOLE
				.deriveFont(Font.PLAIN, 13);
		public static Font FONT_TASK = StreamExternals.FONT_CONSOLE.deriveFont(
				Font.BOLD, 13);
		public static Font FONT_DESC = StreamExternals.FONT_CONSOLE.deriveFont(
				Font.PLAIN, 13);
		public static Font FONT_MONTH = StreamExternals.FONT_CONSOLE
				.deriveFont(Font.PLAIN, 13);
		public static Font FONT_DATE = StreamExternals.FONT_CONSOLE.deriveFont(
				Font.PLAIN, 20);
		public static Font FONT_TIME = StreamExternals.FONT_CONSOLE.deriveFont(
				Font.PLAIN, 13);
		public static Font FONT_PAGE_NUM = StreamExternals.FONT_CONSOLE
				.deriveFont(Font.PLAIN, 13);

		public static final int HEIGHT_MAINFRAME = 700;
		public static final int WIDTH_MAINFRAME = 750;

		public static final int MAX_VIEWABLE_TASK = 7;

		public static final int HEIGHT_TASKPANEL = 50;
		public static final int HEIGHT_CONSOLE = 30;
		public static final int HEIGHT_HEADER = 50;
		public static final int HEIGHT_LOGGER = 45;
		public static final int COMPONENT_WIDTH = 700;
		public static final int WIDTH_INDEX = 80;
		public static final int MARGIN_COMPONENT = 10;
		public static final int MARGIN_SIDE = 25;

		public static final Insets MARGIN_LOGGER = new Insets(5, 5, 5, 5);
		public static final Insets MARGIN_CONSOLE = new Insets(0, 5, 0, 0);

		public static final Rectangle BOUNDS_CAL_ICON = new Rectangle(0, 0,
				HEIGHT_TASKPANEL, HEIGHT_TASKPANEL);
		public static final Rectangle BOUNDS_MONTH = new Rectangle(0, 4,
				HEIGHT_TASKPANEL, 10);
		public static final Rectangle BOUNDS_DATE = new Rectangle(0, 11,
				HEIGHT_TASKPANEL, 30);
		public static final Rectangle BOUNDS_TIME = new Rectangle(0, 37,
				HEIGHT_TASKPANEL, 10);
		public static final Rectangle BOUNDS_INDEX_NUM = new Rectangle(0, 0,
				WIDTH_INDEX, HEIGHT_TASKPANEL);
		public static final Rectangle BOUNDS_START_CAL = new Rectangle(
				WIDTH_INDEX, 0, HEIGHT_TASKPANEL, HEIGHT_TASKPANEL);
		public static final Rectangle BOUNDS_END_CAL = new Rectangle(
				WIDTH_INDEX + HEIGHT_TASKPANEL, 0, HEIGHT_TASKPANEL,
				HEIGHT_TASKPANEL);
		public static final Rectangle BOUNDS_TASK_NAME = new Rectangle(
				WIDTH_INDEX + 2 * HEIGHT_TASKPANEL + 10, 0, COMPONENT_WIDTH
						- WIDTH_INDEX - 4 * HEIGHT_TASKPANEL - 20,
				HEIGHT_TASKPANEL / 2);
		public static final Rectangle BOUNDS_TASK_DESC = new Rectangle(
				WIDTH_INDEX + 2 * HEIGHT_TASKPANEL + 10, HEIGHT_TASKPANEL / 2,
				COMPONENT_WIDTH - WIDTH_INDEX - 4 * HEIGHT_TASKPANEL - 20,
				HEIGHT_TASKPANEL / 2);
		public static final Rectangle BOUNDS_RANK_ICON = new Rectangle(
				COMPONENT_WIDTH - 2 * HEIGHT_TASKPANEL, 0, HEIGHT_TASKPANEL,
				HEIGHT_TASKPANEL);
		public static final Rectangle BOUNDS_STATS_ICON = new Rectangle(
				COMPONENT_WIDTH - HEIGHT_TASKPANEL, 0, HEIGHT_TASKPANEL,
				HEIGHT_TASKPANEL);
		public static final Rectangle BOUNDS_HEADER = new Rectangle(
				MARGIN_SIDE, MARGIN_COMPONENT, COMPONENT_WIDTH, HEIGHT_HEADER);
		public static final Rectangle BOUNDS_CONSOLE = new Rectangle(
				MARGIN_SIDE, 530, COMPONENT_WIDTH, HEIGHT_CONSOLE);
		public static final Rectangle BOUNDS_FEEDBACK = new Rectangle(
				MARGIN_SIDE, 490, COMPONENT_WIDTH, HEIGHT_CONSOLE);
		public static final Rectangle BOUNDS_FOOTER = new Rectangle(
				MARGIN_SIDE, 625, COMPONENT_WIDTH, HEIGHT_CONSOLE);
		public static final Rectangle BOUNDS_PAGE_NUM = new Rectangle(
				MARGIN_SIDE, 625, COMPONENT_WIDTH, HEIGHT_CONSOLE);
		public static final Rectangle BOUNDS_LOGGER = new Rectangle(
				MARGIN_SIDE, 570, COMPONENT_WIDTH, HEIGHT_LOGGER);

	}
}
