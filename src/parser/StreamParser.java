package parser;

import util.StreamConstants;
import util.StreamLogger;
import util.StreamLogger.LogLevel;
import util.StreamUtil;
import exception.StreamParserException;

//@author A0119401U
/**
 * Parser is used to interpret the user input to a pack of information and later
 * on pass it to the Logic part
 * 
 * @version V0.5
 */
public class StreamParser {

	public enum CommandType {
		INIT, ADD, DEL, DESC, DUE, START, VIEW, RANK, MODIFY, NAME, MARK,
		TAG, UNTAG, SEARCH, SORT, UNSORT, FILTER, CLRSRC, CLEAR, UNDO, EXIT,
		ERROR, RECOVER, DISMISS, FIRST, PREV, NEXT, LAST, PAGE, HELP;
	}

	public enum MarkType {
		DONE, NOT, OVERDUE, INACTIVE, NULL;
	}

	public enum RankType {
		HI, MED, LO, NULL;
	}

	public enum SortType {
		ALPHA, START, END, TIME, IMPORTANCE, NULL;
	}

	public enum FilterType {
		DONE, NOT, HIRANK, MEDRANK, LORANK, DUEON, DUEBEF,
		DUEAFT, STARTON, STARTBEF, STARTAFT, NOTIMING,
		DEADLINED, EVENT, NULL, OVERDUE, INACTIVE;
	}

	private CommandType commandKey;
	private Integer commandIndex;
	private String commandContent;

	private static final StreamLogger logger = StreamLogger
			.init(StreamConstants.ComponentTag.STREAMPARSER);

	static final String ERROR_INCOMPLETE_INPUT = "Please provide more information!";
	static final String ERROR_INCOMPLETE_INDEX = "Please provide the index or page number!";
	static final String ERROR_INVALID_INDEX = "Please provide a valid index number!";
	static final String ERROR_INVALID_FILTER = "Please enter a valid filter type!";
	static final String ERROR_INVALID_SORT = "Please enter a valid sorting type!";
	static final String ERROR_INVALID_MARK = "Please enter a valid marking type!";
	static final String ERROR_INVALID_RANK = "Please enter a valid input rank!";
	public static final String ERROR_EMPTY_INPUT = "Empty input detected!";
	static final String ERROR_INDEX_OUT_OF_BOUNDS = "The index you entered is out of range!";
	static final String ERROR_DATE_NOT_PARSEABLE = "Date cannot be understood!";
	static final String ERROR_UNKNOWN_COMMAND = "Unknown command type!";

	private static final int PARAM_POS_KEYWORD = 0;
	private static final int PARAM_POS_CONTENTS = 1;
	private static final int PARAM_POS_INDEX = 1;
	private static final int PARAM_POS_ARGS = 2;
	private static final int PARAM_POS_FILTERTYPE = 1;
	private static final int PARAM_POS_SORTTYPE = 1;
	private static final int PARAM_POS_SORTORDER = 2;

	private static final int ARGS_LENGTH_TYPE_ONE = 2;
	private static final int ARGS_LENGTH_TYPE_TWO = 2;
	private static final int ARGS_LENGTH_TYPE_THREE = 3;
	private static final int ARGS_LENGTH_TYPE_FOUR = 3;

	public StreamParser() {
		this.commandKey = CommandType.INIT;
		this.commandIndex = null;
		this.commandContent = null;
	}

