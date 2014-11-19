package fileio;

import exception.StreamIOException;

//@author A0093874N

public interface Converter {

	public Object convertTaskMap(Object map) throws StreamIOException;

	public Object convertTaskList(Object list) throws StreamIOException;

	public Object convertTask(Object obj) throws StreamIOException;

}
