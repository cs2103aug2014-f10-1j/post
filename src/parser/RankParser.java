package parser;

//@author A0093874N
/**
 * Parser ranking types such as "high", "hi", "low", …
 */
public class RankParser implements BaseParser {

	private static RankParser self = null;

	public enum RankType {
		HI, MED, LO, NULL;
	}
	
	private RankParser() {
		
	}

	public static RankParser init() {
		if (self == null) {
			self = new RankParser();
		}
		return self;
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
