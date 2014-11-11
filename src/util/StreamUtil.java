package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import model.StreamTask;
import parser.StreamParser;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;

/**
 * A class to contain helper methods (methods that help some of Stream's
 * processes but don't really have anything to do with the nature of Stream's
 * processes; e.g listing down an array).
 * 
 * @version V0.5
 */
public class StreamUtil {

	//@author A0093874N

	/**
	 * A utility function to list down contents of <i>array</i>, connecting each
	 * element with the specified <i>connector</i>.
	 * 
	 * @param array
	 *            - array of <b>String</b> elements
	 * @param connector
	 *            - the connector character
	 * @return <b>String</b> - the listed down array contents
	 */
	public static String listDownArrayContent(ArrayList<String> array,
			String connector) {
		String result = "";
		for (String str : array) {
			result += connector + str;
		}
		return result.substring(connector.length());
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
			return "no description provided";
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
		if (tags.size() == 0) {
			return "no tags added";
		} else {
			return listDownArrayContent(tags, ", ");
		}
	}

	/**
	 * Displays the status of a task to user.
	 * 
	 * @param task
	 * @return <b>String</b> - the task status
	 */
	public static String displayStatus(StreamTask task) {
		if (task.isOverdue()) {
			return "overdue";
		} else if (task.isInactive()) {
			return "inactive";
		} else {
			return StreamParser.translateMarking(StreamParser.parseMarking(task
					.isDone()));
		}
	}

	/**
	 * A utility function to add 0 to <i>time</i> less than 10, e.g from 9 to
	 * 09.
	 * 
	 * @return <b>String</b> - the time appended with 0 when applicable
	 */
	public static String addZeroToTime(Integer time) {
		String convertedTime = time.toString();
		if (time < 10) {
			convertedTime = "0" + convertedTime;
		}
		return convertedTime;
	}

	/**
	 * Decorates a <i>logMessage</i> to make it look like terminal input.
	 * 
	 * @return <b>String</b> - the decorated log message
	 */
	public static String showAsTerminalInput(String logMessage) {
		return StreamConstants.PREFIX_INPUT + logMessage;
	}

	/**
	 * Decorates a <i>logMessage</i> to make it look like terminal response.
	 * 
	 * @return <b>String</b> - the decorated log message
	 */
	public static String showAsTerminalResponse(String logMessage) {
		return StreamConstants.PREFIX_OUTPUT + logMessage;
	}

