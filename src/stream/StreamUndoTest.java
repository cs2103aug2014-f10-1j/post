package stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import parser.StreamParser;
import fileio.StreamIO;

// @author A0093874N

/**
 * Most tests here are based on the "combining multiple inputs" heuristic.
 * 
 * "Equivalence partitioning" can also be seen for null and non-null field
 * modification (see nullModificationTest).
 * 
 * Each test has more than one asserts because separating into multiple tests
 * will be very, very cumbersome.
 */
public class StreamUndoTest {

	private static Stream st;
	private static final String TEST_SAVE_FILENAME = "streamtest"
			+ Stream.SAVEFILE_EXTENSION;
	private File testFile;

	@Before
	public void setUp() throws Exception {
		StreamIO.setFilename(TEST_SAVE_FILENAME);
		testFile = new File(StreamIO.getSaveLocation());

		if (testFile.exists()) {
			testFile.delete();
		}
		st = new Stream(TEST_SAVE_FILENAME);
	}

	@After
	public void tearDown() throws Exception {
		testFile.delete();
	}

	// for extreme convenience

	public void in(String input) {
		st.filterAndProcessInput(input);
	}

	public Boolean compare(ArrayList<String> actual, String[] expected) {
		if (actual.size() != expected.length) {
			return false;
		} else {
			for (int i = 0; i < actual.size(); i++) {
				if (!actual.get(i).equals(expected[i])) {
					return false;
				}
			}
			return true;
		}
	}

	@Test
	public void undoAddTest() throws Exception {
		in("add do CS2103");
		assertTrue("do CS2103 is included", st.streamLogic.crdLogic.hasTask("do CS2103"));
		in("add ");
		assertEquals("no new added task", 1, st.streamLogic.getNumberOfTasks());
		in("undo");
		assertFalse("do CS2103 is not included",
				st.streamLogic.crdLogic.hasTask("do CS2103"));
	}

	@Test
	public void undoRemoveTest() throws Exception {
		in("add do CS2103");
		in("delete 1");
		in("delete 3");
		in("delete -1");
		in("delete ");
		assertFalse("do CS2103 is not included",
				st.streamLogic.crdLogic.hasTask("do CS2103"));
		in("undo");
		assertTrue("do CS2103 is included", st.streamLogic.crdLogic.hasTask("do CS2103"));
	}

	@Test
	public void undoRenameTest() throws Exception {
		in("add do CS2103");
		in("name 1 do CS2104");
		assertTrue("do CS2104 is included", st.streamLogic.crdLogic.hasTask("do CS2104"));
		in("name 1 ");
		assertTrue("name doesn't change", st.streamLogic.crdLogic.hasTask("do CS2104"));
		in("undo");
		assertTrue("do CS2103 is included", st.streamLogic.crdLogic.hasTask("do CS2103"));
	}

	@Test
	public void undoClearTest() throws Exception {
		in("add do CS2103");
		in("add do CS2104");
		in("add do CS2105");
		in("add do CS2106");
		in("add do CS2107");
		in("clear");
		assertEquals("no tasks added", 0, st.streamLogic.getNumberOfTasks());
		in("undo");
		assertEquals("5 tasks added", 5, st.streamLogic.getNumberOfTasks());
	}

	@Test
	public void undoModifyTest() throws Exception {
		String taskNameForTest = "a task";
		String newTaskName = "another task";
		in("add " + taskNameForTest);
		in("modify 1 -name " + newTaskName
				+ " -tag fordemo v0.2 -desc multiple inputs");
		assertEquals("new name is \"" + newTaskName + "\"", false,
				st.streamLogic.crdLogic.hasTask(taskNameForTest));
		assertEquals("has description", "multiple inputs", st.streamLogic
				.crdLogic.getTask(newTaskName).getDescription());
		assertFalse("has tags", st.streamLogic.crdLogic.getTask(newTaskName).getTags()
				.isEmpty());
		in("undo");
		assertEquals("old name is \"" + taskNameForTest + "\"", true,
				st.streamLogic.crdLogic.hasTask(taskNameForTest));
		assertNull("no description", st.streamLogic.crdLogic.getTask(taskNameForTest)
				.getDescription());
		assertTrue("no tags", st.streamLogic.crdLogic.getTask(taskNameForTest).getTags()
				.isEmpty());
		in("undo");
		assertFalse("no task added", st.streamLogic.crdLogic.hasTask(taskNameForTest));
	}

	@Test
	public void nullModificationTest() throws Exception {
		String taskNameForTest = "a task";
		in("add " + taskNameForTest);
		in("modify 1 -due 11/11 -desc multiple inputs");
		assertEquals(
				"has date 11/11",
				"11 November 2014 12:00:00",
				StreamParser.tp.translate(st.streamLogic.crdLogic.getTask(
						taskNameForTest).getDeadline()));
		assertEquals("has description", "multiple inputs", st.streamLogic
				.crdLogic.getTask(taskNameForTest).getDescription());
		in("modify 1 -due null -desc null");
		assertNull("has date 11/11", st.streamLogic.crdLogic.getTask(taskNameForTest)
				.getDeadline());
		assertNull("has description", st.streamLogic.crdLogic.getTask(taskNameForTest)
				.getDescription());
	}

