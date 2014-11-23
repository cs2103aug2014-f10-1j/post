package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

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
import exception.StreamRetrievalException;
import exception.StreamModificationException;
import exception.StreamParserException;
import exception.StreamRestriction;
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

	private Stream st;
	private StreamObject stobj;
	private StreamParser stpar = StreamParser.init();

	public UndoLogic undoLogic = UndoLogic.init();
	public CRDLogic crdLogic;
	public ModificationLogic modLogic;
	public OrderLogic orderLogic;
	public UIUpdaterLogic uiLogic;

	private StreamLogic(Stream st, StreamUI stui, StreamObject stobj) {
		this.st = st;
		this.stobj = stobj;
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
	public static StreamLogic init(Stream st, StreamUI stui, StreamObject stobj) {
		return new StreamLogic(st, stui, stobj);
	}

	//@author generated
	@Override
	public String getComponentName() {
		return "STREAMLOGIC";
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
		for (int i = 0; i < stobj.size(); i++) {
			indices.add(i + 1);
		}
		return indices;
	}

	/**
	 * Gets the number of tasks added.
	 */
	public int getNumberOfTasks() {
		return stobj.size();
	}

	//@author A0118007R
	public String parseAndExecute(String input)
			throws StreamModificationException, StreamIOException,
			StreamParserException, StreamRetrievalException {
		StreamCommand cmd = stpar.parseCommand(input);
		CommandType command = cmd.getKey();
		Integer index = cmd.getIndex();
		Object content = cmd.getContent();
		String result;
		switch (command) {
			case ADD:
				result = executeAdd((String) content);
				break;

			case DEL:
				result = executeDelete(index);
				break;

			case DESC:
				result = executeDescribe(index, (String) content);
				break;

			case DUE:
				result = executeDue(index, (Calendar) content);
				break;

			case START:
				result = executeStartTime(index, (Calendar) content);
				break;

			case MODIFY:
				result = executeModify(index, (String) content);
				break;

			case NAME:
				result = executeName(index, (String) content);
				break;

			case RANK:
				result = executeRank(index, (String) content);
				break;

			case MARK:
				result = executeMark(index, (MarkType) content);
				break;

			case TAG:
				result = executeTag(index, (String) content);
				break;

			case UNTAG:
				result = executeUntag(index, (String) content);
				break;

			case FILTER:
				result = executeFilter((String) content);
				break;

			case CLRSRC:
				result = refreshUI(null);
				break;

			case SEARCH:
				result = executeSearch((String) content);
				break;

			case SORT:
				result = executeSort((String) content);
				break;

			case VIEW:
				result = executeView(index);
				break;

			case CLEAR:
				result = executeClear();
				break;

			case UNDO:
				result = executeUndo();
				break;

			case RECOVER:
				result = executeRecover(index);
				break;

			case DISMISS:
				result = executeDismiss(index);
				break;

			case UNSORT:
				result = executeUnsort();
				break;

			case FIRST:
				result = uiLogic.goToFirstPage();
				break;

			case PREV:
				result = uiLogic.goToPrevPage();
				break;

			case NEXT:
				result = uiLogic.goToNextPage();
				break;

			case LAST:
				result = uiLogic.goToLastPage();
				break;

			case PAGE:
				result = uiLogic.goToPage(index);
				break;

			case HELP:
				result = uiLogic.openHelpBox();
				break;

			case EXIT:
				st.exit();

			default: // WILL NOT HAPPEN
				result = null;
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
		StreamTask task = addTaskWithParams(taskName, modifyParams);
		refreshUI(task);

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
	 * @throws StreamRetrievalException
	 * @return <strong>String</strong> - the log message
	 */
	private String executeDelete(Integer taskIndex)
			throws StreamRetrievalException {
		StreamTask deletedTask = crdLogic.getTask(taskIndex);
		ArrayList<String> order = stobj.getTaskListCopy();
		String taskName = deletedTask.getTaskName();

		crdLogic.deleteTask(taskName);
		assertNoTask(taskName);

		crdLogic.push(deletedTask);
		orderLogic.push(order);
		undoLogic.pushInverseDeleteCommand(deletedTask, order);
		refreshUI(null);

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
	 * @throws StreamRetrievalException
	 */
	private String executeDismiss(Integer taskIndex)
			throws StreamRetrievalException {
		StreamTask task = crdLogic.getTask(taskIndex);
		String taskName = task.getTaskName();

		crdLogic.deleteTask(taskName);
		assertNoTask(taskName);
		undoLogic.pushPlaceholderInput();
		refreshUI(null);

		String result = String.format(StreamConstants.LogMessage.DELETE,
				taskName);
		return result;
	}

	/**
	 * Clears all tasks upon receiving the command "clear".
	 */
	private String executeClear() {
		orderLogic.push(stobj.getTaskListCopy());
		for (StreamTask task : stobj.getStreamTaskList()) {
			crdLogic.push(task);
		}
		undoLogic.pushInverseClearCommand(stobj.getTaskListCopy(),
				stobj.getStreamTaskList());
		crdLogic.clear();
		refreshUI(null);
		assert (getNumberOfTasks() == 0) : StreamConstants.Assertion.NOT_CLEARED;
		return StreamConstants.LogMessage.CLEAR;
	}

	//@author A0118007R
	/**
	 * Prints the task details.
	 * 
	 * @throws StreamRetrievalException
	 */
	private String executeView(Integer taskIndex)
			throws StreamRetrievalException {

		StreamTask task = crdLogic.getTask(taskIndex);
		uiLogic.displayDetails(task);
		refreshUI(task);

		// This section is contributed by A0093874N
		String result = String.format(StreamConstants.LogMessage.VIEW,
				task.getTaskName());
		return result;
	}

	/**
	 * Adds a description to a task.
	 * <p>
	 * Pre-condition: <i>task, index, description</i> not null
	 * </p>
	 * 
	 * @throws StreamRetrievalException
	 * @return <strong>String</strong> - the log message
	 */
	private String executeDescribe(Integer taskIndex, String description)
			throws StreamRetrievalException {
		StreamTask task = crdLogic.getTask(taskIndex);
		String oldDescription = task.getDescription();
		String result = modLogic.setDescription(task, description);
		refreshUI(task);

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
	 * @throws StreamRetrievalException
	 * @return <strong>String</strong> - the log message
	 */
	private String executeRank(Integer taskIndex, String taskRank)
			throws StreamRetrievalException {
		StreamTask task = crdLogic.getTask(taskIndex);
		String oldRank = task.getRank();
		String result = modLogic.setRank(task, taskRank);
		refreshUI(task);

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
	 * @throws StreamRetrievalException
	 * @return <strong>String</strong> - the log message
	 */
	private String executeName(Integer taskIndex, String newTaskName)
			throws StreamRetrievalException, StreamModificationException {
		StreamTask task = crdLogic.getTask(taskIndex);
		String oldTaskName = task.getTaskName();
		String result = modLogic.setName(task, newTaskName);
		refreshUI(task);
		undoLogic.pushInverseSetNameCommand(taskIndex, oldTaskName);
		return result;
	}

	/**
	 * Modifies various parameters of a task.
	 * <p>
	 * Pre-condition: <i>task, index, specified params</i> not null
	 * </p>
	 * 
	 * @throws StreamRetrievalException
	 * @return <strong>String</strong> - the log message
	 */
	private String executeModify(Integer taskIndex, String content)
			throws StreamRetrievalException, StreamModificationException {
		String[] contents = content.split(" ");
		StreamTask task = crdLogic.getTask(taskIndex);
		String taskName = task.getTaskName();

		String inverseCommand = undoLogic.prepareInverseModifyCommand(taskName,
				taskIndex, task);

		modLogic.modifyTask(task, Arrays.asList(contents), taskIndex);
		undoLogic.pushInverseModifyCommand(inverseCommand);
		refreshUI(task);

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
	 * @throws StreamRetrievalException
	 * @return <strong>String</strong> - the log message
	 */
	private String executeUntag(Integer taskIndex, String content)
			throws StreamRetrievalException {
		String[] tags = content.split(" ");
		StreamTask task = crdLogic.getTask(taskIndex);
		ArrayList<String> processedTags = modLogic.removeTags(task, tags);
		undoLogic.pushInverseUntagCommand(taskIndex, processedTags);
		refreshUI(task);
		return logRemovedTags(task.getTaskName(), processedTags);
	}

	/**
	 * Tag some tags that are specified in the input.
	 * <p>
	 * Pre-condition: <i>task, index, tags</i> not null
	 * </p>
	 * 
	 * @throws StreamRetrievalException
	 * @return <strong>String</strong> - the log message
	 */
	private String executeTag(Integer taskIndex, String content)
			throws StreamRetrievalException {
		String[] tags = content.split(" ");
		StreamTask task = crdLogic.getTask(taskIndex);
		ArrayList<String> processedTags = modLogic.addTags(task, tags);
		undoLogic.pushInverseAddTagCommand(taskIndex, processedTags);
		refreshUI(task);
		return logAddedTags(task.getTaskName(), processedTags);
	}

	/**
	 * Recovers deleted task from the archive.
	 */
	private String executeRecover(Integer noOfTasksToRecover) {
		undoLogic.pushPlaceholderInput();

		for (int i = 0; i < noOfTasksToRecover; i++) {
			StreamTask task = crdLogic.pop();
			crdLogic.addTask(task);
		}
		orderLogic.setOrdering(orderLogic.pop());
		refreshUI(null);

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
	 * @throws StreamRetrievalException
	 */
	private String executeUndo() throws StreamModificationException,
			StreamIOException, StreamParserException, StreamRetrievalException {
		String result;
		if (!undoLogic.hasInverseInput()) {
			result = StreamConstants.LogMessage.UNDO_FAIL;
		} else {
			String undoneInput = undoLogic.pop();
			result = StreamConstants.LogMessage.UNDO_SUCCESS;
			logDebug(StreamUtil.showAsTerminalInput(undoneInput));
			parseAndExecute(undoneInput);

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
	 * @throws StreamRetrievalException
	 */
	private String executeMark(Integer taskIndex, MarkType markType)
			throws StreamRetrievalException {
		StreamTask task = crdLogic.getTask(taskIndex);

		boolean wasDone = task.isDone();
		String result;
		try {
			result = modLogic.mark(task, markType);
		} catch (StreamRestriction e) {
			result = e.getMessage();
		}
		undoLogic.pushInverseSetDoneCommand(wasDone, taskIndex);

		refreshUI(task);
		return result;
	}

	//@author A0119401U
	private String executeDue(Integer taskIndex, Calendar content)
			throws StreamRetrievalException {
		StreamTask task = crdLogic.getTask(taskIndex);
		Calendar deadline = task.getDeadline();
		String result;
		try {
			result = modLogic.setDeadline(task, content);
			undoLogic.pushInverseDueCommand(taskIndex, deadline);
		} catch (StreamModificationException e) {
			result = StreamConstants.ExceptionMessage.ERR_DEADLINE_BEFORE_STARTTIME;
		}
		refreshUI(task);
		return result;
	}

	private String executeStartTime(Integer taskIndex, Calendar content)
			throws StreamRetrievalException {
		StreamTask task = crdLogic.getTask(taskIndex);
		Calendar startTime = task.getStartTime();
		String result;
		try {
			result = modLogic.setStartTime(task, content);
			undoLogic.pushInverseStartCommand(taskIndex, startTime);
		} catch (StreamModificationException e) {
			result = StreamConstants.ExceptionMessage.ERR_STARTTIME_AFTER_DEADLINE;
		}
		refreshUI(task);
		return result;
	}

	//@author A0096529N
	// updated by A0119401U
	private String executeSort(String content) {
		ArrayList<String> oldOrdering = stobj.getTaskListCopy();
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

		result = orderLogic.sort(stobj.getStreamTaskList(), type, descending);
		refreshUI(null);
		return result;
	}

	/**
	 * Reverts ordering after being sorted.
	 * 
	 * @return <strong>String</strong> - the log message
	 */
	private String executeUnsort() {
		ArrayList<String> order = orderLogic.pop();
		undoLogic.pushPlaceholderInput();
		orderLogic.setOrdering(order);
		refreshUI(null);
		return StreamConstants.LogMessage.UNSORT;
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
		refreshUiAfterSearch(searchResult);

		String result = String.format(StreamConstants.LogMessage.SEARCH,
				content, searchResult.size());
		return result;
	}

	//@author A0093874N
	private String executeFilter(String content) {
		assertNotNull(content);
		ArrayList<Integer> filterResult = crdLogic.filterTasks(content);
		refreshUiAfterSearch(filterResult);

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
	private StreamTask addTaskWithParams(String taskName,
			ArrayList<String> modifyParams) throws StreamModificationException {
		StreamTask task = crdLogic.addTask(taskName);
		assert (crdLogic.hasTask(taskName)) : StreamConstants.Assertion.NOT_ADDED;
		int noOfTasks = getNumberOfTasks();
		undoLogic.pushInverseAddCommand(noOfTasks);
		return processParameterAddition(task, modifyParams, noOfTasks);
	}

	private StreamTask processParameterAddition(StreamTask task,
			ArrayList<String> modifyParams, int index)
			throws StreamModificationException {
		if (modifyParams.size() > 0) {
			modLogic.modifyTask(task, modifyParams, index);
		}
		return task;
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

	public String refreshUI(StreamTask task) {
		if (task != null) {
			uiLogic.setActiveTask(task);
		}
		uiLogic.refreshUI(getIndices(), stobj.getStreamTaskList(getIndices()),
				false, false);
		return null;
	}

	private void refreshUiAfterSearch(ArrayList<Integer> indices) {
		uiLogic.refreshUI(indices, stobj.getStreamTaskList(indices), true, true);
	}

}
