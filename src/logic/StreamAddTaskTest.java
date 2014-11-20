package logic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import model.StreamObject;
import model.StreamTask;

import org.junit.Before;
import org.junit.Test;

import exception.StreamModificationException;

//@author A0118007R
public class StreamAddTaskTest {

	private StreamLogic streamLogic;

	@Before
	public void setup() throws Exception {
		streamLogic = StreamLogic.init(StreamObject.init());
		streamLogic.crdLogic.addTask("Submit CE2");
		streamLogic.crdLogic.addTask("Study for midterms");
	}
	
	@Test
	public void addTaskTest1() {
		assertEquals("Submit CE2 is added to the list.", true,
				streamLogic.crdLogic.hasTask("Submit CE2"));
	}
	@Test
	public void addTaskTest2() {
		assertEquals("Study for midterms is added to the list.", true,
				streamLogic.crdLogic.hasTask("Study for midterms"));
	}
	@Test
	public void addTaskTest3() {
		assertEquals("Watch Rurouni Kenshin is not added to the list.", false,
				streamLogic.crdLogic.hasTask("Watch Rurouni Kenshin"));
	}
	@Test
	public void addTaskTest4() {
		try {
			streamLogic.crdLogic.addTask("Submit CE2");
		} catch (Exception e) {
			assertEquals("Exception should be generated.",
					"\"Submit CE2\" already exists in the tasks list.",
					e.getMessage());
		}
	}
	
	@Test
	public void deleteTest() throws Exception {
		streamLogic.crdLogic.deleteTask("Submit CE2");
		assertEquals(streamLogic.crdLogic.hasTask("Submit CE2"), false);
	}
	
	//"Boundary" cases for get task
	


	@Before
	public void resetup() throws Exception {
		streamLogic = StreamLogic.init(StreamObject.init());
		streamLogic.crdLogic.addTask("Submit CE2");
		streamLogic.crdLogic.addTask("Study for midterms");
	}
	
	@Test 
	public void getTaskTestOne() throws StreamModificationException{
		StreamTask myTask = streamLogic.crdLogic.getTask("Submit CE2");
		assertTrue(myTask.getTaskName().equals("Submit CE2"));
	}
	
	@Test 
	public void getTaskTestTwo() {
		try {
			streamLogic.crdLogic.getTask("AAA");
			fail("A test message");
		} catch (StreamModificationException e) {
			
		}
		
	}
	
	
	
}
