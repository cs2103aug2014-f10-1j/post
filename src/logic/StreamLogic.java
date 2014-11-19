package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.mdimension.jchronic.Chronic;

import parser.StreamParser;
import parser.FilterParser.FilterType;
import parser.RankParser.RankType;
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

//@author A0118007R
public class StreamLogic extends BaseLogic {

	private StreamObject streamObject;
	private TaskLogic taskLogic = TaskLogic.init();

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
		return logic;
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
	 * Sets the ordering of a task list.
	 * 
	 * @param anotherTaskList
	 */
	public void setOrdering(ArrayList<String> anotherTaskList) {
		assert (StreamUtil.listEqual(streamObject.getTaskList(),
				anotherTaskList)) : StreamConstants.Assertion.NOT_EQUAL;
		streamObject.setTaskList(anotherTaskList);
		logDebug(String.format(StreamConstants.LogMessage.REORDER_TASKS,
				Arrays.toString(anotherTaskList.toArray())));
	}

	//@author A0096529N
	/**
	 * Sets the ordering with tasks.
	 * 
	 * @param anotherTaskList
	 */
	public void setOrderingWithTasks(List<StreamTask> anotherTaskList) {

		ArrayList<String> orderList = new ArrayList<String>();
		for (StreamTask task : anotherTaskList) {
			orderList.add(task.getTaskName());
		}
		setOrdering(orderList);
	}

	/**
	 * Sorts by task name, lexicographically.
	 * 
	 * @param descending
	 *            true to reverse the order
	 * @return result of the sort
	 */
	public String sortAlpha(final boolean descending) {
		sort(new Comparator<StreamTask>() {
			@Override
			public int compare(StreamTask o1, StreamTask o2) {
				return descending ? compareName(o1, o2) : compareName(o2, o1);
			}
		});
		return "Sort by alphabetical order, "
				+ (descending ? "descending." : "ascending.");
	}

	/**
	 * Sorts by start time, earliest first
	 * 
	 * @param descending
	 *            true to reverse the order
	 * @return result of the sort
	 */
	public String sortStartTime(final boolean descending) {
		sort(new Comparator<StreamTask>() {
			@Override
			public int compare(StreamTask o1, StreamTask o2) {
				return descending ? compareStartTime(o1, o2, true)
						: compareStartTime(o2, o1, false);
			}
		});
		return "Sort by start time "
				+ (descending ? "descending." : "ascending.");
	}

	/**
	 * Sorts by deadline, earliest first
	 * 
	 * @param descending
	 *            true to reverse the order
	 * @return result of the sort
	 */
	public String sortDeadline(final boolean descending) {
		sort(new Comparator<StreamTask>() {
			@Override
			public int compare(StreamTask o1, StreamTask o2) {
				return descending ? compareDeadline(o1, o2, true)
						: compareDeadline(o2, o1, false);
			}
		});
		return "Sort by deadline "
				+ (descending ? "descending." : "ascending.");
	}

	/**
	 * Sorts based on importance.
	 * 
	 * <p>
	 * Sort algorithm
	 * </p>
	 * <ul>
	 * <li>Level 1: not done first</li>
	 * <li>Level 2: overdue first (only applicable to not-done tasks)</li>
	 * <li>Level 3: rank highest first</li>
	 * <li>Level 4: deadline earliest first</li>
	 * <li>Level 5: starttime earliest first</li>
	 * <li>Level 6: task name alphanumeric</li>
	 * </ul>
	 * 
	 * @param descending
	 *            true to reverse the order
	 * @return result of the sort
	 */
	public String sortImportance(final boolean descending) {
		sort(new Comparator<StreamTask>() {
			@Override
			public int compare(StreamTask o1, StreamTask o2) {
				int comparison = descending ? compareDone(o1, o2)
						: compareDone(o2, o1);
				if (comparison == 0 && !o1.isDone()
						&& o1.isOverdue() != o2.isOverdue()) {
					if (descending) {
						comparison = o2.isOverdue() ? 1 : -1;
					} else {
						comparison = o1.isOverdue() ? 1 : -1;
					}
				}
				if (comparison == 0) {
					comparison = descending ? compareRank(o1, o2)
							: compareRank(o2, o1);
				}
				if (comparison == 0) {
					comparison = descending ? compareDeadline(o2, o1, false)
							: compareDeadline(o1, o2, false);
				}
				if (comparison == 0) {
					comparison = descending ? compareStartTime(o2, o1, false)
							: compareStartTime(o1, o2, false);
				}
				if (comparison == 0) {
					return descending ? compareName(o1, o2) : compareName(o2,
							o1);
				}
				return comparison;
			}
		});
		return "Sort by importance "
				+ (descending ? "descending." : "ascending.");
	}

