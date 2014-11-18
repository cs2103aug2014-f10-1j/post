package parser;

public class RankParser implements BaseParser {

	public enum RankType {
		HI, MED, LO, NULL;
	}

	@Override
	public String translate(Object obj) {
		assert (obj instanceof RankType) : "ERROR";
		switch ((RankType) obj) {
			case HI:
				return "high";
			case MED:
				return "medium";
			case LO:
				return "low";
			default:
				return null;
		}
	}

	@Override
	public RankType parse(String str) {
		switch (str.toLowerCase()) {
			case "high":
			case "hi":
			case "h":
				return RankType.HI;
			case "medium":
			case "med":
			case "m":
				return RankType.MED;
			case "low":
			case "l":
				return RankType.LO;
			default:
				return RankType.NULL;
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
