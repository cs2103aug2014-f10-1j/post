package parser;

//@author A0093874N
/**
 * The basic parsing interface implemented by other classes.
 */
public interface BaseParser {

	public Object parse(String str);

	public String translate(Object obj);

	public Boolean isParseable(String str);

}
