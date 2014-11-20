package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import logger.Loggable;
import model.StreamObject;
import model.StreamTask;
import parser.StreamParser;
import parser.FilterParser.FilterType;
import parser.RankParser.RankType;
import util.StreamConstants;

import com.mdimension.jchronic.Chronic;

public class SearcherLogic extends Loggable {
	
	private StreamObject streamObject;
	
	public SearcherLogic(StreamObject stobj) {
		streamObject = stobj;
	}

	// @author A0096529N
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

	@Override
	public String getComponentName() {
		return "SEARCHERLOGIC";
	}
	
}
