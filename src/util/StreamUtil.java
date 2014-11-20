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

	public static Boolean calEqual(Calendar firstCal, Calendar secondCal) {
		return firstCal.get(Calendar.YEAR) == secondCal.get(Calendar.YEAR)
				&& firstCal.get(Calendar.MONTH) == secondCal
						.get(Calendar.MONTH)
				&& firstCal.get(Calendar.DAY_OF_MONTH) == secondCal
						.get(Calendar.DAY_OF_MONTH)
				&& firstCal.get(Calendar.HOUR_OF_DAY) == secondCal
						.get(Calendar.HOUR_OF_DAY)
				&& firstCal.get(Calendar.MINUTE) == secondCal
						.get(Calendar.MINUTE)
				&& firstCal.get(Calendar.MINUTE) == secondCal
						.get(Calendar.MINUTE);
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