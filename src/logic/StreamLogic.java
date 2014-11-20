package logic;

import java.util.ArrayList;
import java.util.HashMap;

import logger.Loggable;
import model.StreamObject;
import model.StreamTask;

/**
 * Some documentation.
 * 
 * @version V0.5
 */

// @author A0118007R
public class StreamLogic extends Loggable {

	private StreamObject streamObject;
	public UndoLogic undoLogic;
	public CRDLogic crdLogic;
	public ModificationLogic modLogic;
	public OrderLogic orderLogic;
	public SearcherLogic searchLogic;

	private StreamLogic(StreamObject stobj) {
		this.streamObject = stobj;
		this.undoLogic = UndoLogic.init();
		this.crdLogic = CRDLogic.init(stobj);
		this.modLogic = ModificationLogic.init(crdLogic);
		this.orderLogic = OrderLogic.init(stobj);
		this.searchLogic = SearcherLogic.init(stobj);
	}

	/**
	 * Initializes StreamLogic.
	 * 
	 * @param streamObject
	 * @return logic, the instance of the StreamLogic class
	 */
	public static StreamLogic init(StreamObject streamObject) {
		return new StreamLogic(streamObject);
	}

	// @author A0093874N
	/**
	 * Returns the indices of all tasks.
	 * 
	 * @return <strong>indices</strong> - the ArrayList containing the indices
	 *         of all tasks.
	 */
	public ArrayList<Integer> getIndices() {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < streamObject.size(); i++) {
			indices.add(i + 1);
		}
		return indices;
	}

	/**
	 * Gets the number of tasks added.
	 * 
	 */
	public int getNumberOfTasks() {
		return streamObject.size();
	}

	// @author A0096529N
	/**
	 * Retrieves the task name by index
	 * 
	 * <p>
	 * Precondition: index is a valid index
	 * </p>
	 * 
	 * @param index
	 *            the index of the task
	 */
	public String getTaskNumber(int index) {
		return streamObject.getTaskList().get(index - 1);
	}

	/**
	 * @return taskMap a copy of the task map.
	 */
	public HashMap<String, StreamTask> getTaskMap() {
		return new HashMap<String, StreamTask>(streamObject.getTaskMap());
	}

	/**
	 * @return taskList a copy of the task list.
	 */
	public ArrayList<String> getTaskList() {
		return new ArrayList<String>(streamObject.getTaskList());
	}

	/**
	 * @return taskList a copy of the task list.
	 */
	public ArrayList<StreamTask> getStreamTaskList() {
		ArrayList<StreamTask> taskList = new ArrayList<StreamTask>();
		HashMap<String, StreamTask> taskMap = streamObject.getTaskMap();
		for (String key : taskMap.keySet()) {
			taskList.add(taskMap.get(key));
		}
		return taskList;
	}

	// @author A0093874N

	public ArrayList<StreamTask> getStreamTaskList(ArrayList<Integer> indices) {
		ArrayList<StreamTask> tasks = new ArrayList<StreamTask>();
		HashMap<String, StreamTask> taskMap = streamObject.getTaskMap();
		ArrayList<String> taskList = streamObject.getTaskList();
		for (Integer index : indices) {
			tasks.add(taskMap.get(taskList.get(index - 1).toLowerCase()));
		}
		return tasks;
	}

	// @author generated
	@Override
	public String getComponentName() {
		return "STREAMLOGIC";
	}

}