	//@author A0119401U
	/**
	 * Sorts the tasks based on the time given. First we sort by start time, and
	 * then sort by deadline.
	 * 
	 * @param descending
	 * @return 
	 */
	// Sort the task based on the time given, if start time is known, then
	// sort based on start time, if not, then sort based on deadline
	public String sortTime(final boolean descending) {
		sort(new Comparator<StreamTask>() {
			@Override
			public int compare(StreamTask o1, StreamTask o2) {
				if (o1.getStartTime() == null && o1.getDeadline() == null
						&& o2.getStartTime() == null
						&& o2.getDeadline() == null) {
					return 0;
				} else if (o1.getStartTime() == null
						&& o1.getDeadline() == null) {
					return 1;
				} else if (o2.getStartTime() == null
						&& o2.getDeadline() == null) {
					return -1;
				} else if (o1.getStartTime() == null
						&& o2.getStartTime() == null) {
					return descending ? o2.getDeadline().compareTo(
							o1.getDeadline()) : o1.getDeadline().compareTo(
							o2.getDeadline());
				} else if (o1.getStartTime() == null) {
					return descending ? o2.getStartTime().compareTo(
							o1.getDeadline()) : o1.getDeadline().compareTo(
							o2.getStartTime());
				} else if (o2.getStartTime() == null) {
					return descending ? o2.getDeadline().compareTo(
							o1.getStartTime()) : o1.getStartTime().compareTo(
							o2.getDeadline());
				} else {
					return descending ? o2.getStartTime().compareTo(
							o1.getStartTime()) : o1.getStartTime().compareTo(
							o2.getStartTime());
				}
			}
		});
		return "Sort by time " + (descending ? "descending." : "ascending.");
	}

	//@author A0096529N
	/**
	 * Sorts tasks based on given comparator.
	 * 
	 * @param comparator
	 *            for sorting tasks
	 */
	private void sort(Comparator<StreamTask> comparator) {
		assert (comparator != null);
		List<StreamTask> tempList = getStreamTaskList();
		Collections.sort(tempList, comparator);
		setOrderingWithTasks(tempList);
	}

	/**
	 * Compare the ranks of two tasks.
	 * <p>
	 * Values:
	 * </p>
	 * <ul>
	 * <li>HI: 2</li>
	 * <li>MED: 1</li>
	 * <li>LO: 0</li>
	 * <li>NULL: -1</li>
	 * </ul>
	 * 
	 * @param task1
	 *            the first task to compare
	 * @param task2
	 *            the second task to be compared to
	 * @return 0 if tasks' ranks are equal, or the rank of task2 - rank of
	 *         task1.
	 */
	private int compareRank(StreamTask task1, StreamTask task2) {
		return valueRank(task2.getRank()) - valueRank(task1.getRank());
	}

	private int compareDone(StreamTask task1, StreamTask task2) {
		if (task1.isDone() == task2.isDone()) {
			return 0;
		} else if (task1.isDone() && !task2.isDone()) {
			return 1;
		} else {
			return -1;
		}
	}

	private int compareDeadline(StreamTask task1, StreamTask task2,
			boolean reverse) {
		if (task1.getDeadline() == null && task2.getDeadline() == null) {
			return 0;
		} else if (task1.getDeadline() == null) {
			return reverse ? 1 : -1;
		} else if (task2.getDeadline() == null) {
			return reverse ? -1 : 1;
		} else {
			return task2.getDeadline().compareTo(task1.getDeadline());
		}
	}

	private int compareName(StreamTask task1, StreamTask task2) {
		return task2.getTaskName().compareTo(task1.getTaskName());
	}

	private int compareStartTime(StreamTask task1, StreamTask task2,
			boolean reverse) {
		if (task1.getStartTime() == null && task2.getStartTime() == null) {
			return 0;
		} else if (task1.getStartTime() == null) {
			return reverse ? 1 : -1;
		} else if (task2.getStartTime() == null) {
			return reverse ? -1 : 1;
		} else {
			return task2.getStartTime().compareTo(task1.getStartTime());
		}
	}

	private int valueRank(String rank) {
		switch (StreamParser.rp.parse(rank)) {
			case HI:
				return 2;
			case MED:
				return 1;
			case LO:
				return 0;
			default:
				return -1;
		}
	}

	/**
	 * Clears the storage of all tasks.
	 */
	public void clear() {
		streamObject.clear();
		logDebug(StreamConstants.LogMessage.CLEARED_TASKS);
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

	//@author A0096529N
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

	//@author A0096529N
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
	protected String getLoggerComponentName() {
		return StreamConstants.ComponentTag.STREAMLOGIC;
	}

}