	public void interpretCommand(String input, int numOfTasks)
			throws StreamParserException {
		if (input.isEmpty()) {
			throw new StreamParserException(ERROR_EMPTY_INPUT);
		}
		String[] contents = input.trim().split(" ", 2);
		String[] contentsSplitWithIndex = input.trim().split(" ", 3);
		String key = contents[PARAM_POS_KEYWORD].toLowerCase();
		switch (key) {
			case "add":
				checkTypeOneValidity(contents);
				this.commandKey = CommandType.ADD;
				break;

			case "del":
			case "delete":
				checkTypeTwoValidity(contents, numOfTasks);
				this.commandKey = CommandType.DEL;
				break;

			case "desc":
			case "describe":
				checkTypeThreeValidity(contentsSplitWithIndex, numOfTasks);
				this.commandKey = CommandType.DESC;
				break;

			case "due":
			case "end":
				checkTypeFourValidity(contentsSplitWithIndex, numOfTasks);
				this.commandKey = CommandType.DUE;
				break;

			case "start":
				checkTypeFourValidity(contentsSplitWithIndex, numOfTasks);
				this.commandKey = CommandType.START;
				break;

			case "view":
				checkTypeTwoValidity(contents, numOfTasks);
				this.commandKey = CommandType.VIEW;
				break;

			case "rank":
				checkRankValidity(contentsSplitWithIndex, numOfTasks);
				this.commandKey = CommandType.RANK;
				break;

			case "mod":
			case "modify":
				checkTypeThreeValidity(contentsSplitWithIndex, numOfTasks);
				this.commandKey = CommandType.MODIFY;
				break;

			case "name":
				checkTypeThreeValidity(contentsSplitWithIndex, numOfTasks);
				this.commandKey = CommandType.NAME;
				break;

			case "mark":
				checkMarkValidity(contentsSplitWithIndex, numOfTasks);
				this.commandKey = CommandType.MARK;
				break;

			case "tag":
				checkTypeThreeValidity(contentsSplitWithIndex, numOfTasks);
				this.commandKey = CommandType.TAG;
				break;

			case "untag":
				checkTypeThreeValidity(contentsSplitWithIndex, numOfTasks);
				this.commandKey = CommandType.UNTAG;
				break;

			case "search":
			case "find":
				checkTypeOneValidity(contents);
				this.commandKey = CommandType.SEARCH;
				break;

			case "sort":
				checkSortValidity(contents, contentsSplitWithIndex);
				this.commandKey = CommandType.SORT;
				break;

			case "unsort":
				this.commandKey = CommandType.UNSORT;
				break;

			case "filter":
				checkFilterValidity(contents);
				this.commandKey = CommandType.FILTER;
				break;

			case "clrsrc":
				this.commandKey = CommandType.CLRSRC;
				break;

			case "clear":
			case "clr":
				this.commandKey = CommandType.CLEAR;
				break;

			case "undo":
				this.commandKey = CommandType.UNDO;
				break;

			case "recover":
				this.commandKey = CommandType.RECOVER;
				this.commandIndex = Integer.parseInt(contents[PARAM_POS_INDEX]);
				break;

			case "dismiss":
				this.commandKey = CommandType.DISMISS;
				this.commandIndex = Integer.parseInt(contents[PARAM_POS_INDEX]);
				break;

			case "exit":
				this.commandKey = CommandType.EXIT;
				break;

			case "first":
				this.commandKey = CommandType.FIRST;
				break;

			case "prev":
			case "previous":
				this.commandKey = CommandType.PREV;
				break;

			case "next":
				this.commandKey = CommandType.NEXT;
				break;

			case "last":
				this.commandKey = CommandType.LAST;
				break;

			case "page":
			case "goto":
				checkTypeTwoValidity(contents, 1 + numOfTasks
						/ StreamConstants.UI.MAX_VIEWABLE_TASK);
				this.commandKey = CommandType.PAGE;
				break;

			case "help":
				this.commandKey = CommandType.HELP;
				break;

			default:
				logger.log(LogLevel.DEBUG, "Input cannot be interpreted.");
				throw new StreamParserException(ERROR_UNKNOWN_COMMAND);

		}
		logCommand(contents, contentsSplitWithIndex);
	}

