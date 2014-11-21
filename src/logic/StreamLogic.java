package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import parser.StreamCommand;
import parser.StreamParser;
import parser.FilterParser.FilterType;
import parser.MarkParser.MarkType;
import parser.SortParser.SortType;
import parser.StreamCommand.CommandType;
import stream.Stream;
import ui.StreamUI;
import util.StreamConstants;
import util.StreamUtil;
import exception.StreamIOException;
import exception.StreamModificationException;
import exception.StreamParserException;
import logger.Loggable;
import model.StreamObject;
import model.StreamTask;

//@author A0118007R
/**
 * <h1>StreamLogic - STREAM's core logic component</h1>
 * 
 * Consisting of various logic sub-components responsible for executing
 * different categories of user commands and a main class StreamLogic to execute
 * user command.
 */
public class StreamLogic extends Loggable {

	private Stream stream;
	private StreamObject streamObject;
	private StreamParser parser = StreamParser.init();

	public UndoLogic undoLogic;
	public CRDLogic crdLogic;
	public ModificationLogic modLogic;
	public OrderLogic orderLogic;
	public UIUpdaterLogic uiLogic;

	private StreamLogic(Stream str, StreamUI stui, StreamObject stobj) {
		this.stream = str;
		this.streamObject = stobj;
		this.undoLogic = UndoLogic.init();
		this.crdLogic = CRDLogic.init(stobj);
		this.modLogic = ModificationLogic.init(crdLogic);
		this.orderLogic = OrderLogic.init(stobj);
		this.uiLogic = UIUpdaterLogic.init(stui);
	}

	/**
	 * Initializes StreamLogic.
	 * 
	 * @param streamObject
	 * @return logic, the instance of the StreamLogic class
	 */
	public static StreamLogic init(Stream str, StreamUI stui,
			StreamObject streamObject) {
		return new StreamLogic(str, stui, streamObject);
	}