	/**
	 * Checks if both lists of <b>String</b>, <i>firstList</i> and
	 * <i>secondList</i>, are equal. It is assumed that within each list, all
	 * elements are distinct.
	 * 
	 * @param firstList
	 *            - the first list of <b>String</b>
	 * @param secondList
	 *            - the second list of <b>String</b>
	 * @return <b>Boolean</b> - indicates whether both lists are equal or not
	 */
	public static Boolean listEqual(List<String> firstList,
			List<String> secondList) {
		if (firstList.size() != secondList.size()) {
			return false;
		}
		for (String str : firstList) {
			if (!secondList.contains(str)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks two calendars <i>startTime</i> and <i>endTime</i>, formats them
	 * when applicable, and present to user accordingly depending on the
	 * existence of each.
	 * 
	 * @return <b>String</b> - the properly formatted and presentable time
	 */
	public static String getWrittenTime(Calendar startTime, Calendar endTime) {
		if (startTime == null && endTime == null) {
			return "no timing specified";
		} else if (startTime == null) {
			return "by " + getCalendarWriteUp(endTime);
		} else if (endTime == null) {
			// is there a task like this?
			return "from " + getCalendarWriteUp(startTime);
		} else {
			return "from " + getCalendarWriteUp(startTime) + " to "
					+ getCalendarWriteUp(endTime);
		}
	}

	/**
	 * Converts a <i>calendar</i> to format dd MONTHNAME yyyy hh:mm:ss.
	 * 
	 * @return <b>String</b> - the converted calendar
	 */
	public static String getCalendarWriteUp(Calendar calendar) {
		return addZeroToTime(calendar.get(Calendar.DAY_OF_MONTH)) + " "
				+ StreamConstants.Calendar.MONTHS[calendar.get(Calendar.MONTH)]
				+ " " + calendar.get(Calendar.YEAR) + " "
				+ addZeroToTime(calendar.get(Calendar.HOUR_OF_DAY))
				+ StreamConstants.TIME_DELIMITER
				+ addZeroToTime(calendar.get(Calendar.MINUTE))
				+ StreamConstants.TIME_DELIMITER
				+ addZeroToTime(calendar.get(Calendar.SECOND));
	}

	/**
	 * Converts a dd MONTHNAME yyyy hh:mm:ss calendar to dd MONTHNAME yyyy hh mm
	 * ss.
	 * 
	 * @param str
	 *            - a converted calendar
	 * @return <b>String</b> - another converted calendar
	 */
	public static String stripCalendarChars(String str) {
		str = str.replaceAll(StreamConstants.TIME_DELIMITER, " ");
		return str;
	}

	/**
	 * Gets the abbreviated month name, e.g JAN, FEB, ...
	 * 
	 * @param mon
	 *            - the month index
	 * @return <b>String</b> - the abbreviated month name
	 */
	public static String getMonthAbbrev(int mon) {
		return StreamConstants.Calendar.MONTHS[mon].substring(0, 3)
				.toUpperCase();
	}

	/**
	 * Checks whether a particular <b>String</b> is parseable by JChronic to
	 * form a <b>Calendar<b>
	 * 
	 * @param date
	 *            - the <b>String</b> to be parsed
	 * @return <b>boolean</b> - indicates whether parsing can be done or not
	 */
	public static boolean isParseableDate(String date) {
		if (date.trim().equals("null")) {
			/*
			 * special case: we allow "null" since this is to indicate null
			 * timing
			 */
			return true;
		}
		try {
			Chronic.parse(date).getBeginCalendar();
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}

	//@author A0096529N

	public static final SimpleDateFormat cleanDateFormat = new SimpleDateFormat(
			"yyyyMMdd");

	/**
	 * Converts a <i>calendar</i> to simple date format yyyyMMdd.
	 * 
	 * @return <b>String</b> - the parsed calendar
	 */
	public static String getDateString(Calendar calendar) {
		return cleanDateFormat.format(calendar.getTime());
	}

	//@author A0118007R

	/**
	 * With the help of JChronic, parses a <b>String</b> <i>due</i> and tries to
	 * get a proper calendar from it when possible, or simply return <i>due</i>
	 * itself if fails.
	 * 
	 * @param due
	 *            - the <b> String to be parsed
	 * @return <b>String</b> - the parse result, regardless of successful or not
	 */
	public static String parseWithChronic(String due) {
		Span x;
		try {
			x = Chronic.parse(due);
			Calendar begin = x.getBeginCalendar();
			String calendarWriteUp = StreamUtil.getCalendarWriteUp(begin);
			due = StreamUtil.stripCalendarChars(calendarWriteUp);
		} catch (NullPointerException e) {
			System.out.println("\"" + due + "\" cannot be parsed");
			// TODO change to logging... show to user?
		}
		return due;
	}

	/**
	 * Parses <i>contents</i> back to <b>Calendar</b> format.
	 * 
	 * @param contents
	 *            - the <b>String</b> to be parsed
	 * @return <b>Calendar</b>
	 */
	public static Calendar parseCalendar(String contents) {
		String[] dueDate = contents.split(" ");
		int[] dueDateParameters = new int[dueDate.length];
		for (int i = 0; i < dueDate.length; i++) {
			if (i != 1) {
				dueDateParameters[i] = Integer.parseInt(dueDate[i].trim());
			}
		}
		int date = dueDateParameters[0];
		int month = getMonthIndex(dueDate[1]);
		int year = dueDateParameters[2];
		int hour = dueDateParameters[3];
		int minute = dueDateParameters[4];
		int second = dueDateParameters[5];
		Calendar calendar = new GregorianCalendar(year, month - 1, date, hour,
				minute, second);
		return calendar;
	}

	/**
	 * Gets the month index of <i>month</i>, i.e January is 1, February is 2,
	 * ...
	 * 
	 * @return <b>int</b> - the month index of <i>month</i>
	 */
	public static int getMonthIndex(String month) {
		switch (month) {
			case "January":
				return 1;
			case "February":
				return 2;
			case "March":
				return 3;
			case "April":
				return 4;
			case "May":
				return 5;
			case "June":
				return 6;
			case "July":
				return 7;
			case "August":
				return 8;
			case "September":
				return 9;
			case "October":
				return 10;
			case "November":
				return 11;
			case "December":
				return 12;
			default:
				return 0;
		}
	}

	/**
	 * Checks whether <i>param</i> is a valid modifier attribute for Stream
	 * modify command.
	 * 
	 * @return <b>boolean</b> - indicates whether <i>param</i> is a valid
	 *         attribute
	 */
	public static boolean isValidAttribute(String param) {
		for (String s : StreamConstants.MODIFICATION_ATTRIBUTES) {
			if (s.equals(param)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the <i>deadline</i> entered is after <i>startTime</i>
	 * 
	 * @return <b>boolean</b> - indicates whether <i>deadline</i> is after
	 *         <i>startTime</i>
	 */
	public static boolean isValidDeadline(Calendar deadline, Calendar startTime) {
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
	public static boolean isValidStartTime(Calendar deadline, Calendar startTime) {
		if (deadline == null || startTime == null) {
			return true;
		} else {
			return startTime.before(deadline);
		}
	}

	//@author A0119401U

	/**
	 * A utility function to check whether <i>str</i> can be parsed as an
	 * <b>Integer></b>.
	 * 
	 * @param str
	 *            - the String to be parsed
	 */
	public static boolean isInteger(String str) {
		int size = str.length();
		for (int i = 0; i < size; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return size > 0;
	}

	/**
	 * 
	 * A utility function to check whether the given index is within the range
	 * of the whole list of tasks
	 * 
	 * @param index
	 * @param numOfTasks
	 * @return <b>boolean</b> - indicates whether it's within the range or not
	 */

	public static boolean isWithinRange(int index, int numOfTasks) {
		return index >= 1 && index <= numOfTasks;
	}

	//@author A0118007R-unused

	/**
	 * @deprecated
	 */
	public static boolean isValidMonth(int month) {
		return (month >= 1) && (month <= 12);
	}

	/**
	 * @deprecated
	 */
	public static boolean isValidDate(int day, int month, int year) {
		if (isMonthWith31Days(month)) {
			return (day >= 1) && (day <= 31);
		} else if (month == 2) {
			if (isLeapYear(year)) {
				return (day >= 1) && (day <= 29);
			} else {
				return (day >= 1) && (day <= 28);
			}
		} else {
			return (day >= 1) && (day <= 30);
		}

	}

	/**
	 * @deprecated
	 */
	public static boolean isLeapYear(int year) {
		if (year % 400 == 0) {
			return true;
		} else if (year % 100 == 0) {
			return false;
		} else {
			return year % 4 == 0;
		}
	}

	/**
	 * @deprecated
	 */
	private static boolean isMonthWith31Days(int month) {
		return (month == 1) || (month == 3) || (month == 5) || (month == 7)
				|| (month == 8) || (month == 10) || (month == 12);
	}

	/**
	 * @deprecated
	 */
	public static boolean isValidYear(int year) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		return year >= currentYear;
	}

	/**
	 * @deprecated
	 */
	public static int parseYear(String[] dueDate) {
		int year;
		if (dueDate.length == 2) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		} else {
			year = Integer.parseInt(dueDate[2].trim());
		}
		return year;
	}

	/**
	 * @deprecated not needed
	 */
	public static String getCalendarWriteUpForUndo(Calendar calendar) {
		return addZeroToTime(calendar.get(Calendar.MONTH) + 1)
				+ StreamConstants.DATE_DELIMITER
				+ addZeroToTime(calendar.get(Calendar.DAY_OF_MONTH))
				+ StreamConstants.DATE_DELIMITER + calendar.get(Calendar.YEAR)
				+ " " + addZeroToTime(calendar.get(Calendar.HOUR_OF_DAY))
				+ StreamConstants.TIME_DELIMITER
				+ addZeroToTime(calendar.get(Calendar.MINUTE))
				+ StreamConstants.TIME_DELIMITER
				+ addZeroToTime(calendar.get(Calendar.SECOND));
	}
}