	private void checkIndexValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (!StreamUtil.isInteger(contents[PARAM_POS_INDEX])) {
			throw new StreamParserException(ERROR_INVALID_INDEX);
		} else if (!StreamUtil.isWithinRange(
				Integer.parseInt(contents[PARAM_POS_INDEX]), numOfTasks)) {
			throw new StreamParserException(ERROR_INDEX_OUT_OF_BOUNDS);
		}
	}

	/*
	 * Type one command: commands with format (CommandWord) (String arguments of
	 * any length)
	 */
	private void checkTypeOneValidity(String[] contents)
			throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_ONE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		}
		this.commandContent = contents[PARAM_POS_CONTENTS];
	}

	/*
	 * Type two command: commands with format (CommandWord) (index number)
	 */
	private void checkTypeTwoValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_TWO) {
			throw new StreamParserException(ERROR_INCOMPLETE_INDEX);
		} else {
			checkIndexValidity(contents, numOfTasks);
			this.commandIndex = Integer.parseInt(contents[PARAM_POS_INDEX]);
		}
	}

	/*
	 * Type three command: commands with format (CommandWord) (index number)
	 * (String arguments of any length)
	 */
	private void checkTypeThreeValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_THREE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(contents, numOfTasks);
			this.commandIndex = Integer.parseInt(contents[PARAM_POS_INDEX]);
			this.commandContent = contents[PARAM_POS_ARGS];
		}
	}

	/*
	 * Type four command: commands with format (CommandWord) (index number)
	 * (date String to be parsed)
	 */
	private void checkTypeFourValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_FOUR) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(contents, numOfTasks);
			if (!StreamUtil.isParseableDate(contents[PARAM_POS_ARGS])) {
				throw new StreamParserException(ERROR_DATE_NOT_PARSEABLE);
			}
			this.commandIndex = Integer.parseInt(contents[PARAM_POS_INDEX]);
			this.commandContent = contents[PARAM_POS_ARGS];
		}
	}

	private void checkRankValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_THREE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(contents, numOfTasks);
			if (!checkRanking(contents[PARAM_POS_ARGS])) {
				throw new StreamParserException(ERROR_INVALID_RANK);
			}
			this.commandIndex = Integer.parseInt(contents[PARAM_POS_INDEX]);
			this.commandContent = contents[PARAM_POS_ARGS];
		}
	}

	private void checkMarkValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_THREE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(contents, numOfTasks);
			if (!checkMarking(contents[PARAM_POS_ARGS])) {
				throw new StreamParserException(ERROR_INVALID_MARK);
			}
			this.commandIndex = Integer.parseInt(contents[PARAM_POS_INDEX]);
			this.commandContent = contents[PARAM_POS_ARGS];
		}
	}

	private void checkFilterValidity(String[] contents)
			throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_ONE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else if (!checkFilter(contents[PARAM_POS_FILTERTYPE].trim())) {
			throw new StreamParserException(ERROR_INVALID_FILTER);
		}
		this.commandContent = contents[PARAM_POS_CONTENTS];
	}

	private void logCommand(String[] contents, String[] contentsWithIndex) {
		String commandKey = contents[PARAM_POS_KEYWORD].toUpperCase();
		if (contentsWithIndex.length >= 3
				&& StreamUtil.isInteger(contentsWithIndex[PARAM_POS_INDEX])) {
			logger.log(LogLevel.DEBUG, "Command received: " + commandKey
					+ ". Index number " + contentsWithIndex[PARAM_POS_INDEX]
					+ ". Arguments: " + contentsWithIndex[PARAM_POS_ARGS]);
		} else if (contents.length == 2) {
			logger.log(LogLevel.DEBUG, "Command received: " + commandKey
					+ ". Arguments: " + contents[PARAM_POS_CONTENTS]);
		} else {
			logger.log(LogLevel.DEBUG, "Command received: " + commandKey
					+ ". No arguments supplied.");
		}
	}

	/**
	 * @return <b>CommandType</b> - the parsed command type
	 */
	public CommandType getCommandType() {
		return this.commandKey;
	}

	/**
	 * @return <b>Integer</b> - the parsed index number, if applicable
	 */
	public Integer getCommandIndex() {
		return this.commandIndex;
	}

	/**
	 * @return <b>Integer</b> - the parsed arguments/contents, if applicable
	 */
	public String getCommandContent() {
		return this.commandContent;
	}

	//@author A0093874N

	/**
	 * Parses a supplied sorting type into <b>STREAM</b>-recognizable format.
	 * 
	 * @return <b>SortType</b> - the parsed sorting type
	 */
	public static SortType parseSorting(String sortType) {
		sortType = sortType.toLowerCase();
		switch (sortType) {
			case "d":
			case "due":
			case "deadline":
			case "end":
			case "endtime":
				return SortType.END;
			case "s":
			case "start":
			case "begin":
			case "starttime":
				return SortType.START;
			case "a":
			case "alpha":
			case "alphabetical":
			case "alphabetically":
				return SortType.ALPHA;
			case "t":
			case "time":
				return SortType.TIME;
			case "":
			case "impt":
			case "importance":
			case "priority":
				return SortType.IMPORTANCE;
			default:
				return SortType.NULL;
		}
	}

	/**
	 * Translates a supplied sorting order into boolean value, indicating
	 * whether the intended order is descending or not.
	 * 
	 * @return <b>boolean</b> - indicates if descending or not
	 * @throws StreamParserException
	 *             if <i>order</i> is not recognizable
	 */
	public static boolean getSortingOrder(String order)
			throws StreamParserException {
		order = order.toLowerCase();
		switch (order) {
			case "a":
			case "asc":
			case "ascending":
				return false;
			case "":
			case "d":
			case "desc":
			case "descending":
				return true;
			default:
				throw new StreamParserException();
		}
	}

	/**
	 * Parses a supplied rank type into <b>STREAM</b>-recognizable format.
	 * 
	 * @return <b>RankType</b> - the parsed ranking type
	 */
	public static RankType parseRanking(String rankInput) {
		rankInput = rankInput.toLowerCase();
		switch (rankInput) {
			case "high":
			case "hi":
			case "h":
				return RankType.HI;
			case "medium":
			case "med":
			case "m":
				return RankType.MED;
			case "low":
			case "l":
				return RankType.LO;
			default:
				return RankType.NULL;
		}
	}

	/**
	 * Translates an internal ranking format to readable <b>String</b>.
	 * 
	 * @return <b>String</b> - the translated ranking type
	 */
	public static String translateRanking(RankType parsedRank) {
		switch (parsedRank) {
			case HI:
				return "high";
			case MED:
				return "medium";
			case LO:
				return "low";
			default:
				return null;
		}
	}

	/**
	 * Parses a supplied marking type into <b>STREAM</b>-recognizable format.
	 * 
	 * @return <b>MarkType</b> - the parsed marking type
	 */
	public static MarkType parseMarking(String markInput) {
		markInput = markInput.toLowerCase();
		switch (markInput) {
			case "done":
			case "finished":
			case "over":
				return MarkType.DONE;
			case "not done":
			case "not finished":
			case "ongoing":
				return MarkType.NOT;
			case "overdue":
				return MarkType.OVERDUE;
			case "inactive":
				return MarkType.INACTIVE;
			default:
				return MarkType.NULL;
		}
	}

	/**
	 * Parses a boolean value of is done or not into <b>STREAM</b>-recognizable
	 * format for marking.
	 * 
	 * @return <b>MarkType</b> - the parsed marking type
	 */
	public static MarkType parseMarking(Boolean isDone) {
		if (isDone) {
			return MarkType.DONE;
		} else {
			return MarkType.NOT;
		}
	}

	/**
	 * Translates an internal marking format to readable <b>String</b>.
	 * 
	 * @return <b>String</b> - the translated marking type
	 */
	public static String translateMarking(MarkType parsedMark) {
		switch (parsedMark) {
			case DONE:
				return "done";
			case NOT:
				return "ongoing";
			case OVERDUE:
				return "overdue";
			case INACTIVE:
				return "inactive";
			default:
				return null;
		}
	}

	/**
	 * Parses a supplied filter type into <b>STREAM</b>-recognizable format.
	 * 
	 * @return <b>FilterType</b> - the parsed filter type
	 */
	public static FilterType parseFilterType(String filterInput) {
		String[] contents = filterInput.split(" ", 2);
		MarkType parsedMark = parseMarking(contents[0]);
		switch (parsedMark) {
			case DONE:
				return FilterType.DONE;
			case NOT:
				return FilterType.NOT;
			case OVERDUE:
				return FilterType.OVERDUE;
			case INACTIVE:
				return FilterType.INACTIVE;
			default:
		}
		if (contents[0].equals("rank")) {
			if (contents.length == 2) {
				RankType parsedRank = parseRanking(contents[1]);
				switch (parsedRank) {
					case HI:
						return FilterType.HIRANK;
					case MED:
						return FilterType.MEDRANK;
					case LO:
						return FilterType.LORANK;
					default:
						return FilterType.NULL;
				}
			} else {
				return FilterType.NULL;
			}
		} else {
			contents = filterInput.split(" ", 3);
			if (contents.length == 3 && StreamUtil.isParseableDate(contents[2])) {
				switch (contents[0] + " " + contents[1]) {
					case "due before":
						return FilterType.DUEBEF;
					case "due after":
						return FilterType.DUEAFT;
					case "start before":
						return FilterType.STARTBEF;
					case "start after":
						return FilterType.STARTAFT;
					default:
						return FilterType.NULL;
				}
			} else {
				contents = filterInput.split(" ", 2);
				/*
				 * if (contents.length == 2 &&
				 * StreamUtil.isParseableDate(contents[1])) { switch
				 * (contents[0]) { case "due": return FilterType.DUEON; case
				 * "start": return FilterType.STARTON; default: return
				 * FilterType.NULL; } } else
				 */
				// Not implemented until we know how to.
				if (contents.length == 2) {
					switch (contents[0] + " " + contents[1]) {
						case "no timing":
							return FilterType.NOTIMING;
						case "has deadline":
							return FilterType.DEADLINED;
						default:
							return FilterType.NULL;
					}
				} else {
					switch (contents[0]) {
						case "deadlined":
							return FilterType.DEADLINED;
						case "event":
						case "timed":
							return FilterType.EVENT;
						default:
							return FilterType.NULL;
					}
				}
			}
		}
	}

	private void checkSortValidity(String[] contents, String[] contentsWithIndex)
			throws StreamParserException {
		String sortBy = contents.length > 1 ? contentsWithIndex[PARAM_POS_SORTTYPE]
				: "";
		String order = contentsWithIndex.length > 2 ? contentsWithIndex[PARAM_POS_SORTORDER]
				: "";
		if (!checkSort(sortBy, order)) {
			throw new StreamParserException(ERROR_INVALID_SORT);
		} else if (contents.length > 1) {
			this.commandContent = contents[PARAM_POS_SORTTYPE];
		} else {
			this.commandContent = null;
		}
	}

	private boolean checkRanking(String rankInput) {
		RankType parsedRank = parseRanking(rankInput);
		switch (parsedRank) {
			case NULL:
				return false;
			default:
				return true;
		}
	}

	private boolean checkMarking(String markInput) {
		MarkType parsedMark = parseMarking(markInput);
		switch (parsedMark) {
			case NULL:
				return false;
			default:
				return true;
		}
	}

	private boolean checkFilter(String type) {
		FilterType parsedFilter = parseFilterType(type);
		switch (parsedFilter) {
			case NULL:
				return false;
			default:
				return true;
		}
	}

	private boolean checkSort(String sortBy, String order) {
		switch (parseSorting(sortBy)) {
			case NULL:
				return false;
			default:
				try {
					getSortingOrder(order);
					return true;
				} catch (StreamParserException e) {
					return false;
				}
		}
	}

}