	//@author A0093874N
	/**
	 * Returns the indices of all tasks.
	 * 
	 * @return <strong>indices</strong> - the ArrayList containing the indices
	 *         of all tasks.
	 */
	public ArrayList<Integer> getIndices() {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < streamObject.size(); i++) {
			indices.add(i + 1);
		}
		return indices;
	}

	/**
	 * Gets the number of tasks added.
	 * 
	 */
	public int getNumberOfTasks() {
		return streamObject.size();
	}

	//@author A0096529N
	/**
	 * Retrieves the task name by index
	 * 
	 * <p>
	 * Precondition: index is a valid index
	 * </p>
	 * 
	 * @param index
	 *            the index of the task
	 */
	public String getTaskNumber(int index) {
		return streamObject.getTaskList().get(index - 1);
	}

	/**
	 * @return taskMap a copy of the task map.
	 */
	public HashMap<String, StreamTask> getTaskMap() {
		return new HashMap<String, StreamTask>(streamObject.getTaskMap());
	}

	/**
	 * @return taskList a copy of the task list.
	 */
	public ArrayList<String> getTaskList() {
		return new ArrayList<String>(streamObject.getTaskList());
	}

	/**
	 * @return taskList a copy of the task list.
	 */
	public ArrayList<StreamTask> getStreamTaskList() {
		ArrayList<StreamTask> taskList = new ArrayList<StreamTask>();
		HashMap<String, StreamTask> taskMap = streamObject.getTaskMap();
		for (String key : taskMap.keySet()) {
			taskList.add(taskMap.get(key));
		}
		return taskList;
	}

	//@author A0093874N

	public ArrayList<StreamTask> getStreamTaskList(ArrayList<Integer> indices) {
		ArrayList<StreamTask> tasks = new ArrayList<StreamTask>();
		HashMap<String, StreamTask> taskMap = streamObject.getTaskMap();
		ArrayList<String> taskList = streamObject.getTaskList();
		for (Integer index : indices) {
			tasks.add(taskMap.get(taskList.get(index - 1).toLowerCase()));
		}
		return tasks;
	}

	//@author generated
	@Override
	public String getComponentName() {
		return "STREAMLOGIC";
	}

	//@author A0118007R
	public String execute(String input) throws StreamModificationException,
			StreamIOException, StreamParserException {
		StreamCommand cmd = parser.parseCommand(input, getNumberOfTasks());
		CommandType command = cmd.getKey();
		Integer index = cmd.getIndex();
		Object content = cmd.getContent();
		String result;
		switch (command) {
			case ADD:
				result = executeAdd((String) content);
				refreshUI();
				break;

			case DEL:
				result = executeDelete(index);
				refreshUI();
				break;

			case DESC:
				result = executeDescribe(index, (String) content);
				refreshUI();
				break;

			case DUE:
				result = executeDue(index, (Calendar) content);
				refreshUI();
				break;

			case START:
				result = executeStartTime(index, (Calendar) content);
				refreshUI();
				break;

			case MODIFY:
				result = executeModify(index, (String) content);
				refreshUI();
				break;

			case NAME:
				result = executeName(index, (String) content);
				refreshUI();
				break;

			case RANK:
				result = executeRank(index, (String) content);
				refreshUI();
				break;

			case MARK:
				result = executeMark(index, (MarkType) content);
				refreshUI();
				break;

			case TAG:
				result = executeTag(index, (String) content);
				refreshUI();
				break;

			case UNTAG:
				result = executeUntag(index, (String) content);
				refreshUI();
				break;

			case FILTER:
				result = executeFilter((String) content);
				break;

			case CLRSRC:
				refreshUI();
				result = null;
				break;

			case SEARCH:
				result = executeSearch((String) content);
				break;

			case SORT:
				result = executeSort((String) content);
				refreshUI();
				break;

			case VIEW:
				result = executeView(index);
				refreshUI();
				break;

			case CLEAR:
				result = executeClear();
				refreshUI();
				break;

			case UNDO:
				result = executeUndo();
				break;

			case RECOVER:
				result = executeRecover(index);
				refreshUI();
				break;

			case DISMISS:
				result = executeDismiss(index);
				refreshUI();
				break;

			case UNSORT:
				result = executeUnsort();
				refreshUI();
				break;

			case FIRST:
				uiLogic.goToFirstPage();
				result = null;
				break;

			case PREV:
				uiLogic.goToPrevPage();
				result = null;
				break;

			case NEXT:
				uiLogic.goToNextPage();
				result = null;
				break;

			case LAST:
				uiLogic.goToLastPage();
				result = null;
				break;

			case PAGE:
				uiLogic.goToPage(index);
				result = null;
				break;

			case HELP:
				uiLogic.openHelpBox();
				result = null;
				break;

			case EXIT:
				stream.exit();

			default:
				// WILL NOT HAPPEN
				result = null;
				// showAndLogError(StreamConstants.LogMessage.CMD_UNKNOWN);
		}
		return result;
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
	private String executeAdd(String taskNameWithParams)
			throws StreamModificationException {

		assertNotNull(taskNameWithParams);
		String[] contents = taskNameWithParams.split(" ");
		String taskName = "";
		ArrayList<String> modifyParams = new ArrayList<String>();
		for (int i = 0; i < contents.length; i++) {
			String word = contents[i];
			if (modLogic.isValidAttribute(word)) {
				appendEverything(contents, modifyParams, i);
				break;
			} else {
				taskName = taskName + word + " ";
			}
		}

		taskName = taskName.trim();
		addTaskWithParams(taskName, modifyParams);

		StreamTask task = crdLogic.getTask(taskName);
		uiLogic.setActiveTask(task);

		String result = String.format(StreamConstants.LogMessage.ADD, taskName);
		return result;
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
	private String executeDelete(Integer taskIndex)
			throws StreamModificationException {
		String taskName = getTaskNumber(taskIndex);
		assertNotNull(taskName);
		StreamTask deletedTask = crdLogic.getTask(taskName);
		ArrayList<String> order = getTaskList();

		assertNotNull(taskName);

		crdLogic.deleteTask(taskName);
		assertNoTask(taskName);

		orderLogic.push(order);

		crdLogic.push(deletedTask);
		undoLogic.pushInverseDeleteCommand(deletedTask, order);
		String result = String.format(StreamConstants.LogMessage.DELETE,
				taskName);
		return result;
	}

	/**
	 * Asserts if the given task name does not exists.
	 * 
	 * @param taskName
	 *            - the task name to be checked whether it exists or not
	 */
	private void assertNoTask(String taskName) {
		assert (!crdLogic.hasTask(taskName)) : StreamConstants.Assertion.NOT_DELETED;
	}

	/**
	 * Asserts if the task name is null.
	 * 
	 * @param taskName
	 *            - the task name to be checked whether it is null
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
	private String executeDismiss(Integer taskIndex)
			throws StreamModificationException {
		String taskName = getTaskNumber(taskIndex);

		assertNotNull(taskName);
		crdLogic.deleteTask(taskName);
		assertNoTask(taskName);

		undoLogic.pushPlaceholderInput();
		String result = String.format(StreamConstants.LogMessage.DELETE,
				taskName);
		return result;
	}

	/**
	 * Clears all tasks upon receiving the command "clear".
	 * 
	 * @throws StreamModificationException
	 */
	private String executeClear() throws StreamModificationException {
		orderLogic.push(getTaskList());
		for (StreamTask task : getStreamTaskList()) {
			crdLogic.push(task);
		}
		undoLogic.pushInverseClearCommand(getTaskList(), getStreamTaskList());
		crdLogic.clear();
		assert (getNumberOfTasks() == 0) : StreamConstants.Assertion.NOT_CLEARED;
		return StreamConstants.LogMessage.CLEAR;
	}

	//@author A0118007R
	/**
	 * Prints the task details.
	 * 
	 * @throws StreamModificationException
	 */
	private String executeView(Integer taskIndex)
			throws StreamModificationException {
		String taskName = getTaskNumber(taskIndex);

		assertNotNull(taskName);
		StreamTask currentTask = crdLogic.getTask(taskName);
		uiLogic.displayDetails(currentTask);
		uiLogic.setActiveTask(currentTask);

		// This section is contributed by A0093874N
		String result = String
				.format(StreamConstants.LogMessage.VIEW, taskName);
		return result;
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
	private String executeDescribe(Integer taskIndex, String description)
			throws StreamModificationException {
		String taskName = getTaskNumber(taskIndex);
		StreamTask currentTask = crdLogic.getTask(taskName);
		String oldDescription = currentTask.getDescription();
		String result = modLogic.setDescription(currentTask, description);
		uiLogic.setActiveTask(currentTask);

		undoLogic.pushInverseSetDescriptionCommand(taskIndex, oldDescription);
		return result;
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
	private String executeRank(Integer taskIndex, String taskRank)
			throws StreamModificationException {
		String taskName = getTaskNumber(taskIndex);
		StreamTask currentTask = crdLogic.getTask(taskName);
		String oldRank = currentTask.getRank();
		String result = modLogic.setRank(currentTask, taskRank);
		uiLogic.setActiveTask(currentTask);

		undoLogic.pushInverseSetRankingCommand(taskIndex, oldRank);
		return result;
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
	private String executeName(Integer taskIndex, String newTaskName)
			throws StreamModificationException {
		String oldTaskName = getTaskNumber(taskIndex);
		modLogic.setName(oldTaskName, newTaskName);
		StreamTask task = crdLogic.getTask(newTaskName);
		uiLogic.setActiveTask(task);

		undoLogic.pushInverseSetNameCommand(taskIndex, oldTaskName);

		String result = String.format(StreamConstants.LogMessage.NAME,
				oldTaskName, newTaskName);
		return result;
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
	private String executeModify(Integer taskIndex, String content)
			throws StreamModificationException {
		String[] contents = content.split(" ");
		String taskName = getTaskNumber(taskIndex);
		StreamTask currTask = crdLogic.getTask(taskName);

		String inverseCommand = undoLogic.prepareInverseModifyCommand(taskName,
				taskIndex, currTask);

		modLogic.modifyTask(currTask, Arrays.asList(contents), taskIndex);
		undoLogic.pushInverseModifyCommand(inverseCommand);
		uiLogic.setActiveTask(currTask);

		String result = String.format(StreamConstants.LogMessage.MODIFY,
				taskName);
		return result;
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
	private String executeUntag(Integer taskIndex, String content)
			throws StreamModificationException {
		String[] tags = content.split(" ");
		String taskName = getTaskNumber(taskIndex);
		StreamTask task = crdLogic.getTask(taskName);
		ArrayList<String> processedTags = modLogic.removeTags(task, tags);
		undoLogic.pushInverseUntagCommand(taskIndex, processedTags);
		uiLogic.setActiveTask(task);
		return logRemovedTags(taskName, processedTags);
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
	private String executeTag(Integer taskIndex, String content)
			throws StreamModificationException {
		String[] tags = content.split(" ");
		String taskName = getTaskNumber(taskIndex);
		StreamTask task = crdLogic.getTask(taskName);
		ArrayList<String> processedTags = modLogic.addTags(task, tags);
		undoLogic.pushInverseAddTagCommand(taskIndex, processedTags);
		uiLogic.setActiveTask(task);
		return logAddedTags(taskName, processedTags);
	}

	/**
	 * Reverts ordering after being sorted.
	 * 
	 * @return <strong>String</strong> - the log message
	 */
	private String executeUnsort() {
		ArrayList<String> order = orderLogic.pop();
		orderLogic.setOrdering(order);
		undoLogic.pushPlaceholderInput();
		return StreamConstants.LogMessage.UNSORT;
	}

	/**
	 * Recovers deleted task from the archive.
	 */
	private String executeRecover(Integer noOfTasksToRecover) {
		undoLogic.pushPlaceholderInput();

		for (int i = 0; i < noOfTasksToRecover; i++) {
			StreamTask task = crdLogic.pop();
			crdLogic.recoverTask(task);
		}
		orderLogic.setOrdering(orderLogic.pop());

		String result = String.format(StreamConstants.LogMessage.RECOVER,
				noOfTasksToRecover);
		return result;
	}

	/**
	 * Execute the undo operation for the last user action
	 * 
	 * @throws StreamParserException
	 * @throws StreamIOException
	 * @throws StreamModificationException
	 */
	private String executeUndo() throws StreamModificationException,
			StreamIOException, StreamParserException {
		String result;
		if (!undoLogic.hasInverseInput()) {
			result = StreamConstants.LogMessage.UNDO_FAIL;
		} else {
			String undoneInput = undoLogic.pop();
			result = StreamConstants.LogMessage.UNDO_SUCCESS;
			logDebug(StreamUtil.showAsTerminalInput(undoneInput));
			execute(undoneInput);

			/*
			 * VERY IMPORTANT because almost all inputs will add its counterpart
			 * to the inputStack. If not popped, the undo process will be
			 * trapped between just two processes.
			 */
			undoLogic.pop();
		}
		return result;
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
	private String executeMark(Integer taskIndex, MarkType markType)
			throws StreamModificationException {
		String taskName = getTaskNumber(taskIndex);
		StreamTask task = crdLogic.getTask(taskName);

		boolean wasDone = task.isDone();
		String result = modLogic.mark(task, markType);
		undoLogic.pushInverseSetDoneCommand(wasDone, taskIndex);

		uiLogic.setActiveTask(task);
		return result;
	}

	private String executeDue(Integer taskIndex, Calendar content)
			throws StreamModificationException {
		String taskName = getTaskNumber(taskIndex);
		StreamTask task = crdLogic.getTask(taskName);
		Calendar deadline = task.getDeadline();
		Calendar startTime = task.getStartTime();

		String result = processDueDate(taskIndex, content, task, deadline,
				startTime);

		uiLogic.setActiveTask(task);
		return result;
	}

	//@author A0119401U
	private String processDueDate(int taskIndex, Calendar newDeadline,
			StreamTask task, Calendar deadline, Calendar startTime) {
		if (modLogic.isValidDeadline(newDeadline, startTime)) {
			undoLogic.pushInverseDueCommand(taskIndex, deadline);
			return modLogic.setDeadline(task, newDeadline);
		} else {
			return StreamConstants.ExceptionMessage.ERR_DEADLINE_BEFORE_STARTTIME;
		}
	}

	private String executeStartTime(Integer taskIndex, Calendar content)
			throws StreamModificationException {
		String taskName = getTaskNumber(taskIndex);
		StreamTask task = crdLogic.getTask(taskName);
		Calendar startTime = task.getStartTime();
		Calendar deadline = task.getDeadline();

		String result = processStartTime(taskIndex, content, task, startTime,
				deadline);

		uiLogic.setActiveTask(task);
		return result;
	}

	private String processStartTime(int taskIndex, Calendar newStartTime,
			StreamTask currentTask, Calendar currentStartTime, Calendar deadline) {
		if (modLogic.isValidStartTime(deadline, newStartTime)) {
			undoLogic.pushInverseStartCommand(taskIndex, currentStartTime);
			return modLogic.setStartTime(currentTask, newStartTime);
		} else {
			return StreamConstants.ExceptionMessage.ERR_STARTTIME_AFTER_DEADLINE;
		}
	}

	//@author A0096529N
	// updated by A0119401U
	private String executeSort(String content) {
		ArrayList<String> oldOrdering = getTaskList();
		undoLogic.pushInverseSortCommand(oldOrdering);
		orderLogic.push(oldOrdering);

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

		result = orderLogic.sort(getStreamTaskList(), type, descending);
		return result;
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
	private String executeSearch(String content) {
		assertNotNull(content);
		ArrayList<Integer> searchResult = crdLogic.findTasks(content);
		refreshUI(searchResult, true, true);

		String result = String.format(StreamConstants.LogMessage.SEARCH,
				content, searchResult.size());
		return result;
	}

	//@author A0093874N
	private String executeFilter(String content) {
		assertNotNull(content);
		ArrayList<Integer> filterResult = crdLogic.filterTasks(content);
		refreshUI(filterResult, true, true);

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
		return result;
	}

	//@author A0118007R
	private void addTaskWithParams(String taskName,
			ArrayList<String> modifyParams) throws StreamModificationException {
		crdLogic.addTask(taskName);
		assert (crdLogic.hasTask(taskName)) : StreamConstants.Assertion.NOT_ADDED;
		int noOfTasks = getNumberOfTasks();
		undoLogic.pushInverseAddCommand(noOfTasks);
		processParameterAddition(taskName, modifyParams, noOfTasks);
	}

	private void processParameterAddition(String taskName,
			ArrayList<String> modifyParams, int index)
			throws StreamModificationException {
		StreamTask task = crdLogic.getTask(taskName);
		if (modifyParams.size() > 0) {
			modLogic.modifyTask(task, modifyParams, index);
		}
	}

	private String logAddedTags(String taskName, ArrayList<String> tagsAdded) {
		if (!tagsAdded.isEmpty()) {
			return String.format(StreamConstants.LogMessage.TAGS_ADDED,
					taskName, StreamUtil.listDownArrayContent(tagsAdded, ", "));
		} else {
			return StreamConstants.LogMessage.NO_TAGS_ADDED;
		}
	}

	private String logRemovedTags(String taskName, ArrayList<String> tagsRemoved) {
		if (!tagsRemoved.isEmpty()) {
			return String.format(StreamConstants.LogMessage.TAGS_REMOVED,
					taskName,
					StreamUtil.listDownArrayContent(tagsRemoved, ", "));
		} else {
			return StreamConstants.LogMessage.NO_TAGS_REMOVED;
		}
	}

	public void refreshUI() {
		uiLogic.refreshUI(getIndices(), getStreamTaskList(getIndices()), false,
				false);
	}

	public void refreshUI(ArrayList<Integer> indices, Boolean isReset,
			Boolean isSearching) {
		uiLogic.refreshUI(indices, getStreamTaskList(indices), isReset,
				isSearching);
	}

}