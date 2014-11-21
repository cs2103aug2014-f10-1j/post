package fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.StreamTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import exception.StreamIOException;

//@author A0096529N
/**
 * Handles saving-related processes and model-to-JSON conversions.
 */
public class SaveIO implements Converter {

	@Override
	@SuppressWarnings("unchecked")
	public JSONArray convertTaskMap(Object map) throws StreamIOException {
		Map<String, StreamTask> taskMap = (Map<String, StreamTask>) map;
		JSONArray mapJson = new JSONArray();
		for (String key : taskMap.keySet()) {
			JSONObject taskJson = convertTask(taskMap.get(key));
			mapJson.put(taskJson);
		}
		return mapJson;
	}

	@Override
	@SuppressWarnings("unchecked")
	/**
	 * Converts a list of task names to json object with each task mapped to
	 * their index in the list.
	 * 
	 * @param taskList
	 *            the list to be converted to json format
	 * @return taskListJson json with array of tasks mapped to their index
	 * @throws StreamIOException
	 *             if the json could not be constructed
	 */
	public JSONObject convertTaskList(Object list) throws StreamIOException {
		List<String> taskList = (List<String>) list;
		try {
			JSONObject orderListJson = new JSONObject();
			for (int i = 0; i < taskList.size(); i++) {
				orderListJson.put(String.valueOf(i), taskList.get(i));
			}
			return orderListJson;
		} catch (JSONException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	@Override
	public JSONObject convertTask(Object obj) throws StreamIOException {
		StreamTask task = (StreamTask) obj;
		try {
			JSONObject taskJson = new JSONObject();
			taskJson.put(StreamIO.KEY_NAME, task.getTaskName());
			taskJson.put(StreamIO.KEY_DESCRIPTION, task.getDescription());
			taskJson.put(StreamIO.KEY_TAGS, task.getTags());
			taskJson.put(StreamIO.KEY_RANK, task.getRank());
			taskJson.put(StreamIO.KEY_STARTTIME,
					formatDate(task.getStartTime()));
			taskJson.put(StreamIO.KEY_DEADLINE, formatDate(task.getDeadline()));
			taskJson.put(StreamIO.KEY_DONE, task.isDone());
			return taskJson;
		} catch (JSONException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	static String formatDate(Calendar calendar) {
		if (calendar == null) {
			return null;
		} else {
			return formatDate(calendar.getTime());
		}
	}

	static String formatDate(Date date) {
		if (date == null) {
			return null;
		} else {
			return StreamIO.dateFormat.format(date);
		}
	}

	/**
	 * Serialized the given JSONObject and writes to the specified File.
	 * 
	 * @param destin
	 *            destination file to write the data
	 * @param tasksJson
	 *            JSONObject to be serialized
	 * @throws IOException
	 *             from file IO errors
	 */
	void writeToFile(File destin, JSONObject tasksJson) throws IOException {
		FileWriter fwriter = new FileWriter(destin, false);
		BufferedWriter bw = new BufferedWriter(fwriter);
		try {
			bw.write(tasksJson.toString());
			bw.newLine();
		} finally {
			try {
				bw.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	void save(File streamFile, Map<String, StreamTask> taskMap,
			List<String> taskList) throws StreamIOException, IOException {
		JSONArray taskMapJson = convertTaskMap(taskMap);
		JSONObject orderListJson = convertTaskList(taskList);
		JSONObject tasksJson = new JSONObject();
		tasksJson.put(StreamIO.KEY_TASKMAP, taskMapJson);
		tasksJson.put(StreamIO.KEY_TASKLIST, orderListJson);
		writeToFile(streamFile, tasksJson);
	}

	void saveLogFile(List<String> logMessages, String logFileName)
			throws IOException {
		FileWriter fwriter = new FileWriter(logFileName, true);
		BufferedWriter bw = new BufferedWriter(fwriter);
		try {
			for (int i = 0; i < logMessages.size(); i++) {
				bw.write(logMessages.get(i));
				bw.newLine();
			}
		} finally {
			bw.close();
		}
	}

}