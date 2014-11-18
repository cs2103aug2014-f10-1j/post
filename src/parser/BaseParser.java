package parser;

public interface BaseParser {

	/**
	 * some docs
	 */
	public Object parse(String str);

	public String translate(Object obj);

	public Boolean isParseable(String str);

}
