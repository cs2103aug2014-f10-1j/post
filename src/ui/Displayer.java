package ui;

import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import parser.StreamParser;
import parser.TimeParser;
import stream.Stream;
import util.StreamUtil;
import model.StreamTask;

//@author A0093874N
/**
 * A display handler to process attributes of a <b>StreamTask</b> to presentable
 * format, be it in written form or iconic form.
 */
public class Displayer {

	private static final String TEXT_PAGE = "Page %1$s/%2$s";
	private static final String TEXT_INDEX = "#%1$s";
	private static final String TEXT_CONTENT = "<html><body width='400'>"
			+ "<p>Task name: %1$s</p><p>Status: %2$s</p><p>Timing: %3$s</p>"
			+ "<p>Description: %4$s</p><p>Tags: %5$s</p><p>Rank: %6$s</p>";
	private static final String TEXT_NO_DESC = "no description provided";
	private static final String TEXT_NO_TAG = "no tags added";
	private static final String TITLE_DETAILS = "Details for %1$s";
	private static final String VIEW_HOUR = "%1$s:%2$s";

	static String displayPageNumber(Integer pageShown, Integer totalPage) {
		return String.format(TEXT_PAGE, pageShown, totalPage);
	}

	static String displayIndex(Integer ind) {
		return String.format(TEXT_INDEX, ind);
	}

	static String displayName(StreamTask task) {
		return task.getTaskName();
	}

	static String displayRank(StreamTask task) {
		return task.getRank();
	}

	/**
	 * Displays task description to user.
	 * 
	 * @param desc
	 *            - the task description, <b>null</b> if not specified
	 * @return <b>String</b> - the task description
	 */
	static String displayDescription(StreamTask task) {
		String desc = task.getDescription();
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
	static String displayTags(StreamTask task) {
		ArrayList<String> tags = task.getTags();
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
	static String displayStatus(StreamTask task) {
		return StreamParser.mp.translate(StreamParser.mp.parse(task));
	}

	/**
	 * Checks two calendars <i>startTime</i> and <i>endTime</i>, formats them
	 * when applicable, and present to user accordingly depending on the
	 * existence of each.
	 * 
	 * @return <b>String</b> - the properly formatted and presentable time
	 */
	static String displayTime(StreamTask task) {
		Calendar startTime = task.getStartTime();
		Calendar endTime = task.getDeadline();
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

	static String displayDetails(JFrame frame, StreamTask task) {
		JOptionPane.showMessageDialog(frame,
				String.format(TEXT_CONTENT, displayName(task),
						displayStatus(task), displayTime(task),
						displayDescription(task), displayTags(task),
						displayRank(task)), String.format(TITLE_DETAILS,
						displayName(task)), JOptionPane.INFORMATION_MESSAGE);
		return displayName(task);
	}

	static ImageIcon selectStatusIcon(StreamTask task) {
		if (task.isDone()) {
			return Stream.ICON_DONE;
		} else if (task.isOverdue()) {
			return Stream.ICON_OVERDUE;
		} else if (task.isInactive()) {
			return Stream.ICON_INACTIVE;
		} else {
			return Stream.ICON_NOT_DONE;
		}
	}

	static ImageIcon selectRankIcon(StreamTask task) {
		switch (displayRank(task)) {
			case "high":
				return Stream.ICON_HI_RANK;
			case "medium":
				return Stream.ICON_MED_RANK;
			case "low":
				return Stream.ICON_LOW_RANK;
			default:
				// WON'T HAPPEN
				return Stream.ICON_LOW_RANK;
		}
	}

	static void updateCalendarIcon(CalendarIconUI calIcon, StreamTask task,
			Boolean isStartTime) {
		Calendar cal;
		if (isStartTime) {
			cal = task.getStartTime();
		} else {
			cal = task.getDeadline();
		}
		if (cal == null) {
			calIcon.hideView();
		} else {
			calIcon.updateView(cal);
		}
	}

	public static String getMonthAbbrev(int mon) {
		return TimeParser.MONTHS[mon].substring(0, 3).toUpperCase();
	}

	public static String displayHour(int hr, int min) {
		return String.format(VIEW_HOUR, hr, StreamUtil.addZeroToTime(min));
	}

}
