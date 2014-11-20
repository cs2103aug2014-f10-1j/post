package stream;

import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.ImageIcon;

import logger.Loggable;
import logger.StreamLogger;
import logic.StackLogic;
import logic.StreamLogic;
import logic.TaskLogic;
import model.StreamObject;
import model.StreamTask;
import parser.FilterParser.FilterType;
import parser.StreamCommand;
import parser.StreamParser;
import parser.StreamCommand.CommandType;
import parser.MarkParser.MarkType;
import parser.SortParser.SortType;
import ui.StreamUI;
import util.StreamConstants;
import util.StreamExternals;
import util.StreamUtil;
import exception.StreamIOException;
import exception.StreamModificationException;
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

	StreamObject streamObject = StreamObject.getInstance();
	StreamUI stui;
	TaskLogic taskLogic = TaskLogic.init();
	StackLogic stackLogic = StackLogic.init();
	StreamLogic streamLogic = StreamLogic.init(streamObject);

	private StreamParser parser;

	private String filename;

	private static final String THANK_YOU = "Thank you for using STREAM!";

	public static final String VERSION = "V0.6";
	private static final String FILENAME = "stream";
	static final String SAVEFILE_EXTENSION = ".json";
	private static final String SAVEFILE_FORMAT = "%1$s" + SAVEFILE_EXTENSION;
	private static final String LOGFILE_FORMAT = "%1$s.txt";
	private static final SimpleDateFormat LOGFILE_DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	//@author A0118007R
	/**
	 * Stream Constructor to initialize the program.
	 */
	public Stream(String file) {
		initializeExtFiles();
		initStreamIO(file);
		initializeStream();
		load();
		refreshUI();
	}

	private void initializeStream() {
		stui = new StreamUI(this);
		parser = new StreamParser();
	}

	//@author A0093874N
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

	//@author A0096529N
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

	//@author A0118007R
	private void executeInput(StreamCommand cmd)
			throws StreamModificationException, StreamIOException {
		CommandType command = cmd.getKey();
		Integer index = cmd.getIndex();
		Object content = cmd.getContent();
		switch (command) {
			case ADD:
				executeAdd((String) content);
				refreshUI();
				break;

			case DEL:
				executeDelete(index);
				refreshUI();
				break;

			case DESC:
				executeDescribe(index, (String) content);
				refreshUI();
				break;

			case DUE:
				executeDue(index, (Calendar) content);
				refreshUI();
				break;

			case START:
				executeStartTime(index, (Calendar) content);
				refreshUI();
				break;

			case MODIFY:
				executeModify(index, (String) content);
				refreshUI();
				break;

			case NAME:
				executeName(index, (String) content);
				refreshUI();
				break;

			case RANK:
				executeRank(index, (String) content);
				refreshUI();
				break;

			case MARK:
				executeMark(index, (MarkType) content);
				refreshUI();
				break;

			case TAG:
				executeTag(index, (String) content);
				refreshUI();
				break;

			case UNTAG:
				executeUntag(index, (String) content);
				refreshUI();
				break;

			case FILTER:
				ArrayList<Integer> filterResult = executeFilter((String) content);
				refreshUI(filterResult, true, true);
				break;

			case CLRSRC:
				refreshUI();
				break;

			case SEARCH:
				ArrayList<Integer> searchResult = executeSearch((String) content);
				refreshUI(searchResult, true, true);
				break;

			case SORT:
				executeSort((String) content);
				refreshUI();
				break;

			case VIEW:
				executeView(index);
				refreshUI();
				break;

			case CLEAR:
				executeClear();
				refreshUI();
				break;

			case UNDO:
				executeUndo();
				break;

			case RECOVER:
				executeRecover(index);
				refreshUI();
				break;

			case DISMISS:
				executeDismiss(index);
				refreshUI();
				break;

			case UNSORT:
				executeUnsort();
				refreshUI();
				break;

			case FIRST:
				stui.goToFirstPage();
				break;

			case PREV:
				stui.goToPrevPage();
				break;

			case NEXT:
				stui.goToNextPage();
				break;

			case LAST:
				stui.goToLastPage();
				break;

			case PAGE:
				stui.goToPage(index);
				break;

			case HELP:
				stui.openHelpBox();
				break;

			case EXIT:
				executeExit();

			default:
				showAndLogError(StreamConstants.LogMessage.CMD_UNKNOWN);
		}
	}

	/**
	 * Adds a new task to the tasks list.
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null
	 * </p>
	 * 
	 * @param taskNameWithParams
	 *            - the task name
	 * @throws StreamModificationException
	 *             - if a <b>StreamTask<b> named <i>taskName</i> is already
	 *             present.
	 * @return <strong>String</strong> - the log message
	 */
	private void executeAdd(String taskNameWithParams)
			throws StreamModificationException {

		assertNotNull(taskNameWithParams);
		String[] contents = taskNameWithParams.split(" ");
		String taskName = "";
		ArrayList<String> modifyParams = new ArrayList<String>();

		for (int i = 0; i < contents.length; i++) {
			String word = contents[i];
			if (StreamUtil.isValidAttribute(word)) {
				appendEverything(contents, modifyParams, i);
				break;
			} else {
				taskName = taskName + word + " ";
			}
		}

		taskName = taskName.trim();
		addTaskWithParams(taskName, modifyParams);

		StreamTask task = streamLogic.getTask(taskName);
		stui.setActiveTask(task);

		String result = String.format(StreamConstants.LogMessage.ADD, taskName);
		showAndLogResult(result);
	}

	/**
	 * Appends all trailing strings that are not part of a task's name to be
	 * modified as parameters.
	 * 
	 * @param contents
	 *            - the trailing strings
	 * @param modifyParams
	 *            - the storage for parameter modification
	 * @param i
	 *            - the starting index
	 */
	private void appendEverything(String[] contents,
			ArrayList<String> modifyParams, int i) {
		for (int k = i - 1; k < contents.length; k++) {
			modifyParams.add(contents[k]);
		}
	}

	/**
	 * Deletes a task from the tasks list and then archives it so it can be
	 * recovered by undo process.
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null.
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeDelete(Integer taskIndex)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		assertNotNull(taskName);
		StreamTask deletedTask = streamLogic.getTask(taskName);
		ArrayList<String> order = streamLogic.getTaskList();

		assertNotNull(taskName);

		streamLogic.deleteTask(taskName);
		assertNoTask(taskName);

		streamLogic.orderLogic.push(order);
		stackLogic.pushInverseDeleteCommand(deletedTask, order);
		String result = String.format(StreamConstants.LogMessage.DELETE,
				taskName);
		showAndLogResult(result);
	}

	/**
	 * Asserts if the given task name does not exists.
	 * 
	 * @param taskName
	 *            - the task name to be checked whether it exists or not
	 */
	private void assertNoTask(String taskName) {
		assert (!streamLogic.hasTask(taskName)) : StreamConstants.Assertion.NOT_DELETED;
	}

	/**
	 * Asserts if the task name is null.
	 * 
	 * @param taskName
	 *            - the task name to be chekced whether it is null
	 */
	private void assertNotNull(String taskName) {
		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
	}

	//@author A0093874N
	/**
	 * Deletes a task from the tasks list permanently.
	 * 
	 * @throws StreamModificationException
	 */
	private void executeDismiss(Integer taskIndex)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);

		assertNotNull(taskName);
		streamLogic.deleteTask(taskName);
		assertNoTask(taskName);

		stackLogic.pushPlaceholderInput();
		String result = String.format(StreamConstants.LogMessage.DELETE,
				taskName);
		showAndLogResult(result);
	}

	/**
	 * Clears all tasks upon receiving the command "clear".
	 * 
	 * @throws StreamModificationException
	 */
	private void executeClear() throws StreamModificationException {
		streamLogic.orderLogic.push(streamLogic.getTaskList());
		stackLogic.pushInverseClearCommand(streamLogic.getTaskList(),
				streamLogic.getStreamTaskList());
		streamLogic.clear();
		assert (streamLogic.getNumberOfTasks() == 0) : StreamConstants.Assertion.NOT_CLEARED;
		showAndLogResult(StreamConstants.LogMessage.CLEAR);
	}

	//@author A0118007R
	/**
	 * Prints the task details.
	 * 
	 * @throws StreamModificationException
	 */
	private void executeView(Integer taskIndex)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);

		assertNotNull(taskName);
		StreamTask currentTask = streamLogic.getTask(taskName);
		stui.displayDetails(currentTask);
		stui.setActiveTask(currentTask);

		// This section is contributed by A0093874N
		String result = String
				.format(StreamConstants.LogMessage.VIEW, taskName);
		showAndLogResult(result);
	}

	/**
	 * Adds a description to a task.
	 * <p>
	 * Pre-condition: <i>task, index, description</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeDescribe(Integer taskIndex, String description)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask currentTask = streamLogic.getTask(taskName);
		String oldDescription = currentTask.getDescription();
		currentTask.setDescription(description.equals("null") ? null
				: description);
		stui.setActiveTask(currentTask);

		stackLogic.pushInverseSetDescriptionCommand(taskIndex, oldDescription);
		String result = String.format(StreamConstants.LogMessage.DESC,
				currentTask.getTaskName(), description);
		showAndLogResult(result);
	}

	//@author A0119401U
	/**
	 * Adds a rank to a task.
	 * <p>
	 * Pre-condition: <i>task, index, description</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeRank(Integer taskIndex, String taskRank)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask currentTask = streamLogic.getTask(taskName);
		String oldRank = currentTask.getRank();
		currentTask.setRank(taskRank);
		stui.setActiveTask(currentTask);

		stackLogic.pushInverseSetRankingCommand(taskIndex, oldRank);
		String result = String.format(StreamConstants.LogMessage.RANK,
				currentTask.getTaskName(), taskRank);
		showAndLogResult(result);
	}

	//@author A0118007R
	/**
	 * Changes the name of a task.
	 * <p>
	 * Pre-condition: <i>task, index, name</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeName(Integer taskIndex, String newTaskName)
			throws StreamModificationException {
		String oldTaskName = streamLogic.getTaskNumber(taskIndex);
		streamLogic.updateTaskName(oldTaskName, newTaskName);
		StreamTask task = streamLogic.getTask(newTaskName);
		stui.setActiveTask(task);

		stackLogic.pushInverseSetNameCommand(taskIndex, oldTaskName);

		String result = String.format(StreamConstants.LogMessage.NAME,
				oldTaskName, newTaskName);
		showAndLogResult(result);
	}

	/**
	 * Modifies various parameters of a task.
	 * <p>
	 * Pre-condition: <i>task, index, specified params</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeModify(Integer taskIndex, String content)
			throws StreamModificationException {
		String[] contents = content.split(" ");
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask currTask = streamLogic.getTask(taskName);

		String inverseCommand = stackLogic.prepareInverseModifyCommand(
				taskName, taskIndex, currTask);

		streamLogic.modifyTaskWithParams(taskName, Arrays.asList(contents));
		stackLogic.pushInverseModifyCommand(inverseCommand);
		stui.setActiveTask(currTask);

		String result = String.format(StreamConstants.LogMessage.MODIFY,
				taskName);
		showAndLogResult(result);
	}

	//@author A0093874N
	/**
	 * Untags some tags that are specified in the input.
	 * <p>
	 * Pre-condition: <i>task, index, tags</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeUntag(Integer taskIndex, String content)
			throws StreamModificationException {
		String[] tags = content.split(" ");
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask task = streamLogic.getTask(taskName);
		ArrayList<String> processedTags = taskLogic.removeTags(task, tags);
		stackLogic.pushInverseUntagCommand(taskIndex, processedTags);
		stui.setActiveTask(task);
		logRemovedTags(taskName, processedTags);
	}

	/**
	 * Tag some tags that are specified in the input.
	 * <p>
	 * Pre-condition: <i>task, index, tags</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeTag(Integer taskIndex, String content)
			throws StreamModificationException {
		String[] tags = content.split(" ");
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask task = streamLogic.getTask(taskName);
		ArrayList<String> processedTags = taskLogic.addTags(task, tags);
		stackLogic.pushInverseAddTagCommand(taskIndex, processedTags);
		stui.setActiveTask(task);
		logAddedTags(taskName, processedTags);
	}

	/**
	 * Reverts ordering after being sorted.
	 * 
	 * @return <strong>String</strong> - the log message
	 */
	private void executeUnsort() {
		ArrayList<String> order = streamLogic.orderLogic.pop();
		streamLogic.orderLogic.setOrdering(order);
		stackLogic.pushPlaceholderInput();
		showAndLogResult(StreamConstants.LogMessage.UNSORT);
	}

	/**
	 * Recovers deleted task from the archive.
	 */
	private void executeRecover(Integer noOfTasksToRecover) {
		stackLogic.pushPlaceholderInput();

		for (int i = 0; i < noOfTasksToRecover; i++) {
			StreamTask task = stackLogic.recoverTask();
			streamLogic.recoverTask(task);
		}
		streamLogic.orderLogic.setOrdering(streamLogic.orderLogic.pop());

		String result = String.format(StreamConstants.LogMessage.RECOVER,
				noOfTasksToRecover);
		showAndLogResult(result);
	}

	/**
	 * Execute the undo operation for the last user action
	 */
	private void executeUndo() {
		if (!stackLogic.hasInverseInput()) {
			showAndLogResult(StreamConstants.LogMessage.UNDO_FAIL);
		} else {
			String undoneInput = stackLogic.popInverseCommand();
			showAndLogResult(StreamConstants.LogMessage.UNDO_SUCCESS);
			logDebug(StreamUtil.showAsTerminalInput(undoneInput));
			processInput(undoneInput);

			/*
			 * VERY IMPORTANT because almost all inputs will add its counterpart
			 * to the inputStack. If not popped, the undo process will be
			 * trapped between just two processes.
			 */
			stackLogic.popInverseCommand();
		}
	}

	//@author A0118007R
	/**
	 * Marks the category as the specified category
	 * 
	 * @param taskIndex
	 * @param markType
	 *            - the category to be used for marking
	 * @throws StreamModificationException
	 */
	private void executeMark(Integer taskIndex, MarkType markType)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask task = streamLogic.getTask(taskName);
		String result = null;
		result = processMarking(taskIndex, markType, task);
		stui.setActiveTask(task);
		showAndLogResult(result);
	}

	private String processMarking(Integer taskIndex, MarkType markType,
			StreamTask task) {
		String result;
		switch (markType) {
			case DONE:
				result = markAsDone(task, taskIndex);
				break;
			case NOT:
				result = markAsOngoing(task, taskIndex);
				break;
			case INACTIVE:
			case OVERDUE:
				result = "Disallowed marking type: " + markType;
				break;
			default:
				// should not happen, but let's play safe
				result = "Unknown marking type: " + markType;
		}
		return result;
	}

	//@author A0118007R
	private void executeDue(Integer taskIndex, Calendar content)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		String result = null;
		result = processDue(content, taskIndex, taskName);
		StreamTask task = streamLogic.getTask(taskName);
		stui.setActiveTask(task);
		showAndLogResult(result);
	}

	private String processDue(Calendar content, int taskIndex, String taskName)
			throws StreamModificationException {
		String result = setDueDate(taskName, taskIndex, content);
		return result;
	}

	private void executeStartTime(Integer taskIndex, Calendar content)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		String result = processStartTime(content, taskIndex, taskName);
		StreamTask task = streamLogic.getTask(taskName);
		stui.setActiveTask(task);
		showAndLogResult(result);
	}

	private String processStartTime(Calendar content, int taskIndex,
			String taskName) throws StreamModificationException {
		String result = setStartDate(taskName, taskIndex, content);
		return result;
	}

	//@author A0096529N
	// updated by A0119401U
	private void executeSort(String content) {
		ArrayList<String> oldOrdering = streamLogic.getTaskList();
		stackLogic.pushInverseSortCommand(oldOrdering);
		streamLogic.orderLogic.push(oldOrdering);

		String result = null;
		String sortBy = null;
		String order = null;
		boolean descending = true;
		if (content != null && content.contains(" ")) {
			sortBy = content.split(" ")[0];
			order = content.split(" ")[1];
		} else {
			sortBy = content == null ? "" : content;
			order = "";
		}
		SortType type = StreamParser.sp.parse(sortBy);
		try {
			descending = StreamParser.sp.getOrder(order);
		} catch (Exception e) {
			// ok to ignore
		}

		result = streamLogic.sort(type, descending);
		showAndLogResult(result);
	}

	private void executeExit() {
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

	/**
	 * Search for tasks with specified key phrase, in the task name, description
	 * and tags.
	 * <p>
	 * Key phrase will be broken down into key words (by splitting with space
	 * character). Key words will be used to search in the tags.
	 * </p>
	 * 
	 * <p>
	 * Precondition: keyphrase != null
	 * </p>
	 * 
	 * @return tasks - a list of tasks containing the key phrase.
	 */
	private ArrayList<Integer> executeSearch(String content) {
		assertNotNull(content);
		ArrayList<Integer> searchResult = streamLogic.searchLogic
				.findTasks(content);

		String result = String.format(StreamConstants.LogMessage.SEARCH,
				content, searchResult.size());
		showAndLogResult(result);

		return searchResult;
	}

	//@author A0093874N
	private ArrayList<Integer> executeFilter(String content) {
		assertNotNull(content);
		ArrayList<Integer> filterResult = streamLogic.searchLogic
				.filterTasks(content);

		FilterType type = StreamParser.fp.parse(content);
		String log = StreamParser.fp.translate(type);
		switch (type) {
			case DUEBEF:
			case DUEAFT:
			case STARTBEF:
			case STARTAFT:
				String time = content.split(" ", 3)[2];
				log += time;
			default:
				// no extra work needed
		}
		String result = String.format(StreamConstants.LogMessage.FILTER, log,
				filterResult.size());
		showAndLogResult(result);

		return filterResult;
	}

	//@author A0118007R
	private void addTaskWithParams(String taskName,
			ArrayList<String> modifyParams) throws StreamModificationException {

		streamLogic.addTask(taskName);
		assert (streamLogic.hasTask(taskName)) : StreamConstants.Assertion.NOT_ADDED;
		stackLogic.pushInverseAddCommand(streamLogic.getNumberOfTasks());
		processParameterAddition(taskName, modifyParams);
	}

	private void processParameterAddition(String taskName,
			ArrayList<String> modifyParams) throws StreamModificationException {
		if (modifyParams.size() > 0) {
			streamLogic.modifyTaskWithParams(taskName, modifyParams);
		}
	}

	//@author A0119401U
	/**
	 * Set the due date of the selected task
	 * 
	 * @throws StreamModificationException
	 */
	private String setDueDate(String taskName, int taskIndex,
			Calendar newDeadline) throws StreamModificationException {
		StreamTask task = streamLogic.getTask(taskName);
		Calendar deadline = task.getDeadline();
		Calendar startTime = task.getStartTime();
		return processDueDate(taskIndex, newDeadline, task, deadline, startTime);
	}

	private String processDueDate(int taskIndex, Calendar newDeadline,
			StreamTask task, Calendar deadline, Calendar startTime) {
		if (StreamUtil.isValidDeadline(newDeadline, startTime)) {
			stackLogic.pushInverseDueCommand(taskIndex, deadline);
			// This section is contributed by A0093874N
			return taskLogic.setDeadline(task, newDeadline);
			//
		} else {
			return StreamConstants.ExceptionMessage.ERR_DEADLINE_BEFORE_STARTTIME;
		}
	}

	/**
	 * Set the start date of the selected task
	 * 
	 * @throws StreamModificationException
	 */
	private String setStartDate(String taskName, int taskIndex,
			Calendar newStartTime) throws StreamModificationException {
		StreamTask currentTask = streamLogic.getTask(taskName);
		Calendar currentStartTime = currentTask.getStartTime();
		Calendar deadline = currentTask.getDeadline();
		return processStartTime(taskIndex, newStartTime, currentTask,
				currentStartTime, deadline);
	}

	private String processStartTime(int taskIndex, Calendar newStartTime,
			StreamTask currentTask, Calendar currentStartTime, Calendar deadline) {
		if (StreamUtil.isValidStartTime(deadline, newStartTime)) {
			stackLogic.pushInverseStartCommand(taskIndex, currentStartTime);
			return taskLogic.setStartTime(currentTask, newStartTime);
		} else {
			return StreamConstants.ExceptionMessage.ERR_STARTTIME_AFTER_DEADLINE;
		}
	}

	/**
	 * Mark the selected task as done
	 * 
	 * @return <strong>String</strong> - the log message
	 */
	private String markAsDone(StreamTask task, int index) {
		boolean wasDone = task.isDone();
		task.markAsDone();

		stackLogic.pushInverseSetDoneCommand(wasDone, index);
		// This section is contributed by A0093874N
		return String.format(StreamConstants.LogMessage.MARK,
				task.getTaskName(), "done");
		//
	}

	//@author A0118007R
	/**
	 * Mark the selected task as ongoing
	 * 
	 * @return <strong>String</strong> - the log message
	 */
	private String markAsOngoing(StreamTask task, int index) {
		boolean wasDone = task.isDone();
		task.markAsOngoing();

		stackLogic.pushInverseSetDoneCommand(wasDone, index);
		// This section is contributed by A0093874N
		return String.format(StreamConstants.LogMessage.MARK,
				task.getTaskName(), "ongoing");
		//
	}

	//@author A0096529N
	private void refreshUI() {
		refreshUI(false, false);
	}

	//@author A0093874N
	private void refreshUI(boolean isReset, boolean isSearching) {
		stui.resetAvailableTasks(streamLogic.getIndices(),
				streamLogic.getStreamTaskList(streamLogic.getIndices()),
				isReset, isSearching);
	}

	private void refreshUI(ArrayList<Integer> index, boolean isReset,
			boolean isSearching) {
		stui.resetAvailableTasks(index, streamLogic.getStreamTaskList(index),
				isReset, isSearching);
	}

	private void logAddedTags(String taskName, ArrayList<String> tagsAdded) {
		if (!tagsAdded.isEmpty()) {
			showAndLogResult(String.format(
					StreamConstants.LogMessage.TAGS_ADDED, taskName,
					StreamUtil.listDownArrayContent(tagsAdded, ", ")));
		} else {
			showAndLogResult(StreamConstants.LogMessage.NO_TAGS_ADDED);
		}
	}

	private void logRemovedTags(String taskName, ArrayList<String> tagsRemoved) {
		if (!tagsRemoved.isEmpty()) {
			showAndLogResult(String.format(
					StreamConstants.LogMessage.TAGS_REMOVED, taskName,
					StreamUtil.listDownArrayContent(tagsRemoved, ", ")));
		} else {
			showAndLogResult(StreamConstants.LogMessage.NO_TAGS_REMOVED);
		}
	}

	private void showAndLogResult(String logMessage) {
		showAndLogResult(logMessage, logMessage);
	}

	//@author A0118007R
	private void showAndLogError(String errorMessage) {
		showAndLogError(errorMessage, errorMessage);
	}

	//@author A0093874N
	private void showAndLogResult(String logMessageForDoc,
			String logMessageForUser) {
		stui.log(logMessageForUser, false);
		logDebug(StreamUtil.showAsTerminalResponse(logMessageForDoc));
	}

	private void showAndLogError(String errorMessageForDoc,
			String errorMessageForUser) {
		stui.log(errorMessageForUser, true);
		logError(StreamUtil.showAsTerminalResponse(errorMessageForDoc));
	}

	//@author A0093874N
	private void processInput(String input) {
		try {
			StreamCommand cmd = parser.parseCommand(input,
					streamLogic.getNumberOfTasks());
			executeInput(cmd);
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
			showAndLogError(StreamConstants.LogMessage.CMD_UNKNOWN);
		} else {
			processInput(input);
			save();
		}
	}

	public static void main(String[] args) {
		new Stream(FILENAME);
	}

	@Override
	public String getComponentName() {
		return "STREAM";
	}

}