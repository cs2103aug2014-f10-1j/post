package logic;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import model.StreamObject;
import model.StreamTask;

import org.junit.Before;
import org.junit.Test;

import exception.StreamModificationException;

//@author A0096529N
public class StreamModificationTest {
	private StreamObject stobj = StreamObject.init();
	private CRDLogic crdLogic = CRDLogic.init(stobj);
	private ModificationLogic modLogic = ModificationLogic.init(crdLogic);

	private StreamTask task1, task2, task3;
	private Calendar taskDeadline;
	private String TASK_NAME_1 = "Find X";
	private String TASK_NAME_2 = "Find Pandora's Box";
	private String TASK_NAME_3 = "Code unit tests";
	
	@Before 
	public void setUp() throws Exception {
		taskDeadline = Calendar.getInstance();
		taskDeadline.set(2014, 9, 17, 18, 48, 45);
		
		Calendar task1Deadline = Calendar.getInstance();
		task1Deadline.setTime(taskDeadline.getTime());
		task1 = new StreamTask(TASK_NAME_1);
		task1.setDescription("If a = b and b = c, find x.");
		task1.setDeadline(task1Deadline);
		task1.getTags().add("X");
		task1.getTags().add("FIND");
		task1.getTags().add("MATH");
		task1.getTags().add("SIMPLE");
		crdLogic.addTask(task1);

		Calendar task2Deadline = Calendar.getInstance();
		task2Deadline.setTime(taskDeadline.getTime());
		task2 = new StreamTask(TASK_NAME_2);
		task2.setDescription("Try and search around the bamboo forest...");
		task2.setDeadline(task2Deadline);
		task2.getTags().add("IMPOSSIBLE");
		task2.getTags().add("PANDA");
		task2.getTags().add("NOLINE");
		crdLogic.addTask(task2);
		
		task3 = new StreamTask(TASK_NAME_3);
		task3.setDescription("Code the unit tests for StreamObject");
		task3.getTags().add("BORINGTASK");
		task3.getTags().add("PROCRASTINATE");
		crdLogic.addTask(task3);
	}

	@Test 
	public void testChangeDeadline() throws StreamModificationException {
		assertEquals("Deadline before modification", 
				toDateString(taskDeadline), toDateString(task1.getDeadline()));
		
		Calendar task1Deadline = Calendar.getInstance();
		task1Deadline.set(2014, 9, 17, 18, 48, 45);
		task1.setDeadline(task1Deadline);

		assertEquals("Deadline after modification", 
				toDateString(task1Deadline), toDateString(task1.getDeadline()));
	}

	@Test 
	public void testRemoveTag() throws StreamModificationException {
		assertEquals("Tags before modification", true, task3.hasTag("procrastinate"));

		modLogic.removeTags(task3, "procrastinate");

		assertEquals("Tags after modification", false, task3.hasTag("procrastinate"));
	}

	@Test 
	public void testAddTags() throws StreamModificationException {
		assertEquals("Tags before modification", false, task3.hasTag("tagtobeadded"));

		task3.getTags().add("TAGTOBEADDED");

		assertEquals("Tags after modification", true, task3.hasTag("tagtobeadded"));
	}

	@Test 
	public void testUpdateTaskName() throws StreamModificationException {
		String newTaskName = "New task name";
		
		assertEquals("Task name before modification", TASK_NAME_3, task3.getTaskName());

		modLogic.setName(task3, newTaskName);

		assertEquals("Task name after modification", newTaskName, task3.getTaskName());
	}

	@Test 
	public void testMarkTaskAsDone() throws StreamModificationException {
		task1.markAsOngoing();
		assertEquals("Done before modification", false, task2.isDone());

		task2.markAsDone();

		assertEquals("Done after modification", true, task2.isDone());
	}

	@Test 
	public void testMarkTaskAsOngoing() throws StreamModificationException {
		task1.markAsDone();
		assertEquals("Done before modification", true, task1.isDone());

		task1.markAsOngoing();

		assertEquals("Done after modification", false, task1.isDone());
	}

	@Test 
	public void testSetDueTime() throws StreamModificationException {
		assertEquals("Deadline before modification", null, 
				task3.getDeadline());

		Calendar task3Deadline = Calendar.getInstance();
		task3Deadline.setTime(taskDeadline.getTime());
		task3.setDeadline(task3Deadline);

		assertEquals("Deadline after modification", 
				toDateString(taskDeadline), toDateString(task2.getDeadline()));
	}

	@Test 
	public void testSetNullDeadline() throws StreamModificationException {
		assertEquals("Deadline before modification", 
				toDateString(taskDeadline), toDateString(task2.getDeadline()));
		
		task2.setDeadline(null);

		assertEquals("Deadline after modification", 
				null, task2.getDeadline());
	}


	private String toDateString(Calendar taskDeadline) {
		return taskDeadline.getTime().toString();
	}
}
