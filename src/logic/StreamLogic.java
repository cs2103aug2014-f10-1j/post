package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import parser.SortParser.SortType;
import logger.Loggable;
import model.StreamObject;
import model.StreamTask;
import util.StreamConstants;
import exception.StreamModificationException;

/**
 * Some documentation.
 * 
 * @version V0.5
 */

// @author A0118007R
public class StreamLogic extends Loggable {

	private StreamObject streamObject;
	public ModificationLogic modLogic;
	public UndoLogic undoLogic;
	public DeleteLogic delLogic;
	public OrderLogic orderLogic;
	public SearcherLogic searchLogic;

	private StreamLogic(StreamObject stobj) {
		this.streamObject = stobj;
		this.modLogic = ModificationLogic.init();
		this.undoLogic = UndoLogic.init();
		this.delLogic = DeleteLogic.init();
		this.orderLogic = OrderLogic.init(stobj);
		this.searchLogic = SearcherLogic.init(stobj);
	}

	/**
	 * Initializes StreamLogic.
	 * 
	 * @param streamObject
	 * @return logic, the instance of the StreamLogic class
	 */
	public static StreamLogic init(StreamObject streamObject) {
		return new StreamLogic(streamObject);
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

	//@author A0118007R

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
			if (modLogic.isValidAttribute(s)) {
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
			modLogic.modifyTask(task, attribute, contents);
		}
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