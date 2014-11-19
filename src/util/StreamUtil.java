package util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A class to contain helper methods (methods that help some of Stream's
 * processes but don't really have anything to do with the nature of Stream's
 * processes; e.g listing down an array).
 * 
 * @version V0.5
 */
public class StreamUtil {

	private static final String PREFIX_INPUT = "<< ";
	private static final String PREFIX_OUTPUT = ">> ";

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
	 * Decorates a <i>logMessage</i> to make it look like terminal input.
	 * 
	 * @return <b>String</b> - the decorated log message
	 */
	public static String showAsTerminalInput(String logMessage) {
		return PREFIX_INPUT + logMessage;
	}

	/**
	 * Decorates a <i>logMessage</i> to make it look like terminal response.
	 * 
	 * @return <b>String</b> - the decorated log message
	 */
	public static String showAsTerminalResponse(String logMessage) {
		return PREFIX_OUTPUT + logMessage;
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

	//@author A0118007R

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

}