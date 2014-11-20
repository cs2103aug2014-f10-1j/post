package logic;

import java.util.Stack;

import exception.StreamModificationException;
import util.StreamConstants;
import logger.Loggable;
import model.StreamObject;
import model.StreamTask;

public class CRDLogic extends Loggable implements StackLogic {

	private StreamObject streamObject;
	private Stack<StreamTask> deletedTasks;

	//@author A0096529N

	private CRDLogic(StreamObject stobj) {
		streamObject = stobj;
		deletedTasks = new Stack<StreamTask>();
	}

	public static CRDLogic init(StreamObject stobj) {
		return new CRDLogic(stobj);
	}

	@Override
	public void push(Object obj) {
		StreamTask deletedTask = (StreamTask) obj;
		assert (deletedTask != null) : StreamConstants.Assertion.NULL_INVERSE_TASK;
		deletedTasks.push(deletedTask);
		logDebug(String.format(StreamConstants.LogMessage.PUSH_INVERSE_TASK,
				deletedTask.getTaskName()));
	}

	@Override
	public StreamTask pop() {
		StreamTask deletedTask = deletedTasks.pop();
		logDebug(String.format(StreamConstants.LogMessage.POP_INVERSE_TASK,
				deletedTask.getTaskName()));
		return deletedTask;
	}

	@Override
	public String getComponentName() {
		return "DELETELOGIC";
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
		if (streamObject.containsKey(taskName)) {
			streamObject.remove(taskName);
		} else {
			throw new StreamModificationException(String.format(
					StreamConstants.ExceptionMessage.ERR_TASK_DOES_NOT_EXIST,
					taskName));
		}
	}

	/**
	 * Clears the storage of all tasks.
	 */
	public void clear() {
		streamObject.clear();
		logDebug(StreamConstants.LogMessage.CLEARED_TASKS);
	}

	public void updateTaskName(String oldName, String newName, StreamTask task,
			int index) {
		streamObject.remove(oldName);
		streamObject.put(newName, task, index);
	}

	//@author A0118007R
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

	public Integer getIndex(String taskName) {
		return streamObject.indexOf(taskName);
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

}
