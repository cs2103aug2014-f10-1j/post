package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

//@author A0118007R
/**
 * StreamObject is a class that stores the data of all StreamTasks inside a
 * hashmap and an arraylist.
 * 
 * This is the internal storage of the software.
 * 
 * It is implemented as a singleton.
 * 
 * @version V0.5
 */
public class StreamObject {

	private HashMap<String, StreamTask> taskMap;
	private ArrayList<String> taskList;

	private StreamObject() {
		this.taskMap = new HashMap<String, StreamTask>();
		this.taskList = new ArrayList<String>();
	}

	/**
	 * Accesses the constructor of this class
	 * 
	 * @return StreamObject - an instance of this class
	 */
	public static StreamObject init() {
		return new StreamObject();
	}

	//@author generated
	/**
	 * Gets the hashmap of the current state of tasks stored as StreamTask
	 * objects.
	 * 
	 * @return taskMap - the hashmap containing all current tasks
	 */
	public HashMap<String, StreamTask> getTaskMap() {
		return taskMap;
	}

	/**
	 * Sets the hashmap of the current tasks to be the one that is inputted. For
	 * loading purposes.
	 * 
	 * @param taskMap
	 *            - the new hashmap containing all new tasks
	 */
	public void setTaskMap(HashMap<String, StreamTask> taskMap) {
		this.taskMap = taskMap;
	}

	/**
	 * Gets the ArrayList containing all current tasks stored as task names.
	 * 
	 * @return taskList - the ArrayList containing all current tasks
	 */
	public ArrayList<String> getTaskList() {
		return taskList;
	}

	/**
	 * Sets the arraylist of the current tasks to be the one that is inputted.
	 * For loading purposes.
	 * 
	 * @param taskList
	 *            - the new arraylist containing all new tasks
	 */
	public void setTaskList(ArrayList<String> taskList) {
		this.taskList = taskList;
	}

	//@author A0096529N

	// Delegate methods

	/**
	 * Gets the size (number) of tasks inside the arraylist.
	 * 
	 * @return size - the number of tasks.
	 */
	public int size() {
		return taskList.size();
	}

	/**
	 * Gets a task (as a StreamTask) by using the task name
	 * 
	 * @param taskName
	 * @return the task as an instance of StreamTask
	 */
	public StreamTask get(String taskName) {
		return taskMap.get(taskName.toLowerCase());
	}

	/**
	 * Gets a task's name based on the index of the task
	 * 
	 * @param index
	 * @return task name - the name of the task
	 */
	public String get(int index) {
		return taskList.get(index);
	}

	/**
	 * Adds a new task to the storage Updates the hashmap and arraylist
	 * accordingly
	 * 
	 * @param taskName
	 * @param task
	 * @return
	 */
	public StreamTask put(String taskName, StreamTask task) {
		taskList.add(taskName);
		return taskMap.put(taskName.toLowerCase(), task);
	}

	/**
	 * Adds a new task to the storage based on the specified index Updates the
	 * hashmap and arraylist accordingly
	 * 
	 * @param taskName
	 * @param task
	 * @return
	 */
	public StreamTask put(String taskName, StreamTask task, int index) {
		taskList.add(index, taskName);
		return taskMap.put(taskName.toLowerCase(), task);
	}

	/**
	 * Gets the keySet of the hashmap
	 * 
	 * @return keySet - the keys of taskMap
	 */
	public Set<String> keySet() {
		return taskMap.keySet();
	}

	/**
	 * Checks whether a given task name is already used / exists in the storage
	 * 
	 * @param taskName
	 * @return true if it exists, false otherwise
	 */
	public boolean containsKey(String taskName) {
		return taskMap.containsKey(taskName.toLowerCase());
	}

	/**
	 * Checks whether a given task object is already included in the hashmap of tasks
	 * 
	 * @param task
	 * @return true if it is included, false otherwise
	 */
	public boolean containsValue(StreamTask task) {
		return taskMap.containsValue(task);
	}

	/**
	 * Gets the index of a given task in the arraylist
	 * 
	 * @param taskName
	 * @return index - the index of the task
	 */
	public int indexOf(String taskName) {
		return taskList.indexOf(taskName);
	}

	/**
	 * Checks whether a task name is already used
	 * 
	 * @param taskName
	 * @return true if it is already used, false otherwise
	 */
	public boolean contains(String taskName) {
		return taskList.contains(taskName);
	}

	/**
	 * remove a given task from the storage.
	 * update the arraylist and hashmap accordingly
	 * 
	 * @param taskName
	 */
	public void remove(String taskName) {
		taskMap.remove(taskName.toLowerCase());
		taskList.remove(taskName);
	}

	/**
	 * Clears all tasks
	 * 
	 */
	public void clear() {
		taskMap.clear();
		taskList.clear();
	}
}
