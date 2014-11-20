package stream;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import model.StreamTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fileio.StreamIO;

public class StreamSaveTest {
	private StreamTask task1, task2;
	private HashMap<String, StreamTask> map;
	private ArrayList<String> taskList;
	private static final String TEST_SAVE_FILENAME = "streamtest"
			+ Stream.SAVEFILE_EXTENSION;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.ENGLISH);
	private File testFile;
	private Stream stream;

	//@author A0096529N
	@Before
	public void setUp() throws Exception {
		StreamIO.setFilename(TEST_SAVE_FILENAME);
		testFile = new File(StreamIO.getSaveLocation());

		if (testFile.exists()) {
			testFile.delete();
		}

		stream = new Stream(testFile.getName());
		String taskName1 = "Code Jarvis";
		stream.streamLogic.crdLogic.addTask(taskName1);
		task1 = stream.streamLogic.crdLogic.getTask(taskName1);
		Calendar calendar = Calendar.getInstance();
		Date date = simpleDateFormat.parse("20410719000000");
		calendar.setTime(date);
		task1.setDeadline(calendar);
		task1.setDescription("Just\na\nRather\nVery\nIntelligent\nSystem");
		task1.getTags().add("EPIC");
		task1.getTags().add("IMPOSSIBLE");

		String taskName2 = "Build IoT";
		stream.streamLogic.crdLogic.addTask(taskName2);
		task2 = stream.streamLogic.crdLogic.getTask(taskName2);
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
	}

	//@author A0096529N
	@After
	public void tearDown() throws Exception {
		testFile.delete();
	}

	//@author A0096529N
	@Test
	public void testSave() throws IOException {
		stream.save();

		String expectedContent = "{\"taskList\":{\"1\":\"Build IoT\",\"0\":\"Code Jarvis\"},"
				+ "\"allTasks\":[{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"rank\":\"low\",\"done\":false,\"deadline\":\"20410719000000\","
				+ "\"taskName\":\"Code Jarvis\","
				+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"},"
				+ "{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"rank\":\"low\",\"done\":false,\"deadline\":\"20180101123456\","
				+ "\"taskName\":\"Build IoT\","
				+ "\"taskDescription\":\"Internet of Things\"}]}";
		assertEquals("Saved state", expectedContent, fileToString(testFile));
	}

	//@author A0096529N
	private String fileToString(File file) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			return stringBuilder.toString().trim();
		}
	}
}
