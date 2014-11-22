package logic;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.StreamObject;
import model.StreamTask;

import org.junit.Before;
import org.junit.Test;

//@author A0096529N
public class StreamSearchTest {

	private StreamObject stobj = StreamObject.init();
	private CRDLogic crdLogic = CRDLogic.init(stobj);
	private StreamTask task1, task2;
	private static final Comparator<StreamTask> taskComparator = new Comparator<StreamTask>() {
		@Override public int compare(StreamTask o1, StreamTask o2) {
			return o1.getTaskName().compareTo(o2.getTaskName());
		}
	};; 

	@Before 
	public void setUp() throws Exception {
		crdLogic.addTask("Find X");
		task1 = crdLogic.getTask("Find X");
		task1.setDescription("If a = b and b = c, find x.");
		task1.getTags().add("X");
		task1.getTags().add("FIND");
		task1.getTags().add("MATH");
		task1.getTags().add("SIMPLE");
		crdLogic.addTask("Find Pandora's Box");
		task2 = crdLogic.getTask("Find Pandora's Box");
		task2.setDescription("Try and search around the bamboo forest...");
		task2.getTags().add("IMPOSSIBLE");
		task2.getTags().add("PANDA");
		task2.getTags().add("NOLINE");
	}
	// TODO think on how to implement this...

	@Test 
	public void testSearch1() {
		testOneSearch("Search for nothing", stobj.getStreamTaskList(crdLogic.findTasks("nothing")));
	}
	@Test 
	public void testSearch2() {
		testOneSearch("Search for x", stobj.getStreamTaskList(crdLogic.findTasks("x")), task1, task2);
	}
	@Test 
	public void testSearch3() {
		testOneSearch("Search for panda", stobj.getStreamTaskList(crdLogic.findTasks("im looking for a panda")), task2);
	}

	private void testOneSearch(String testMessage, List<StreamTask> actualTasks, StreamTask...tasks) {
		List<StreamTask> expectedTasks = Arrays.asList(tasks);
		Collections.sort(expectedTasks, taskComparator);
		Collections.sort(actualTasks, taskComparator);
		assertEquals(testMessage, expectedTasks, actualTasks);
	}
}
