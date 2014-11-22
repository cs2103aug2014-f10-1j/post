package fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.StreamTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import exception.StreamIOException;

//@author A0096529N
/**
 * Handles loading-related processes and JSON-to-model conversions.
 */
public class LoadIO implements Converter {

	private static LoadIO self = null;

	/**
	 * Parses the json object, with a list of task names mapped to their index,
	 * into a string list.
	 * 
	 * @param orderListJson
	 *            json with the task names mapped to their index
	 * @return taskList the list of task names
	 * @throws StreamIOException
	 *             if the json could not be parsed
	 */
	@Override
	public List<String> convertTaskList(Object list) throws StreamIOException {
		JSONObject orderListJson = (JSONObject) list;
		try {
			List<String> taskList = new ArrayList<String>();
			for (int i = 0; orderListJson.has(String.valueOf(i)); i++) {
				taskList.add(orderListJson.getString(String.valueOf(i)));
			}
			return taskList;
		} catch (JSONException e) {
			throw new StreamIOException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage(), e);
		}
	}

	@Override
	public HashMap<String, StreamTask> convertTaskMap(Object map)
			throws StreamIOException {
		JSONArray tasksJson = (JSONArray) map;
		try {
			HashMap<String, StreamTask> taskMap = new HashMap<String, StreamTask>();
			for (int i = 0; i < tasksJson.length(); i++) {
				StreamTask task = convertTask(tasksJson.getJSONObject(i));
				taskMap.put(task.getTaskName().toLowerCase(), task);
			}
			return taskMap;
		} catch (JSONException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	@Override
	public StreamTask convertTask(Object obj) throws StreamIOException {
		JSONObject taskJson = (JSONObject) obj;
		try {
			String taskName = taskJson.getString(StreamIO.KEY_NAME);
			StreamTask task = new StreamTask(taskName);
			if (taskJson.has(StreamIO.KEY_DESCRIPTION)) {
				task.setDescription(taskJson
						.getString(StreamIO.KEY_DESCRIPTION));
			}

			if (taskJson.has(StreamIO.KEY_STARTTIME)) {
				Calendar startTime = Calendar.getInstance();
				Date startTimeDate = StreamIO.dateFormat.parse(taskJson
						.getString(StreamIO.KEY_STARTTIME));
				startTime.setTime(startTimeDate);
				task.setStartTime(startTime);
			}

			if (taskJson.has(StreamIO.KEY_DEADLINE)) {
				Calendar deadline = Calendar.getInstance();
				Date deadlineDate = StreamIO.dateFormat.parse(taskJson
						.getString(StreamIO.KEY_DEADLINE));
				deadline.setTime(deadlineDate);
				task.setDeadline(deadline);
			}

			if (taskJson.has(StreamIO.KEY_TAGS)) {
				JSONArray tagsJson = taskJson.getJSONArray(StreamIO.KEY_TAGS);
				for (int i = 0; i < tagsJson.length(); i++) {
					task.addTag(tagsJson.getString(i));
				}
			}

			if (taskJson.has(StreamIO.KEY_DONE)) {
				Boolean isDone = taskJson.getBoolean(StreamIO.KEY_DONE);
				if (isDone) {
					task.markAsDone();
				}
			}

			if (taskJson.has(StreamIO.KEY_RANK)) {
				task.setRank(taskJson.getString(StreamIO.KEY_RANK));
			}
			return task;
		} catch (JSONException | ParseException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}
	
	private LoadIO() {
		
	}

	public static LoadIO init() {
		if (self == null) {
			self = new LoadIO();
		}
		return self;
	}

	/**
	 * Loads the contents in the given file and parse it into a JSONObject
	 * 
	 * @param file
	 *            source file to be loaded
	 * @return JSONObject that is parsed
	 * @throws StreamIOException
	 *             from file IO errors or when the contents could not be parsed
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	JSONObject readFromFile(File file) throws FileNotFoundException,
			IOException {
		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			String mapJsonString = stringBuilder.toString().trim();
			if (mapJsonString.isEmpty()) {
				return null;
			} else {
				return new JSONObject(mapJsonString);
			}
		}
	}

	/**
	 * Loads data from file and populate the taskMap and taskList accordingly
	 * with the data in the file
	 * 
	 * @param file
	 *            to load
	 * @param taskMap
	 *            the map to populate loaded tasks
	 * @param taskList
	 *            the list to populate loaded task names
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws StreamIOException
	 *             if the file is corrupted or the file could not be loaded.
	 */
	void load(File file, Map<String, StreamTask> taskMap, List<String> taskList)
			throws FileNotFoundException, IOException {
		JSONObject tasksJson = readFromFile(file);
		try {
			if (tasksJson != null) {
				loadTaskMap(taskMap, tasksJson);
				loadTaskList(taskList, tasksJson);
			}
		} catch (StreamIOException e) {
			throw new JSONException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage());
		}
	}

	/**
	 * Populate the task list with task names from the json object given, which
	 * should contain a list of tasks data
	 * 
	 * @param taskList
	 *            the list to populate loaded task names
	 * @param tasksJson
	 *            the json containing all tasks data
	 * @throws StreamIOException
	 *             if the json could not be parsed
	 */
	void loadTaskList(List<String> taskList, JSONObject tasksJson)
			throws StreamIOException {
		try {
			JSONObject orderListJson = tasksJson
					.getJSONObject(StreamIO.KEY_TASKLIST);
			List<String> storedList = convertTaskList(orderListJson);
			taskList.addAll(storedList);
		} catch (JSONException e) {
			throw new StreamIOException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage(), e);
		}
	}

	/**
	 * Populate the task map with tasks from the json object given, which should
	 * contain a list of tasks data
	 * 
	 * @param taskMap
	 *            the map to populate with loaded task names
	 * @param tasksJson
	 *            the json containing all tasks data
	 * @throws StreamIOException
	 *             if the json could not be parsed
	 */
	void loadTaskMap(Map<String, StreamTask> taskMap, JSONObject tasksJson)
			throws StreamIOException {
		try {
			JSONArray taskMapJson = tasksJson
					.getJSONArray(StreamIO.KEY_TASKMAP);
			HashMap<String, StreamTask> storedTasks = convertTaskMap(taskMapJson);
			taskMap.putAll(storedTasks);
		} catch (JSONException e) {
			throw new StreamIOException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage(), e);
		}
	}

	/**
	 * Loads data from file in working directory, this used to be the default
	 * save location before V0.4.
	 * 
	 * @param taskMap
	 *            the map to populate loaded tasks
	 * @param taskList
	 *            the list to populate loaded task names
	 * @throws StreamIOException
	 *             if the file is corrupted or the file could not be loaded.
	 * @deprecated
	 */
	Boolean loadLegacyStorage(Map<String, StreamTask> taskMap,
			List<String> taskList) throws StreamIOException {
		/*
		 * File streamFile = new File(StreamIO.STREAM_FILENAME); if
		 * (streamFile.exists()) { // load(streamFile, taskMap, taskList);
		 * streamFile.delete(); return true; } else { return false; }
		 */
		return null;
	}

}