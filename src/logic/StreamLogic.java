package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.mdimension.jchronic.Chronic;

import parser.SortParser.SortType;
import parser.StreamParser;
import parser.FilterParser.FilterType;
import parser.RankParser.RankType;
import logger.Loggable;
import model.StreamObject;
import model.StreamTask;
import util.StreamConstants;
import util.StreamUtil;
import exception.StreamModificationException;

/**
 * Some documentation.
 * 
 * @version V0.5
 */

// @author A0118007R
public class StreamLogic extends Loggable {

	private StreamObject streamObject;
	private TaskLogic taskLogic = TaskLogic.init();

	@SuppressWarnings("unused")
	private CRDLogic crdLogic = new CRDLogic();
	@SuppressWarnings("unused")
	private ModificationLogic modLogic = new ModificationLogic();
	@SuppressWarnings("unused")
	private UndoLogic undoLogic = new UndoLogic();
	@SuppressWarnings("unused")
	private SearcherLogic searchLogic = new SearcherLogic();
	public OrderLogic orderLogic;

	private StreamLogic() {

	}

	/**
	 * Initializes StreamLogic.
	 * 
	 * @param streamObject
	 * @return logic, the instance of the StreamLogic class
	 */
	public static StreamLogic init(StreamObject streamObject) {
		StreamLogic logic = new StreamLogic();
		logic.streamObject = streamObject;
		logic.orderLogic = new OrderLogic(streamObject);
		return logic;
	}

	// @author A0093874N
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
	 * Clears the storage of all tasks.
	 */
	public void clear() {
		streamObject.clear();
		logDebug(StreamConstants.LogMessage.CLEARED_TASKS);
	}

	// @author A0093874N
	/**
	 * Adds a new task to StreamObject
	 * 
	 * <p>
	 * Precondition: newTaskName != null
	 * </p>
	 * 
	 * @param newTaskName
	 *            name of the new task
	 */
	public void addTask(String newTaskName) throws StreamModificationException {
		if (hasTask(newTaskName)) {
			logDebug(String.format(
					StreamConstants.LogMessage.ADD_DUPLICATE_TASK, newTaskName));
			throw new StreamModificationException(String.format(
					StreamConstants.ExceptionMessage.ERR_TASK_ALREADY_EXISTS,
					newTaskName));
		} else {
			streamObject.put(newTaskName, new StreamTask(newTaskName));
			logDebug(String.format(StreamConstants.LogMessage.ADDED_TASK,
					newTaskName));
		}
	}

	/**
	 * Adds the given task back into storage
	 * 
	 * @param task
	 *            to be added
	 */
	public void recoverTask(StreamTask task) {
		streamObject.put(task.getTaskName(), task);
		logDebug(String.format(StreamConstants.LogMessage.RECOVERED_TASK,
				task.getTaskName()));
	}

	/**
	 * Checks whether a specific task is already included in the tasks list.
	 * Only for testing.
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null
	 * </p>
	 * 
	 * @param taskName
	 *            - the task name
	 * @return <strong>Boolean</strong> - true if the
	 *         <strong>StreamTask</strong> <i>taskName</i> exists, false
	 *         otherwise.
	 */
	public Boolean hasTask(String taskName) {
		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		return streamObject.containsKey(taskName);
	}