	@Test
	public void undoSearchTest() throws Exception {
		// undoing search is done by invoking clrsrc - short form of clear
		// search
		in("add a task");
		in("add some task");
		in("add another task");
		in("add new task");
		in("add other task");
		assertEquals("4 tasks viewable", 5, st.stui.getNumberOfTasksStored());
		in("search new");
		assertEquals("1 task viewable", 1, st.stui.getNumberOfTasksStored());
		in("search other");
		assertEquals("2 tasks viewable", 2, st.stui.getNumberOfTasksStored());
		in("clrsrc");
		assertEquals("4 tasks viewable", 5, st.stui.getNumberOfTasksStored());
	}

	@Test
	public void undoFilterTest() throws Exception {
		in("add a task");
		in("add some task");
		in("add another task");
		in("add new task");
		in("mark 1 done");
		assertEquals("4 tasks viewable", 4, st.stui.getNumberOfTasksStored());
		in("filter done");
		assertEquals("1 task viewable", 1, st.stui.getNumberOfTasksStored());
		in("filter ongoing");
		assertEquals("3 tasks viewable", 3, st.stui.getNumberOfTasksStored());
		in("clrsrc");
		assertEquals("4 tasks viewable", 4, st.stui.getNumberOfTasksStored());
	}

	@Test
	public void undoMarkTest() throws Exception {
		in("add a task");
		assertFalse("Task 1 is not done", st.streamLogic.crdLogic.getTask("a task")
				.isDone());
		in("mark 1 done");
		assertTrue("Task 1 is done", st.streamLogic.crdLogic.getTask("a task").isDone());
		in("undo");
		assertFalse("Task 1 is not done", st.streamLogic.crdLogic.getTask("a task")
				.isDone());
	}

	@Test
	public void undoSortTest() throws Exception {
		in("add Task D -due 4/4/2015");
		in("add Task A -due 3/3/2015");
		in("add Task C -due 2/2/2015");
		in("add Task B -due 1/1/2015");
		String[] alphaSorted = { "Task A", "Task B", "Task C", "Task D" };
		String[] chronoSorted = { "Task D", "Task A", "Task C", "Task B" };
		String[] unsorted = { "Task D", "Task A", "Task C", "Task B" };
		assertTrue("Unsorted", compare(st.streamLogic.getTaskList(), unsorted));
		in("sort a asc");
		assertTrue("Sorted alphabetically",
				compare(st.streamLogic.getTaskList(), alphaSorted));
		in("sort d desc");
		assertTrue("Sorted chronologically",
				compare(st.streamLogic.getTaskList(), chronoSorted));
		in("undo");
		in("undo");
		assertTrue("Now unsorted again",
				compare(st.streamLogic.getTaskList(), unsorted));
	}

	@Test
	public void undoTagTest() throws Exception {
		in("add a task");
		in("tag 1 sometask randomtask");
		assertTrue("Tag sometask exists", st.streamLogic.crdLogic.getTask("a task")
				.hasTag("sometask"));
		assertTrue("Tag randomtask exists", st.streamLogic.crdLogic.getTask("a task")
				.hasTag("randomtask"));
		in("tag 1 sometask newtask");
		assertTrue("Tag newtask exists", st.streamLogic.crdLogic.getTask("a task")
				.hasTag("newtask"));
		in("undo");
		assertTrue("Tag sometask still exists", st.streamLogic
				.crdLogic.getTask("a task").hasTag("sometask"));
		assertFalse("Tag newtask no longer exists",
				st.streamLogic.crdLogic.getTask("a task").hasTag("newtask"));
		in("untag 1 randomtask newtask");
		assertFalse("Tag randomtask no longer exists",
				st.streamLogic.crdLogic.getTask("a task").hasTag("randomtask"));
		in("undo");
		assertTrue("Tag randomtask exists again",
				st.streamLogic.crdLogic.getTask("a task").hasTag("randomtask"));
		assertFalse("Tag newtask still not exist",
				st.streamLogic.crdLogic.getTask("a task").hasTag("newtask"));
		in("undo");
		assertFalse("Tag randomtask no longer exists",
				st.streamLogic.crdLogic.getTask("a task").hasTag("randomtask"));
		assertFalse("Tag sometask no longer exists",
				st.streamLogic.crdLogic.getTask("a task").hasTag("sometask"));
	}

	@Test
	public void undoRankTest() throws Exception {
		in("add a task");
		in("rank 1 wat");
		in("rank 1 high");
		assertEquals("high", st.streamLogic.crdLogic.getTask("a task").getRank());
		in("rank 1 ");
		in("rank 1 m");
		assertEquals("medium", st.streamLogic.crdLogic.getTask("a task").getRank());
		in("undo");
		assertEquals("high", st.streamLogic.crdLogic.getTask("a task").getRank());
		in("undo");
		assertEquals("low", st.streamLogic.crdLogic.getTask("a task").getRank());
	}

	@Test
	public void undoDescTest() throws Exception {
		in("add a task");
		in("desc 1 a description");
		assertEquals("description updated", "a description", st.streamLogic
				.crdLogic.getTask("a task").getDescription());
		in("desc 1 ");
		assertEquals("description remains", "a description", st.streamLogic
				.crdLogic.getTask("a task").getDescription());
		in("undo");
		assertEquals("no description", null, st.streamLogic.crdLogic.getTask("a task")
				.getDescription());
	}

	// TODO add multi-modify or multi-add tests where some of the parameters are
	// invalid
}