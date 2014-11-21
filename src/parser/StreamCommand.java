package parser;

//@author A0093874N
/**
 * <b>StreamParser</b>'s parsing result. The command is packed into three
 * fields: the <i>key</i>, the task <i>index</i> number, and the <i>content</i>.
 * Only the keyword is guaranteed non-empty for all commands.
 */
public class StreamCommand {

	public enum CommandType {
		INIT, ADD, DEL, DESC, DUE, START, VIEW, RANK, MODIFY, NAME, MARK, TAG, UNTAG, SEARCH, SORT, UNSORT, FILTER, CLRSRC, CLEAR, UNDO, EXIT, ERROR, RECOVER, DISMISS, FIRST, PREV, NEXT, LAST, PAGE, HELP;
	}

	private CommandType key = null;
	private Integer index = null;
	private Object content = null;

	public CommandType getKey() {
		return key;
	}

	public void setKey(CommandType key) {
		this.key = key;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

}