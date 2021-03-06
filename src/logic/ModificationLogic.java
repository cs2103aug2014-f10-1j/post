package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import exception.StreamModificationException;
import exception.StreamRestriction;
import parser.StreamParser;
import parser.MarkParser.MarkType;
import parser.RankParser.RankType;
import logger.Loggable;
import model.StreamTask;
import util.StreamConstants;
import util.StreamUtil;

//@author A0118007R

/**
 * Executes task parameter modification processes. This component supports
 * multiple concurrent parameter modifications.
 */
public class ModificationLogic extends Loggable {

	private CRDLogic crdLogic;

	private static final String[] MODIFICATION_ATTRIBUTES = { "-name", "-desc",
			"-start", "-from", "-due", "-by", "-end", "-tag", "-untag",
			"-settags", "-rank", "-mark", "-to" };

	public static ModificationLogic init(CRDLogic crdLogic) {
		ModificationLogic modLogic = new ModificationLogic();
		modLogic.crdLogic = crdLogic;
		return modLogic;
	}

	@Override
	public String getComponentName() {
		return "MODIFICATIONLOGIC";
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
	String setName(StreamTask task, String newTaskName)
			throws StreamModificationException {
		assert (newTaskName != null) : StreamConstants.Assertion.NULL_INPUT;
		String taskName = task.getTaskName();
		int index = crdLogic.getIndex(taskName);
		if (!taskName.equals(newTaskName)) {
			if (crdLogic.hasTask(newTaskName)) {
				logDebug(String.format(
						StreamConstants.LogMessage.UPDATE_TASK_NAME_DUPLICATE,
						newTaskName));
				throw new StreamModificationException(
						String.format(
								StreamConstants.ExceptionMessage.ERR_NEW_TASK_NAME_NOT_AVAILABLE,
								newTaskName));
			}
		}
		task.setTaskName(newTaskName);
		crdLogic.updateTaskName(taskName, newTaskName, task, index);
		String result = String.format(StreamConstants.LogMessage.NAME, taskName,
				newTaskName);
		logDebug(result);
		return result;
	}

	String setDescription(StreamTask task, String contents) {
		String result;
		if (contents.equals("null")) {
			task.setDescription(null);
			result = String.format(StreamConstants.LogMessage.DESC_REMOVED,
					task.getTaskName());
		} else {
			task.setDescription(contents);
			result = String.format(StreamConstants.LogMessage.DESC,
					task.getTaskName(), contents);
		}
		logDebug(result);
		return result;
	}

	// @author A0093874N
	/**
	 * Process the addition of tags to the task object.
	 * 
	 * @param task
	 * @param tags
	 * @return tagsAdded - the ArrayList consisting of added tags to the task
	 *         object
	 */
	ArrayList<String> addTags(StreamTask task, String... tags) {
		logDebug(String.format(StreamConstants.LogMessage.TAGS_TO_ADD,
				task.getTaskName(), Arrays.toString(tags)));
		ArrayList<String> tagsAdded = new ArrayList<String>();
		for (String tag : tags) {
			if (tag.contains(" ")) {
				addTags(task, tag.split(" "));
			} else {
				tag = tag.toUpperCase();
				if (!task.hasTag(tag)) {
					task.getTags().add(tag);
					tagsAdded.add(tag);
				}
			}
		}

		logDebug(String.format(StreamConstants.LogMessage.TAGS_ADDED,
				task.getTaskName(), Arrays.toString(tagsAdded.toArray())));
		Collections.sort(task.getTags());
		return tagsAdded;
	}

	/**
	 * Process the removal of tags to the task object.
	 * 
	 * @param task
	 * @param tags
	 * @return tagsRemoved - the ArrayList consisting of removed tags from the
	 *         task object
	 */
	ArrayList<String> removeTags(StreamTask task, String... tags) {
		logDebug(String.format(StreamConstants.LogMessage.TAGS_TO_REMOVE,
				task.getTaskName(), Arrays.toString(tags)));
		ArrayList<String> tagsRemoved = new ArrayList<String>();
		for (String tag : tags) {
			tag = tag.toUpperCase();
			if (task.hasTag(tag)) {
				task.getTags().remove(tag);
				tagsRemoved.add(tag);
			}
		}

		logDebug(String.format(StreamConstants.LogMessage.TAGS_REMOVED,
				task.getTaskName(), Arrays.toString(tags)));
		Collections.sort(task.getTags());
		return tagsRemoved;
	}

	/**
	 * Sets the deadline of a task to the task object
	 * 
	 * @param task
	 * @param calendar
	 * @return result - the string consisting of the final deadline assigned to
	 *         the task.
	 * @throws StreamModificationException
	 */
	String setDeadline(StreamTask task, Calendar calendar)
			throws StreamModificationException {
		String result = null;
		if (calendar == null) {
			task.setDeadline(null);
			result = String.format(StreamConstants.LogMessage.DUE_NEVER,
					task.getTaskName());
		} else if (!isValidDeadline(calendar, task.getStartTime())) {
			throw new StreamModificationException("Invalid deadline");
		} else {
			task.setDeadline(calendar);
			String parsedCalendar = StreamParser.tp.translate(calendar);
			result = String.format(StreamConstants.LogMessage.DUE,
					task.getTaskName(), parsedCalendar);
		}
		logDebug(result);
		return result;
	}

	// @author A0119401U
	/**
	 * Sets the start time of a task to the task object
	 * 
	 * @param task
	 * @param calendar
	 * @return result - the string consisting of the final start time assigned
	 *         to the task.
	 * @throws StreamModificationException
	 */
	String setStartTime(StreamTask task, Calendar calendar)
			throws StreamModificationException {
		String result = null;
		if (calendar == null) {
			task.setStartTime(null);
			result = String.format(
					StreamConstants.LogMessage.START_NOT_SPECIFIED,
					task.getTaskName());
		} else if (!isValidStartTime(task.getDeadline(), calendar)) {
			throw new StreamModificationException("Invalid start time");
		} else {
			task.setStartTime(calendar);
			String parsedCalendar = StreamParser.tp.translate(calendar);
			result = String.format(StreamConstants.LogMessage.START,
					task.getTaskName(), parsedCalendar);
		}
		logDebug(result);
		return result;
	}

	String mark(StreamTask task, MarkType markType)
			throws StreamRestriction {
		String result;
		switch (markType) {
			case DONE:
				task.markAsDone();
				result = String.format(StreamConstants.LogMessage.MARK,
						task.getTaskName(), "done");
				break;
			case NOT:
				task.markAsOngoing();
				result = String.format(StreamConstants.LogMessage.MARK,
						task.getTaskName(), "ongoing");
				break;
			case INACTIVE:
			case OVERDUE:
				throw new StreamRestriction(
						"Disallowed marking type: " + markType);
			default:
				// should not happen, but let's play safe
				result = "Unknown marking type: " + markType;
		}
		logDebug(result);
		return result;
	}

	String setRank(StreamTask task, String contents) {
		String result;
		String inputRank = contents.trim();
		RankType parsedRankType = StreamParser.rp.parse(inputRank);
		switch (parsedRankType) {
			case HI:
			case MED:
			case LO:
				String translatedRank = StreamParser.rp.translate(parsedRankType);
				task.setRank(translatedRank);
				result = String.format(StreamConstants.LogMessage.RANK,
						task.getTaskName(), translatedRank);
			default:
				result = "Rank parsing failed";
				// won't happen in single param modification, may happen in
				// multi-modify
		}
		logDebug(result);
		return result;
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
	void modifyTask(StreamTask task, List<String> modifyParams, int index)
			throws StreamModificationException {
		String attribute = modifyParams.get(0);
		String contents = "";
		for (int i = 1; i < modifyParams.size(); i++) {
			String s = modifyParams.get(i);
			if (isValidAttribute(s)) {
				// first content is guaranteed to be a valid parameter
				modifyParam(task, attribute, contents.trim(), index);
				attribute = s;
				contents = "";
			} else {
				contents = contents + s + " ";
			}
		}
		modifyParam(task, attribute, contents, index);
	}

	/**
	 * The logic behind task modification for multi-add and multi-modify
	 * commands.
	 * 
	 * The "-" symbol is added to avoid confusion with parameters of each
	 * keywords.
	 * 
	 * @param task
	 * @param attribute
	 * @param contents
	 */
	void modifyParam(StreamTask task, String attribute, String contents,
			int index) {
		contents = contents.trim();
		switch (attribute) {
			case "-name":
				try {
					setName(task, contents);
				} catch (Exception ignore) {

				}
				break;
			case "-desc":
				setDescription(task, contents);
				break;
			case "-due":
			case "-by":
			case "-to":
			case "-end":
				if (StreamParser.tp.isParseable(contents)) {
					Calendar deadline = StreamParser.tp.parse(contents);
					try {
						setDeadline(task, deadline);
					} catch (StreamModificationException ignore) {
						//
					}
				} // else don't do anything
				break;
			case "-start":
			case "-from":
				if (StreamParser.tp.isParseable(contents)) {
					Calendar start = StreamParser.tp.parse(contents);
					try {
						setStartTime(task, start);
					} catch (StreamModificationException ignore) {

					}
				} // else don't do anything
				break;
			case "-tag":
				addTags(task, contents.split(" "));
				break;
			case "-untag":
				removeTags(task, contents.split(" "));
				break;
			case "-settags":
				setTags(task, contents);
				break;
			case "-rank":
				setRank(task, contents);
				break;
			case "-mark":
				MarkType mt = StreamParser.mp.parse(contents.trim());
				try {
					mark(task, mt);
				} catch (StreamRestriction ignore) {
					
				}
				break;
		}
		logDebug(String.format(StreamConstants.LogMessage.NEW_MODIFICATION,
				task.getTaskName(), attribute, contents));
	}

	// @author A0096529N
	private void setTags(StreamTask task, String contents) {
		task.getTags().clear();
		if (!contents.trim().isEmpty()) {
			addTags(task, contents.split(" "));
		}
	}

	/**
	 * Checks whether the <i>deadline</i> entered is after <i>startTime</i>
	 * 
	 * @return <b>boolean</b> - indicates whether <i>deadline</i> is after
	 *         <i>startTime</i>
	 */
	private boolean isValidDeadline(Calendar deadline, Calendar startTime) {
		if (deadline == null || startTime == null) {
			return true;
		} else {
			return deadline.after(startTime);
		}
	}

	/**
	 * Checks whether the <i>startTime</i> entered is before <i>deadline</i>
	 * 
	 * @return <b>boolean</b> - indicates whether <i>startTime</i> is before
	 *         <i>deadline</i>
	 */
	private boolean isValidStartTime(Calendar deadline, Calendar startTime) {
		if (deadline == null || startTime == null) {
			return true;
		} else {
			return startTime.before(deadline);
		}
	}

	/**
	 * Checks whether <i>param</i> is a valid modifier attribute for Stream
	 * modify command.
	 * 
	 * @return <b>boolean</b> - indicates whether <i>param</i> is a valid
	 *         attribute
	 */
	boolean isValidAttribute(String param) {
		for (String s : MODIFICATION_ATTRIBUTES) {
			if (s.equals(param)) {
				return true;
			}
		}
		return false;
	}

	static boolean[] compareTask(StreamTask taskA, StreamTask taskB) {
		boolean[] ATTR_ARRAY = { false, false, false, false, false, false,
				false };
		int ATTR_POS_NAME = 0;
		int ATTR_POS_DESCRIPTION = 1;
		int ATTR_POS_STARTTIME = 2;
		int ATTR_POS_DEADLINE = 3;
		int ATTR_POS_RANK = 4;
		int ATTR_POS_STATUS = 5;
		int ATTR_POS_TAGS = 6;

		if (!taskA.getTaskName().equals(taskB.getTaskName())) {
			ATTR_ARRAY[ATTR_POS_NAME] = true;
		}
		if (!taskA.getDescription().equals(taskB.getDescription())) {
			ATTR_ARRAY[ATTR_POS_DESCRIPTION] = true;
		}
		if (!StreamUtil.calEqual(taskA.getStartTime(), taskB.getStartTime())) {
			ATTR_ARRAY[ATTR_POS_STARTTIME] = true;
		}
		if (!StreamUtil.calEqual(taskA.getDeadline(), taskB.getDeadline())) {
			ATTR_ARRAY[ATTR_POS_DEADLINE] = true;
		}
		if (!taskA.getRank().equals(taskB.getRank())) {
			ATTR_ARRAY[ATTR_POS_RANK] = true;
		}
		if (!taskA.isDone() ^ taskB.isDone()) {
			ATTR_ARRAY[ATTR_POS_STATUS] = true;
		}
		if (!StreamUtil.listEqual(taskA.getTags(), taskB.getTags())) {
			ATTR_ARRAY[ATTR_POS_TAGS] = true;
		}
		return ATTR_ARRAY;
	}
	
}
