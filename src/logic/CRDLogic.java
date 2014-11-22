package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Stack;

import parser.StreamParser;
import parser.FilterParser.FilterType;
import parser.RankParser.RankType;

import exception.StreamRetrievalException;
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

	private StreamObject stobj;
	private Stack<StreamTask> taskStack;

	//@author A0096529N

	public static CRDLogic init(StreamObject stobj) {
		CRDLogic crdLogic = new CRDLogic();
		crdLogic.stobj = stobj;
		crdLogic.taskStack = new Stack<StreamTask>();
		return crdLogic;
	}

	@Override
	public void push(Object obj) {
		StreamTask deletedTask = (StreamTask) obj;
		assert (deletedTask != null) : StreamConstants.Assertion.NULL_INVERSE_TASK;
		taskStack.push(deletedTask);
		logDebug(String.format(StreamConstants.LogMessage.PUSH_INVERSE_TASK,
				deletedTask.getTaskName()));
	}

	@Override
	public StreamTask pop() {
		StreamTask deletedTask = taskStack.pop();
		logDebug(String.format(StreamConstants.LogMessage.POP_INVERSE_TASK,
				deletedTask.getTaskName()));
		return deletedTask;
	}

	@Override
	public String getComponentName() {
		return "CRDLOGIC";
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
	StreamTask addTask(String newTaskName) throws StreamModificationException {
		if (hasTask(newTaskName)) {
			logDebug(String.format(
					StreamConstants.LogMessage.ADD_DUPLICATE_TASK, newTaskName));
			throw new StreamModificationException(String.format(
					StreamConstants.ExceptionMessage.ERR_TASK_ALREADY_EXISTS,
					newTaskName));
		} else {
			stobj.put(newTaskName, new StreamTask(newTaskName));
			logDebug(String.format(StreamConstants.LogMessage.ADDED_TASK,
					newTaskName));
			try {
				return getTask(newTaskName);
			} catch (StreamRetrievalException wonthappen) {
				return null;
			}
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
	 */
	void deleteTask(String taskName) {
		assert (hasTask(taskName)) : "";
		stobj.remove(taskName);
	}

	/**
	 * Clears the storage of all tasks.
	 */
	void clear() {
		stobj.clear();
		logDebug(StreamConstants.LogMessage.CLEARED_TASKS);
	}

	void updateTaskName(String oldName, String newName, StreamTask task,
			int index) {
		stobj.remove(oldName);
		stobj.put(newName, task, index);
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
	 */
	public StreamTask getTask(String taskName) throws StreamRetrievalException {
		if (hasTask(taskName)) {
			return stobj.get(taskName);
		} else {
			throw new StreamRetrievalException(String.format(
					StreamConstants.ExceptionMessage.ERR_TASK_DOES_NOT_EXIST,
					taskName));
		}
	}

	/**
	 * Retrieves the task name by index
	 * 
	 * <p>
	 * Precondition: index is a valid index
	 * </p>
	 * 
	 * @param index
	 *            the index of the task
	 * @throws StreamRetrievalException
	 */
	StreamTask getTask(int index) throws StreamRetrievalException {
		try {
			return getTask(stobj.getTaskList().get(index - 1));
		} catch (IndexOutOfBoundsException e) {
			throw new StreamRetrievalException("Task number " + index
					+ " does not exist");
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
		return stobj.containsKey(taskName);
	}

	int getIndex(String taskName) {
		return stobj.indexOf(taskName);
	}

	//@author A0093874N
	/**
	 * Adds the given task back into storage
	 * 
	 * @param task
	 *            to be added
	 */
	void addTask(StreamTask task) {
		stobj.put(task.getTaskName(), task);
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
	ArrayList<Integer> findTasks(String keyphrase) {
		// Split key phrase into keywords
		String[] keywords = null;
		if (keyphrase.contains(" ")) {
			keywords = keyphrase.split(" ");
		} else {
			keywords = new String[] { keyphrase };
		}

		ArrayList<Integer> tasks = new ArrayList<Integer>();
		for (int i = 0; i < stobj.size(); i++) {
			StreamTask task = stobj.get(stobj.get(i));

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
	ArrayList<Integer> filterTasks(String criteria) {
		ArrayList<Integer> tasks = new ArrayList<Integer>();
		FilterType type = StreamParser.fp.parse(criteria);
		String[] contents;
		Calendar dueDate;
		for (int i = 1; i <= stobj.size(); i++) {
			StreamTask task = stobj.get(stobj.get(i - 1));
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
					dueDate = StreamParser.tp.parse(contents[2]);
					if (task.getStartTime() != null
							&& task.getStartTime().before(dueDate)) {
						tasks.add(i);
					}
					break;
				case STARTAFT:
					contents = criteria.split(" ", 3);
					dueDate = StreamParser.tp.parse(contents[2]);
					if (task.getStartTime() != null
							&& task.getStartTime().after(dueDate)) {
						tasks.add(i);
					}
					break;
				case DUEBEF:
					contents = criteria.split(" ", 3);
					dueDate = StreamParser.tp.parse(contents[2]);
					if (task.getDeadline() != null
							&& task.getDeadline().before(dueDate)) {
						tasks.add(i);
					}
					break;
				case DUEAFT:
					contents = criteria.split(" ", 3);
					dueDate = StreamParser.tp.parse(contents[2]);
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
