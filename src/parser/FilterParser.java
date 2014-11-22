package parser;

import parser.MarkParser.MarkType;
import parser.RankParser.RankType;

//@author A0093874N
/**
 * Parses filter types such as "rank high", "not done", "due before today", ...
 */
public class FilterParser implements BaseParser {

	private MarkParser mp;
	private RankParser rp;
	private TimeParser tp;

	private static FilterParser self = null;

	public enum FilterType {
		DONE, NOT, HIRANK, MEDRANK, LORANK, DUEBEF, DUEAFT, STARTBEF, STARTAFT, NOTIMING, DEADLINED, EVENT, NULL, OVERDUE, INACTIVE;
	}

	private FilterParser(MarkParser mp, RankParser rp, TimeParser tp) {
		this.mp = mp;
		this.rp = rp;
		this.tp = tp;
	}

	public static FilterParser init(MarkParser mp, RankParser rp, TimeParser tp) {
		if (self == null) {
			self = new FilterParser(mp, rp, tp);
		}
		return self;
	}

	@Override
	public FilterType parse(String str) {
		String[] contents = str.split(" ", 2);
		MarkType parsedMark = mp.parse(contents[0]);
		switch (parsedMark) {
			case DONE:
				return FilterType.DONE;
			case NOT:
				return FilterType.NOT;
			case OVERDUE:
				return FilterType.OVERDUE;
			case INACTIVE:
				return FilterType.INACTIVE;
			default:
		}
		if (contents[0].equals("rank")) {
			if (contents.length == 2) {
				RankType parsedRank = rp.parse(contents[1]);
				switch (parsedRank) {
					case HI:
						return FilterType.HIRANK;
					case MED:
						return FilterType.MEDRANK;
					case LO:
						return FilterType.LORANK;
					default:
						return FilterType.NULL;
				}
			} else {
				return FilterType.NULL;
			}
		} else {
			contents = str.split(" ", 3);
			if (contents.length == 3 && tp.isParseable(contents[2])) {
				switch (contents[0] + " " + contents[1]) {
					case "due before":
						return FilterType.DUEBEF;
					case "due after":
						return FilterType.DUEAFT;
					case "start before":
						return FilterType.STARTBEF;
					case "start after":
						return FilterType.STARTAFT;
					default:
						return FilterType.NULL;
				}
			} else {
				contents = str.split(" ", 2);
				if (contents.length == 2) {
					switch (contents[0] + " " + contents[1]) {
						case "no timing":
							return FilterType.NOTIMING;
						case "has deadline":
							return FilterType.DEADLINED;
						default:
							return FilterType.NULL;
					}
				} else {
					switch (contents[0]) {
						case "deadlined":
							return FilterType.DEADLINED;
						case "event":
						case "timed":
							return FilterType.EVENT;
						default:
							return FilterType.NULL;
					}
				}
			}
		}
	}

	@Override
	public String translate(Object obj) {
		assert (obj instanceof FilterType) : "ERROR";
		switch ((FilterType) obj) {
			case DONE:
				return "done";
			case NOT:
				return "ongoing";
			case OVERDUE:
				return "overdue";
			case INACTIVE:
				return "inactive";
			case HIRANK:
				return "ranked high";
			case MEDRANK:
				return "ranked medium";
			case LORANK:
				return "ranked low";
			case NOTIMING:
				return "with no timing";
			case DEADLINED:
				return "with deadline";
			case EVENT:
				return "with start and end time";
			case DUEBEF:
				return "due or ending before ";
			case DUEAFT:
				return "due or ending after ";
			case STARTBEF:
				return "starting before ";
			case STARTAFT:
				return "starting after ";
			default:
				// WILL NOT HAPPEN
				return null;
		}
	}

	@Override
	public Boolean isParseable(String str) {
		switch (parse(str)) {
			case NULL:
				return false;
			default:
				return true;
		}
	}

}