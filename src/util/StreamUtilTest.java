package util;

//@author A0118007R
// many tests are obsolete now

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("deprecation")
public class StreamUtilTest {
	
	//Tests for display description
	/*
	@Test //null input value
	public void displayDescriptionTest1() {
		String description = StreamUtil.displayDescription(null);
		assertEquals("no description provided", description);
	}
	
	@Test //some random input, already covers boundary cases / partitioning
	public void displayDescriptionTest2() {
		String description = StreamUtil.displayDescription("task description");
		assertEquals("task description", description);
	}
	
	//Tests for displayStatus
	@Test
	public void displayStatusTest1() {
		StreamTask myTask = new StreamTask("my task");
		myTask.markAsDone();
		String status = StreamUtil.displayStatus(myTask);
		assertEquals(status, "done");
	}
	
	@Test
	public void displayStatusTest2() {
		StreamTask myTask = new StreamTask("my task");
		String status = StreamUtil.displayStatus(myTask);
		assertEquals(status, "ongoing");
	}
	*/
	//Tests parseWithChronic
	/*
	@Test
	public void parseWithChronicTest1() {
		String date = "now";
		Calendar now = Calendar.getInstance();
		String parsedDate = StreamUtil.parseWithChronic(date);
		Calendar parsedCalendar = StreamUtil.parseCalendar(parsedDate);
		String nowCalendar = StreamUtil.getCalendarWriteUp(now);
		String calendarWriteUp = StreamUtil.getCalendarWriteUp(parsedCalendar);
		assertEquals(nowCalendar, calendarWriteUp);
	}*/
	
	//Tests getMonthIndex
	@Test
	public void getMonthIndexTest1() {
		String month = "January";
		assertEquals(1, StreamUtil.getMonthIndex(month));
	}
	
	@Test
	public void getMonthIndexTest2() {
		String month = "Some weird month";
		assertEquals(0, StreamUtil.getMonthIndex(month));
	}
	
	@Test
	public void getMonthIndexTest3() {
		String month = "5";
		assertEquals(0, StreamUtil.getMonthIndex(month));
	}
	
	//Tests isValidAttribute
	@Test
	public void isValidAttributeTest1() {
		String att = "-desc";
		assertTrue(StreamUtil.isValidAttribute(att));
	}
	
	@Test
	public void isValidAttributeTest2() {
		String att = "-due";
		assertTrue(StreamUtil.isValidAttribute(att));
	}
	
	@Test
	public void isValidAttributeTest3() {
		String att = "-name";
		assertTrue(StreamUtil.isValidAttribute(att));
	}
	
	@Test
	public void isValidAttributeTest4() {
		String att = "-weirdAttribute";
		assertFalse(StreamUtil.isValidAttribute(att));
	}
	
	@Test
	public void isValidAttributeTest5() {
		String att = null;
		assertFalse(StreamUtil.isValidAttribute(att));
	}
	
	
}
