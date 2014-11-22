package stream;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//@author A0096529N

public class StreamSaveTest {
	private static final String TEST_SAVE_FILENAME = "streamtest"
			+ Stream.SAVEFILE_EXTENSION;

	private File testFile;
	private Stream stream = new Stream(TEST_SAVE_FILENAME);

	@Before
	public void setUp() throws Exception {
		testFile = new File(stream.stio.getSaveLocation());

		if (testFile.exists()) {
			testFile.delete();
		}

		stream = new Stream(testFile.getName());
		stream.filterAndProcessInput("add Code Jarvis");
		stream.filterAndProcessInput("due 1 19 july 2041 00:00:00");
		stream.filterAndProcessInput("desc 1 Just\na\nRather\nVery\nIntelligent\nSystem");
		stream.filterAndProcessInput("tag 1 epic impossible");

		stream.filterAndProcessInput("add Build IoT -due 1 january 2018 12:34:56 "
				+ "-desc Internet of Things -tag epic popular urgent");
	}

	@After
	public void tearDown() throws Exception {
		testFile.delete();
	}

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
