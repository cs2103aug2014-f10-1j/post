package stream;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import model.StreamTask;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fileio.StreamIO;
import util.StreamConstants;

public class StreamLoadTest {
	private StreamTask task1, task2;
	private HashMap<String, StreamTask> map;
	private ArrayList<String> taskList;
	private static final String TEST_SAVE_FILENAME = "streamtest" + Stream.SAVEFILE_EXTENSION;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
			Locale.ENGLISH);
	private File testFile;

	// @author A0096529N
	@Before 
	public void setUp() throws Exception {
		task1 = new StreamTask("Code Jarvis");
		Calendar calendar = Calendar.getInstance();
		Date date = simpleDateFormat.parse("20410719000000");
		calendar.setTime(date);
		task1.setDeadline(calendar);
		task1.setDescription("Just\na\nRather\nVery\nIntelligent\nSystem");
		task1.getTags().add("EPIC");
		task1.getTags().add("IMPOSSIBLE");

		task2 = new StreamTask("Build IoT");
		Calendar calendar2 = Calendar.getInstance();
		Date date2 = simpleDateFormat.parse("20180101123456");
		calendar2.setTime(date2);
		task2.setDeadline(calendar2);
		task2.setDescription("Internet of Things");
		task2.getTags().add("EPIC");
		task2.getTags().add("POPULAR");
		task2.getTags().add("URGENT");

		map = new HashMap<String, StreamTask>();
		map.put(task1.getTaskName().toLowerCase(), task1);
		map.put(task2.getTaskName().toLowerCase(), task2);

		taskList = new ArrayList<String>();
		taskList.add(task1.getTaskName());
		taskList.add(task2.getTaskName());

		String fileContent = "{\"taskList\":{\"1\":\"Build IoT\",\"0\":\"Code Jarvis\"},"
				+ "\"allTasks\":[{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"deadline\":\"20410719000000\","
				+ "\"taskName\":\"Code Jarvis\","
				+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"},"
				+ "{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"deadline\":\"20180101123456\","
				+ "\"taskName\":\"Build IoT\"," + "\"taskDescription\":\"Internet of Things\"}]}";
		try {
			StreamIO.setFilename(TEST_SAVE_FILENAME);
			testFile = new File(StreamIO.getSaveLocation());
			
			if (testFile.exists()) {
				testFile.delete();
			}
			stringToFile(testFile, fileContent);
		} catch (IOException e) {
			throw new IOException(String.format(StreamConstants.ExceptionMessage.ERR_CREATEFILE, e.getMessage()), e);
		}
	}

	// @author A0096529N
	@After
	public void tearDown() throws Exception {
		testFile.delete();
	}

	// @author A0096529N
	@Test 
	public void testLoadMap() {
		Stream stream = new Stream(testFile.getName());
		assertEquals("Loaded task map", serializeTaskMap(map),
				serializeTaskMap(stream.streamLogic.getTaskMap()));
	}

	// @author A0096529N
	@Test 
	public void testLoadList() {
		Stream stream = new Stream(testFile.getName());
		assertEquals("Loaded task map", taskList, stream.streamLogic.getTaskList());
	}

	// @author A0096529N
	private void stringToFile(File destin, String content) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(destin)) {
			if (destin.exists()) {
				destin.delete();
			}
			fos.write(content.getBytes());
		}
	}

	// @author A0096529N
	private String serializeTaskMap(HashMap<String, StreamTask> taskMap) {
		JSONObject taskMapJson = new JSONObject(taskMap);
		return taskMapJson.toString();
	}
}
