package ui;

import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import parser.StreamParser;
import util.StreamUtil;
import model.StreamTask;

public class Displayer {

	private static final String TEXT_PAGE = "Page %1$s/%2$s";
	private static final String TEXT_INDEX = "#%1$s";
	private static final String TEXT_CONTENT = "<html><body width='400'>"
			+ "<p>Task name: %1$s</p><p>Status: %2$s</p><p>Timing: %3$s</p>"
			+ "<p>Description: %4$s</p><p>Tags: %5$s</p><p>Rank: %6$s</p>";
	private static final String TEXT_NO_DESC = "no description provided";
	private static final String TEXT_NO_TAG = "no tags added";
	private static final String TITLE_DETAILS = "Details for %1$s";

	public static String displayPageNumber(Integer pageShown, Integer totalPage) {
		return String.format(TEXT_PAGE, pageShown, totalPage);
	}

	public static String displayIndex(Integer ind) {
		return String.format(TEXT_INDEX, ind);
	}

	/**
	 * Displays task description to user.
	 * 
	 * @param desc
	 *            - the task description, <b>null</b> if not specified
	 * @return <b>String</b> - the task description
	 */
	public static String displayDescription(String desc) {
		if (desc == null) {
			return TEXT_NO_DESC;
		} else {
			return desc;
		}
	}

	/**
	 * Displays tags to user nicely.
	 * 
	 * @param tags
	 *            - array of tags
	 * @return <b>String</b> - the listed down tags
	 */
	public static String displayTags(ArrayList<String> tags) {
		if (tags.isEmpty()) {
			return TEXT_NO_TAG;
		} else {
			return StreamUtil.listDownArrayContent(tags, ", ");
		}
	}

	/**
	 * Displays the status of a task to user.
	 * 
	 * @param task
	 * @return <b>String</b> - the task status
	 */
	public static String displayStatus(StreamTask task) {
		return StreamParser.mp.translate(StreamParser.mp.parse(task));
	}

	/**
	 * Checks two calendars <i>startTime</i> and <i>endTime</i>, formats them
	 * when applicable, and present to user accordingly depending on the
	 * existence of each.
	 * 
	 * @return <b>String</b> - the properly formatted and presentable time
	 */
	public static String displayTime(Calendar startTime, Calendar endTime) {
		if (startTime == null && endTime == null) {
			return "no timing specified";
		} else if (startTime == null) {
			return "by " + StreamParser.tp.translate(endTime);
		} else if (endTime == null) {
			return "from " + StreamParser.tp.translate(startTime);
		} else {
			return "from " + StreamParser.tp.translate(startTime) + " to "
					+ StreamParser.tp.translate(endTime);
		}
	}

	public static void displayDetails(JFrame frame, StreamTask task) {
		JOptionPane.showMessageDialog(frame, String.format(TEXT_CONTENT,
				task.getTaskName(), displayStatus(task),
				displayTime(task.getStartTime(), task.getDeadline()),
				displayDescription(task.getDescription()),
				displayTags(task.getTags()), task.getRank()), String.format(
				TITLE_DETAILS, task.getTaskName()),
				JOptionPane.INFORMATION_MESSAGE);
	}

}