	// @author A0118007R
	/**
	 * Gets a specific task
	 * 
	 * <p>
	 * Precondition: taskName != null
	 * </p>
	 * 
	 * @param taskName
	 *            name of task to be returned
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public StreamTask getTask(String taskName)
			throws StreamModificationException {
		if (hasTask(taskName)) {
			return streamObject.get(taskName);
		} else {
			throw new StreamModificationException(String.format(
					StreamConstants.ExceptionMessage.ERR_TASK_DOES_NOT_EXIST,
					taskName));
		}
	}

	/**
	 * Deletes a specific task
	 * 
	 * <p>
	 * Precondition: taskName != null
	 * </p>
	 * 
	 * @param taskName
	 *            name of task to be deleted
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public void deleteTask(String taskName) throws StreamModificationException {
		if (hasTask(taskName)) {
			streamObject.remove(taskName);
		} else {
			throw new StreamModificationException(String.format(
					StreamConstants.ExceptionMessage.ERR_TASK_DOES_NOT_EXIST,
					taskName));
		}
	}

	// @author A0096529N
	/**
	 * Change task name of the task
	 * 
	 * <p>
	 * Precondition: taskName, newName != null
	 * </p>
	 * 
	 * @param taskName
	 *            to be modified
	 * @param newTaskName
	 *            name to be set to the task
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found. Or when task with newTaskName is already present.
	 */
	public String updateTaskName(String taskName, String newTaskName)
			throws StreamModificationException {
		assert (taskName != null && newTaskName != null) : StreamConstants.Assertion.NULL_INPUT;
		StreamTask task = getTask(taskName);
		if (!taskName.equals(newTaskName)) {
			if (streamObject.containsKey(newTaskName)) {
				logDebug(String.format(
						StreamConstants.LogMessage.UPDATE_TASK_NAME_DUPLICATE,
						newTaskName));
				throw new StreamModificationException(
						String.format(
								StreamConstants.ExceptionMessage.ERR_NEW_TASK_NAME_NOT_AVAILABLE,
								newTaskName));
			}
		}
		int index = streamObject.indexOf(taskName);

		streamObject.remove(task.getTaskName());
		task.setTaskName(newTaskName);
		streamObject.put(newTaskName, task, index);
		// This section is contributed by A0093874N
		logDebug(String.format(StreamConstants.LogMessage.UPDATE_TASK_NAME,
				taskName, newTaskName));
		return String.format(StreamConstants.LogMessage.NAME, taskName,
				newTaskName);
	}

	// @author A0118007R
	/**
	 * Modifies the various specified parameters of a task.
	 * 
	 * <p>
	 * Precondition: head of the modifyParams list is a valid parameter
	 * </p>
	 * 
	 * @param taskName
	 *            to be modified
	 * @param modifyParams
	 *            various parameters that are going to be modified
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found.
	 */
	public void modifyTaskWithParams(String taskName, List<String> modifyParams)
			throws StreamModificationException {
		StreamTask task = getTask(taskName);
		String attribute = modifyParams.get(0);
		String contents = "";
		for (int i = 1; i < modifyParams.size(); i++) {
			String s = modifyParams.get(i);
			if (StreamUtil.isValidAttribute(s)) {
				// first content is guaranteed to be a valid parameter
				modifyTask(task, attribute, contents.trim());
				attribute = s;
				contents = "";
			} else {
				contents = contents + s + " ";
			}
		}
		modifyTask(task, attribute, contents);
	}

