package parser;

import model.StreamTask;

//@author A0093874N

public class MarkParser implements BaseParser {

	public enum MarkType {
		DONE, NOT, OVERDUE, INACTIVE, NULL;
	}

	@Override
	public MarkType parse(String str) {
		switch (str.toLowerCase()) {
			case "done":
			case "finished":
			case "over":
				return MarkType.DONE;
			case "not done":
			case "not finished":
			case "ongoing":
				return MarkType.NOT;
			case "overdue":
				return MarkType.OVERDUE;
			case "inactive":
				return MarkType.INACTIVE;
			default:
				return MarkType.NULL;
		}
	}
	
	public MarkType parse(Boolean isDone) {
		if (isDone) {
			return MarkType.DONE;
		} else {
			return MarkType.NOT;
		}
	}

	public MarkType parse(StreamTask task) {
		if (task.isDone()) {
			return MarkType.DONE;
		} else if (task.isOverdue()) {
			return MarkType.OVERDUE;
		} else if (task.isInactive()) {
			return MarkType.INACTIVE;
		} else {
			return MarkType.NOT;
		}
	}

	public String translate(Object obj) {
		assert (obj instanceof MarkType) : "ERROR";
		switch ((MarkType) obj) {
			case DONE:
				return "done";
			case NOT:
				return "ongoing";
			case OVERDUE:
				return "overdue";
			case INACTIVE:
				return "inactive";
			default:
				return null;
		}
	}

	public Boolean isParseable(String str) {
		switch (parse(str)) {
			case NULL:
				return false;
			default:
				return true;
		}
	}

}
