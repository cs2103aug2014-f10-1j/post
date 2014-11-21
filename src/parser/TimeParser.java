package parser;

import java.util.Calendar;

import util.StreamUtil;

import com.mdimension.jchronic.Chronic;

//@author A0093874N
/**
 * Parses time String with the help of <b>JChronic</b>. Able to parse non-exact
 * times such as "today", "tomorrow", "next week", …
 */
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
		return StreamUtil.addZeroToTime(calendar.get(Calendar.DAY_OF_MONTH))
				+ " " + MONTHS[calendar.get(Calendar.MONTH)] + " "
				+ calendar.get(Calendar.YEAR) + " "
				+ StreamUtil.addZeroToTime(calendar.get(Calendar.HOUR_OF_DAY))
				+ TIME_DELIMITER
				+ StreamUtil.addZeroToTime(calendar.get(Calendar.MINUTE))
				+ TIME_DELIMITER
				+ StreamUtil.addZeroToTime(calendar.get(Calendar.SECOND));
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

}