	// @author A0096529N
	/**
	 * Modify an attribute of a task
	 * 
	 * @param task
	 *            to modify
	 * @param attribute
	 *            to be modified
	 * @param contents
	 *            that contains the instruction to modify that attribute
	 * @return result of this operation
	 * @throws StreamModificationException
	 */
	public void modifyTask(StreamTask task, String attribute, String contents)
			throws StreamModificationException {
		if (attribute.equalsIgnoreCase("-name")) {
			// modify name need access to streamObject, special case
			updateTaskName(task.getTaskName(), contents);
		} else {
			taskLogic.modifyTask(task, attribute, contents);
		}
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
	 * @return tasks - a list of tasks containing the key phrase, empty list if
	 *         nothing matches
	 */
	public ArrayList<Integer> findTasks(String keyphrase) {
		// Split key phrase into keywords
		String[] keywords = null;
		if (keyphrase.contains(" ")) {
			keywords = keyphrase.split(" ");
		} else {
			keywords = new String[] { keyphrase };
		}

		ArrayList<Integer> tasks = new ArrayList<Integer>();
		for (int i = 0; i < streamObject.size(); i++) {
			StreamTask task = streamObject.get(streamObject.get(i));

			// check for matches between keywords and tags
			if (task.hasTag(keywords)) {
				tasks.add(i + 1);
				continue;
			}
			// improved by A0093874N: case-insensitive search
			// check if task description contains key phrase
			if (task.getDescription() != null
					&& task.getDescription().toLowerCase()
							.contains(keyphrase.toLowerCase())) {
				tasks.add(i + 1);
				continue;
			}
			// check if task name contains key phrase
			if (task.getTaskName().toLowerCase()
					.contains(keyphrase.toLowerCase())) {
				tasks.add(i + 1);
				continue;
			}
		}

		logDebug(String.format(StreamConstants.LogMessage.SEARCHED_TASKS,
				keyphrase, Arrays.toString(tasks.toArray())));
		return tasks;
	}

	// @author A0093874N
	/**
	 * Filter tasks by various categories
	 * 
	 * @param criteria
	 *            the filtering criteria
	 * 
	 */
	public ArrayList<Integer> filterTasks(String criteria) {
		ArrayList<Integer> tasks = new ArrayList<Integer>();
		FilterType type = StreamParser.fp.parse(criteria);
		String[] contents;
		Calendar dueDate;
		for (int i = 1; i <= streamObject.size(); i++) {
			StreamTask task = streamObject.get(streamObject.get(i - 1));
			switch (type) {
				case DONE:
					if (task.isDone()) {
						tasks.add(i);
					}
					break;
				case NOT:
					if (!task.isDone()) {
						tasks.add(i);
					}
					break;
				case HIRANK:
					if (StreamParser.rp.parse(task.getRank()) == RankType.HI) {
						tasks.add(i);
					}
					break;
				case MEDRANK:
					if (StreamParser.rp.parse(task.getRank()) == RankType.MED) {
						tasks.add(i);
					}
					break;
				case LORANK:
					if (StreamParser.rp.parse(task.getRank()) == RankType.LO) {
						tasks.add(i);
					}
					break;
				case STARTBEF:
					contents = criteria.split(" ", 3);
					dueDate = Chronic.parse(contents[2]).getBeginCalendar();
					if (task.getStartTime() != null
							&& task.getStartTime().before(dueDate)) {
						tasks.add(i);
					}
					break;
				case STARTAFT:
					contents = criteria.split(" ", 3);
					dueDate = Chronic.parse(contents[2]).getBeginCalendar();
					if (task.getStartTime() != null
							&& task.getStartTime().after(dueDate)) {
						tasks.add(i);
					}
					break;
				case DUEBEF:
					contents = criteria.split(" ", 3);
					dueDate = Chronic.parse(contents[2]).getBeginCalendar();
					if (task.getDeadline() != null
							&& task.getDeadline().before(dueDate)) {
						tasks.add(i);
					}
					break;
				case DUEAFT:
					contents = criteria.split(" ", 3);
					dueDate = Chronic.parse(contents[2]).getBeginCalendar();
					if (task.getDeadline() != null
							&& task.getDeadline().after(dueDate)) {
						tasks.add(i);
					}
					break;
				case NOTIMING:
					if (task.isFloatingTask()) {
						tasks.add(i);
					}
					break;
				case DEADLINED:
					if (task.isDeadlineTask()) {
						tasks.add(i);
					}
					break;
				case EVENT:
					if (task.isTimedTask()) {
						tasks.add(i);
					}
					break;
				case OVERDUE:
					if (task.isOverdue()) {
						tasks.add(i);
					}
					break;
				case INACTIVE:
					if (task.isInactive()) {
						tasks.add(i);
					}
					break;
				// case STARTON:
				// case DUEON:
				// TODO think on how to implement this
				default:
					// shouldn't happen, but in case it happens, pretend
					// that there is no filter
					tasks.add(i);
					break;
			}
		}
		logDebug(String.format(StreamConstants.LogMessage.FILTERED_TASKS,
				criteria, Arrays.toString(tasks.toArray())));
		return tasks;
	}

	/**
	 * Gets the number of tasks added.
	 * 
	 */
	public int getNumberOfTasks() {
		return streamObject.size();
	}

	// @author A0096529N
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

	// @author A0093874N

	public ArrayList<StreamTask> getStreamTaskList(ArrayList<Integer> indices) {
		ArrayList<StreamTask> tasks = new ArrayList<StreamTask>();
		HashMap<String, StreamTask> taskMap = streamObject.getTaskMap();
		ArrayList<String> taskList = streamObject.getTaskList();
		for (Integer index : indices) {
			tasks.add(taskMap.get(taskList.get(index - 1).toLowerCase()));
		}
		return tasks;
	}

	// @author generated
	@Override
	public String getComponentName() {
		return "STREAMLOGIC";
	}

	public String sort(SortType type, Boolean isDescending) {
		List<StreamTask> initialList = getStreamTaskList();
		String result = null;
		switch (type) {
			case ALPHA:
				result = orderLogic.sortAlpha(initialList, isDescending);
				break;
			case END:
				result = orderLogic.sortDeadline(initialList, isDescending);
				break;
			case START:
				result = orderLogic.sortStartTime(initialList, isDescending);
				break;
			case TIME:
				result = orderLogic.sortTime(initialList, isDescending);
				break;
			case IMPORTANCE:
				result = orderLogic.sortImportance(initialList, isDescending);
				break;
			default:
				// WILL NOT HAPPEN
				result = "Unknown sort category";
				break;
		}
		return result;
	}
}