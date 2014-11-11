package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import exception.StreamParserException;

//@author A0119401U

//Note: for test not focusing on the range problem (number of tasks type)
//remember to set the second param of interpretCommand as a reasonable positive integer

public class StreamParserTest {

	StreamParser stparser;
	
	@Before
	public void setUp() throws Exception {
		stparser = new StreamParser();
	}

	@Test
	public void parserAddTest() {
		
		try{
			
			stparser.interpretCommand("add ", 0);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INCOMPLETE_INPUT;
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserDescTest() {
		
		try{
			stparser.interpretCommand("desc -1 ok", 1);
			fail();
		}catch (Exception e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_INDEX;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("desc newcq ok", 3);
			fail();
		}catch (Exception e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_INDEX;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("desc 50 ok", 45);
			fail();
		}catch (Exception e) {
			final String expectedMessage = StreamParser.ERROR_INDEX_OUT_OF_BOUNDS;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("desc 1", 2);
			fail();
		}catch (Exception e) {
			final String expectedMessage = StreamParser.ERROR_INCOMPLETE_INPUT;
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserDelTest() {
		
		try{
			
			stparser.interpretCommand("del ", 1);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INCOMPLETE_INDEX;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			
			stparser.interpretCommand("del as", 5);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_INDEX;
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserMarkTest() {
		
		try{
			
			stparser.interpretCommand("done tutorial", 3);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_UNKNOWN_COMMAND;
			System.out.println(expectedMessage);
			System.out.println(e.getMessage());
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserRankTest() {
		
		try{
			
			stparser.interpretCommand("rank as high", 5);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_INDEX;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		//This test and the following one demonstrate the ranking type and 
		//the boundary: you cannot modify a task whose index is not real
		try{
			
			stparser.interpretCommand("rank 1 small", 1);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_RANK;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("rank 1 high",0);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INDEX_OUT_OF_BOUNDS;
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	
	@Test
	public void parserTagTest() {
		
		try{
			stparser.interpretCommand("tag 1", 3);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INCOMPLETE_INPUT;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("tag 80 home", 78);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INDEX_OUT_OF_BOUNDS;
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	
	@Test
	public void parserFilterTest() {
		
		try{
			stparser.interpretCommand("filter", 5);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INCOMPLETE_INPUT;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("filter rank no", 3);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_FILTER;
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	
	@Test
	public void parserSortTest() {
		
		try{
			stparser.interpretCommand("sort command", 3);
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_SORT;
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	

}
