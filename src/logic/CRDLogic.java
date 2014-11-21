package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Stack;

import parser.StreamParser;
import parser.FilterParser.FilterType;
import parser.RankParser.RankType;

import com.mdimension.jchronic.Chronic;

import exception.StreamModificationException;
import util.StreamConstants;
import logger.Loggable;
import model.StreamObject;
import model.StreamTask;

//@author A0118007R
/**
 * Executes create, retrieve (including search/filter), and delete processes.
 * Deleted tasks are kept in a stack should they need to be recycled (e.g by
 * undoing a delete command).
 */
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
		return "CRDLOGIC";
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

	//@author A0093874N
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

	//@author A0096529N
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

	//@author A0093874N
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

}
