package parser;

public class SortParser implements BaseParser {

	public enum SortType {
		ALPHA, START, END, TIME, IMPORTANCE, NULL;
	}

	@Override
	public SortType parse(String str) {
		switch (str.toLowerCase()) {
			case "d":
			case "due":
			case "deadline":
			case "end":
			case "endtime":
				return SortType.END;
			case "s":
			case "start":
			case "begin":
			case "starttime":
				return SortType.START;
			case "a":
			case "alpha":
			case "alphabetical":
			case "alphabetically":
				return SortType.ALPHA;
			case "t":
			case "time":
				return SortType.TIME;
			case "":
			case "impt":
			case "importance":
			case "priority":
				return SortType.IMPORTANCE;
			default:
				return SortType.NULL;
		}
	}

	public Boolean getOrder(String order) throws Exception {
		switch (order.toLowerCase()) {
			case "a":
			case "asc":
			case "ascending":
				return false;
			case "":
			case "d":
			case "desc":
			case "descending":
				return true;
			default:
				throw new Exception();
		}
	}

	@Override
	// method unused
	public String translate(Object obj) {
		return null;
	}

	@Override
	public Boolean isParseable(String str) {
		String order = "";
		String type = "";
		String[] contents = str.split(" ");
		try {
			type = contents[0];
			order = contents[1];
		} catch (Exception e) {
			// ok to ignore
		}
		switch (parse(type)) {
			case NULL:
				return false;
			default:
				try {
					getOrder(order);
					return true;
				} catch (Exception e) {
					return false;
				}
		}
	}

}
