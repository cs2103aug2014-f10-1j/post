package parser;

import parser.StreamCommand.CommandType;
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

	public static MarkParser mp = new MarkParser();
	public static RankParser rp = new RankParser();
	public static FilterParser fp = new FilterParser();
	public static SortParser sp = new SortParser();
	public static TimeParser tp = new TimeParser();
	
	private static final StreamLogger logger = StreamLogger
			.init(StreamConstants.ComponentTag.STREAMPARSER);

	static final String ERROR_INCOMPLETE_INPUT = "Please provide more information!";
	static final String ERROR_INCOMPLETE_INDEX = "Please provide the index or page number!";
	static final String ERROR_INVALID_INDEX = "Please enter a valid index number!";
	static final String ERROR_INVALID_FILTER = "Please enter a valid filter type!";
	static final String ERROR_INVALID_SORT = "Please enter a valid sorting type!";
	static final String ERROR_INVALID_MARK = "Please enter a valid marking type!";
	static final String ERROR_INVALID_RANK = "Please enter a valid ranking type!";
	public static final String ERROR_EMPTY_INPUT = "Please enter a command!";
	static final String ERROR_INDEX_OUT_OF_BOUNDS = "The index you entered is out of range!";
	static final String ERROR_DATE_NOT_PARSEABLE = "Date cannot be understood!";
	static final String ERROR_UNKNOWN_COMMAND = "Unknown command type!";

	static final String LOG_COMMAND_NO_ARGS = "Command received: [%1$s]. No arguments supplied.";
	static final String LOG_COMMAND_WITH_INDEX = "Command received: [%1$s]. Index number supplied: %2$s.";
	static final String LOG_COMMAND_WITH_ARGS = "Command received: [%1$s]. Arguments supplied: %2$s.";
	static final String LOG_COMMAND_WITH_INDEX_AND_ARGS = "Command received: [%1$s]. Index number: %2$s. Arguments: %3$s.";
	static final String LOG_COMMAND_UNKNOWN = "Unknown command received: [%1$s]";

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

	public static StreamParser init() {
		return new StreamParser();
	}

	public StreamCommand interpretCommand(String input, int numOfTasks)
			throws StreamParserException {
		if (input.isEmpty()) {
			throw new StreamParserException(ERROR_EMPTY_INPUT);
		}
		String[] contents = input.trim().split(" ", 2);
		String[] contentsSplitWithIndex = input.trim().split(" ", 3);
		String key = contents[PARAM_POS_KEYWORD].toLowerCase();
		StreamCommand cmd = new StreamCommand();
		switch (key) {
			case "add":
				checkTypeOneValidity(cmd, contents);
				cmd.setKey(CommandType.ADD);
				break;

			case "del":
			case "delete":
				checkTypeTwoValidity(cmd, contents, numOfTasks);
				cmd.setKey(CommandType.DEL);
				break;

			case "desc":
			case "describe":
				checkTypeThreeValidity(cmd, contentsSplitWithIndex, numOfTasks);
				cmd.setKey(CommandType.DESC);
				break;

			case "due":
			case "end":
				checkDateValidity(cmd, contentsSplitWithIndex, numOfTasks);
				cmd.setKey(CommandType.DUE);
				break;

			case "start":
				checkDateValidity(cmd, contentsSplitWithIndex, numOfTasks);
				cmd.setKey(CommandType.START);
				break;

			case "view":
				checkTypeTwoValidity(cmd, contents, numOfTasks);
				cmd.setKey(CommandType.VIEW);
				break;

			case "rank":
				checkRankValidity(cmd, contentsSplitWithIndex, numOfTasks);
				cmd.setKey(CommandType.RANK);
				break;

			case "mod":
			case "modify":
				checkTypeThreeValidity(cmd, contentsSplitWithIndex, numOfTasks);
				cmd.setKey(CommandType.MODIFY);
				break;

			case "name":
				checkTypeThreeValidity(cmd, contentsSplitWithIndex, numOfTasks);
				cmd.setKey(CommandType.NAME);
				break;

			case "mark":
				checkMarkValidity(cmd, contentsSplitWithIndex, numOfTasks);
				cmd.setKey(CommandType.MARK);
				break;

			case "tag":
				checkTypeThreeValidity(cmd, contentsSplitWithIndex, numOfTasks);
				cmd.setKey(CommandType.TAG);
				break;

			case "untag":
				checkTypeThreeValidity(cmd, contentsSplitWithIndex, numOfTasks);
				cmd.setKey(CommandType.UNTAG);
				break;

			case "search":
			case "find":
				checkTypeOneValidity(cmd, contents);
				cmd.setKey(CommandType.SEARCH);
				break;

			case "sort":
				checkSortValidity(cmd, contents, contentsSplitWithIndex);
				cmd.setKey(CommandType.SORT);
				break;

			case "unsort":
				cmd.setKey(CommandType.UNSORT);
				break;

			case "filter":
				checkFilterValidity(cmd, contents);
				cmd.setKey(CommandType.FILTER);
				break;

			case "clrsrc":
				cmd.setKey(CommandType.CLRSRC);
				break;

			case "clear":
			case "clr":
				cmd.setKey(CommandType.CLEAR);
				break;

			case "undo":
				cmd.setKey(CommandType.UNDO);
				break;

			case "recover":
				cmd.setKey(CommandType.RECOVER);
				cmd.setIndex(Integer.parseInt(contents[PARAM_POS_INDEX]));
				break;

			case "dismiss":
				cmd.setKey(CommandType.DISMISS);
				cmd.setIndex(Integer.parseInt(contents[PARAM_POS_INDEX]));
				break;

			case "exit":
				cmd.setKey(CommandType.EXIT);
				break;

			case "first":
				cmd.setKey(CommandType.FIRST);
				break;

			case "prev":
			case "previous":
				cmd.setKey(CommandType.PREV);
				break;

			case "next":
				cmd.setKey(CommandType.NEXT);
				break;

			case "last":
				cmd.setKey(CommandType.LAST);
				break;

			case "page":
			case "goto":
				checkTypeTwoValidity(cmd, contents, 1 + numOfTasks
						/ StreamConstants.UI.MAX_VIEWABLE_TASK);
				cmd.setKey(CommandType.PAGE);
				break;

			case "help":
				cmd.setKey(CommandType.HELP);
				break;

			default:
				logger.log(LogLevel.DEBUG,
						String.format(LOG_COMMAND_UNKNOWN, key));
				throw new StreamParserException(ERROR_UNKNOWN_COMMAND);

		}
		logCommand(contents, contentsSplitWithIndex);
		return cmd;
	}

	private void checkIndexValidity(StreamCommand cmd, String[] contents,
			int numOfTasks) throws StreamParserException {
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
	private void checkTypeOneValidity(StreamCommand cmd, String[] contents)
			throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_ONE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		}
		cmd.setContent(contents[PARAM_POS_CONTENTS]);
	}

	/*
	 * Type two command: commands with format (CommandWord) (index number)
	 */
	private void checkTypeTwoValidity(StreamCommand cmd, String[] contents,
			int numOfTasks) throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_TWO) {
			throw new StreamParserException(ERROR_INCOMPLETE_INDEX);
		} else {
			checkIndexValidity(cmd, contents, numOfTasks);
			cmd.setIndex(Integer.parseInt(contents[PARAM_POS_INDEX]));
		}
	}

	/*
	 * Type three command: commands with format (CommandWord) (index number)
	 * (String arguments of any length)
	 */
	private void checkTypeThreeValidity(StreamCommand cmd, String[] contents,
			int numOfTasks) throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_THREE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(cmd, contents, numOfTasks);
			cmd.setIndex(Integer.parseInt(contents[PARAM_POS_INDEX]));
			cmd.setContent(contents[PARAM_POS_ARGS]);
		}
	}

	/*
	 * Type four command: commands with format (CommandWord) (index number)
	 * (date String to be parsed)
	 */
	private void checkDateValidity(StreamCommand cmd, String[] contents,
			int numOfTasks) throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_THREE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(cmd, contents, numOfTasks);
			if (!tp.isParseable(contents[PARAM_POS_ARGS])) {
				throw new StreamParserException(ERROR_DATE_NOT_PARSEABLE);
			}
			cmd.setIndex(Integer.parseInt(contents[PARAM_POS_INDEX]));
			cmd.setContent(tp.parse(contents[PARAM_POS_ARGS]));
		}
	}

	private void checkRankValidity(StreamCommand cmd, String[] contents,
			int numOfTasks) throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_THREE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(cmd, contents, numOfTasks);
			if (!rp.isParseable(contents[PARAM_POS_ARGS])) {
				throw new StreamParserException(ERROR_INVALID_RANK);
			}
			cmd.setIndex(Integer.parseInt(contents[PARAM_POS_INDEX]));
			cmd.setContent(rp.translate(rp.parse(contents[PARAM_POS_ARGS])));
		}
	}

	private void checkMarkValidity(StreamCommand cmd, String[] contents,
			int numOfTasks) throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_THREE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(cmd, contents, numOfTasks);
			if (!mp.isParseable(contents[PARAM_POS_ARGS])) {
				throw new StreamParserException(ERROR_INVALID_MARK);
			}
			cmd.setIndex(Integer.parseInt(contents[PARAM_POS_INDEX]));
			cmd.setContent(mp.parse(contents[PARAM_POS_ARGS]));
		}
	}

	private void checkFilterValidity(StreamCommand cmd, String[] contents)
			throws StreamParserException {
		if (contents.length < ARGS_LENGTH_TYPE_ONE) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else if (!fp.isParseable(contents[PARAM_POS_FILTERTYPE].trim())) {
			throw new StreamParserException(ERROR_INVALID_FILTER);
		}
		cmd.setContent(contents[PARAM_POS_CONTENTS]);
	}

	private void checkSortValidity(StreamCommand cmd, String[] contents,
			String[] contentsWithIndex) throws StreamParserException {
		String sortBy = contents.length > 1 ? contentsWithIndex[PARAM_POS_SORTTYPE]
				: "";
		String order = contentsWithIndex.length > 2 ? contentsWithIndex[PARAM_POS_SORTORDER]
				: "";
		if (!sp.isParseable(sortBy + " " + order)) {
			throw new StreamParserException(ERROR_INVALID_SORT);
		} else if (contents.length > 1) {
			cmd.setContent(contents[PARAM_POS_SORTTYPE]);
		} else {
			cmd.setContent(null);
		}
	}

	private void logCommand(String[] contents, String[] contentsWithIndex) {
		String commandKey = contents[PARAM_POS_KEYWORD].toUpperCase();
		if (contentsWithIndex.length >= 3
				&& StreamUtil.isInteger(contentsWithIndex[PARAM_POS_INDEX])) {
			logger.log(LogLevel.DEBUG, String.format(
					LOG_COMMAND_WITH_INDEX_AND_ARGS, commandKey,
					contentsWithIndex[PARAM_POS_INDEX],
					contentsWithIndex[PARAM_POS_ARGS]));
		} else if (contents.length == 2) {
			if (StreamUtil.isInteger(contents[1])) {
				logger.log(LogLevel.DEBUG, String.format(
						LOG_COMMAND_WITH_INDEX, commandKey,
						contents[PARAM_POS_CONTENTS]));
			} else {
				logger.log(LogLevel.DEBUG, String.format(LOG_COMMAND_WITH_ARGS,
						commandKey, contents[PARAM_POS_CONTENTS]));
			}
		} else {
			logger.log(LogLevel.DEBUG,
					String.format(LOG_COMMAND_NO_ARGS, commandKey));
		}
	}
}
