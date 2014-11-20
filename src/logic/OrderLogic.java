package logic;

import java.util.Arrays;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import logger.Loggable;
import model.StreamObject;
import model.StreamTask;
import parser.StreamParser;
import util.StreamConstants;
import util.StreamUtil;

public class OrderLogic extends Loggable implements StackLogic {

	private Stack<ArrayList<String>> orderStack;
	private StreamObject streamObject;

	private OrderLogic(StreamObject stobj) {
		orderStack = new Stack<ArrayList<String>>();
		streamObject = stobj;
	}
	
	public static OrderLogic init(StreamObject stobj) {
		return new OrderLogic(stobj);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void push(Object obj) {
		ArrayList<String> newOrder = (ArrayList<String>) obj;
		assert (newOrder != null && !newOrder.isEmpty()) : StreamConstants.Assertion.EMPTY_INVERSE_ORDER;
		orderStack.push(newOrder);
		logDebug(String.format(StreamConstants.LogMessage.PUSH_ORDER,
				Arrays.toString(newOrder.toArray())));
	}

	@Override
	/**
	 * Pops the order on the top of the ordering stack
	 * 
	 * @return taskList List of taskNames in the order that was pushed
	 *         previously
	 */
	public ArrayList<String> pop() {
		ArrayList<String> order = orderStack.pop();
		logDebug(String.format(StreamConstants.LogMessage.POP_ORDER,
				Arrays.toString(order.toArray())));
		return order;
	}

	/**
	 * Sorts by task name, lexicographically.
	 * 
	 * @param isDescending
	 *            true to reverse the order
	 * @return result of the sort
	 */
	String sortAlpha(List<StreamTask> initList, final boolean isDescending) {
		sort(initList, new Comparator<StreamTask>() {
			@Override
			public int compare(StreamTask o1, StreamTask o2) {
				return isDescending ? compareName(o1, o2) : compareName(o2, o1);
			}
		});
		return "Sort by alphabetical order, "
				+ (isDescending ? "descending." : "ascending.");
	}

	/**
	 * Sorts by start time, earliest first
	 * 
	 * @param isDescending
	 *            true to reverse the order
	 * @return result of the sort
	 */
	String sortStartTime(List<StreamTask> initList, final boolean isDescending) {
		sort(initList, new Comparator<StreamTask>() {
			@Override
			public int compare(StreamTask o1, StreamTask o2) {
				return isDescending ? compareStartTime(o1, o2, true)
						: compareStartTime(o2, o1, false);
			}
		});
		return "Sort by start time "
				+ (isDescending ? "descending." : "ascending.");
	}

	/**
	 * Sorts by deadline, earliest first
	 * 
	 * @param isDescending
	 *            true to reverse the order
	 * @return result of the sort
	 */
	String sortDeadline(List<StreamTask> initList, final boolean isDescending) {
		sort(initList, new Comparator<StreamTask>() {
			@Override
			public int compare(StreamTask o1, StreamTask o2) {
				return isDescending ? compareDeadline(o1, o2, true)
						: compareDeadline(o2, o1, false);
			}
		});
		return "Sort by deadline "
				+ (isDescending ? "descending." : "ascending.");
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
	 * @param isDescending
	 *            true to reverse the order
	 * @return result of the sort
	 */
	String sortImportance(List<StreamTask> initList, final boolean isDescending) {
		sort(initList, new Comparator<StreamTask>() {
			@Override
			public int compare(StreamTask o1, StreamTask o2) {
				int comparison = isDescending ? compareDone(o1, o2)
						: compareDone(o2, o1);
				if (comparison == 0 && !o1.isDone()
						&& o1.isOverdue() != o2.isOverdue()) {
					if (isDescending) {
						comparison = o2.isOverdue() ? 1 : -1;
					} else {
						comparison = o1.isOverdue() ? 1 : -1;
					}
				}
				if (comparison == 0) {
					comparison = isDescending ? compareRank(o1, o2)
							: compareRank(o2, o1);
				}
				if (comparison == 0) {
					comparison = isDescending ? compareDeadline(o2, o1, false)
							: compareDeadline(o1, o2, false);
				}
				if (comparison == 0) {
					comparison = isDescending ? compareStartTime(o2, o1, false)
							: compareStartTime(o1, o2, false);
				}
				if (comparison == 0) {
					return isDescending ? compareName(o1, o2) : compareName(o2,
							o1);
				}
				return comparison;
			}
		});
		return "Sort by importance "
				+ (isDescending ? "descending." : "ascending.");
	}

	// @author A0119401U
	/**
	 * Sorts the tasks based on the time given. First we sort by start time, and
	 * then sort by deadline.
	 * 
	 * @param isDescending
	 * @return
	 */
	// Sort the task based on the time given, if start time is known, then
	// sort based on start time, if not, then sort based on deadline
	String sortTime(List<StreamTask> initList, final boolean isDescending) {
		sort(initList, new Comparator<StreamTask>() {
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
					return isDescending ? o2.getDeadline().compareTo(
							o1.getDeadline()) : o1.getDeadline().compareTo(
							o2.getDeadline());
				} else if (o1.getStartTime() == null) {
					return isDescending ? o2.getStartTime().compareTo(
							o1.getDeadline()) : o1.getDeadline().compareTo(
							o2.getStartTime());
				} else if (o2.getStartTime() == null) {
					return isDescending ? o2.getDeadline().compareTo(
							o1.getStartTime()) : o1.getStartTime().compareTo(
							o2.getDeadline());
				} else {
					return isDescending ? o2.getStartTime().compareTo(
							o1.getStartTime()) : o1.getStartTime().compareTo(
							o2.getStartTime());
				}
			}
		});
		return "Sort by time " + (isDescending ? "descending." : "ascending.");
	}

	// @author A0096529N
	/**
	 * Sorts tasks based on given comparator.
	 * 
	 * @param comparator
	 *            for sorting tasks
	 */
	private void sort(List<StreamTask> initialList,
			Comparator<StreamTask> comparator) {
		assert (comparator != null);
		Collections.sort(initialList, comparator);

		ArrayList<String> orderList = new ArrayList<String>();
		for (StreamTask task : initialList) {
			orderList.add(task.getTaskName());
		}
		setOrdering(orderList);
	}

	public void setOrdering(ArrayList<String> orderList) {
		assert (StreamUtil.listEqual(streamObject.getTaskList(), orderList)) : StreamConstants.Assertion.NOT_EQUAL;
		streamObject.setTaskList(orderList);
		logDebug(String.format(StreamConstants.LogMessage.REORDER_TASKS,
				Arrays.toString(orderList.toArray())));
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

	@Override
	public String getComponentName() {
		return "ORDERLOGIC";
	}

}