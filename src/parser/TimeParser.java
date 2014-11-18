package parser;

import java.util.Calendar;

import com.mdimension.jchronic.Chronic;

public class TimeParser implements BaseParser {

	public static final String[] MONTHS = { "January", "February", "March",
			"April", "May", "June", "July", "August", "September", "October",
			"November", "December" };
	public static final String TIME_DELIMITER = ":";

	@Override
	public Calendar parse(String str) {
		if (str.equals("null")) {
			return null;
		}
		try {
			Calendar parsed = Chronic.parse(str).getBeginCalendar();
			return parsed;
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public String translate(Object obj) {
		assert (obj instanceof Calendar) : "ERROR";
		Calendar calendar = (Calendar) obj;
		return addZeroToTime(calendar.get(Calendar.DAY_OF_MONTH)) + " "
				+ MONTHS[calendar.get(Calendar.MONTH)] + " "
				+ calendar.get(Calendar.YEAR) + " "
				+ addZeroToTime(calendar.get(Calendar.HOUR_OF_DAY))
				+ TIME_DELIMITER + addZeroToTime(calendar.get(Calendar.MINUTE))
				+ TIME_DELIMITER + addZeroToTime(calendar.get(Calendar.SECOND));
	}

	@Override
	public Boolean isParseable(String str) {
		if (str.trim().equals("null")) {
			/*
			 * special case: we allow "null" since this is to indicate null
			 * timing
			 */
			return true;
		}
		try {
			Chronic.parse(str).getBeginCalendar();
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static String addZeroToTime(Integer time) {
		String convertedTime = time.toString();
		if (time < 10) {
			convertedTime = "0" + convertedTime;
		}
		return convertedTime;
	}

	public static String getMonthAbbrev(int mon) {
		return MONTHS[mon].substring(0, 3).toUpperCase();
	}

}
