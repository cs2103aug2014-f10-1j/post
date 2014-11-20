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
			
			stparser.parseCommand("add ", 0);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INCOMPLETE_INPUT;
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserDescTest() {
		
		try{
			stparser.parseCommand("desc -1 ok", 1);
			fail();
		}catch (Exception e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_INDEX;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.parseCommand("desc newcq ok", 3);
			fail();
		}catch (Exception e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_INDEX;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.parseCommand("desc 50 ok", 45);
			fail();
		}catch (Exception e) {
			final String expectedMessage = StreamParser.ERROR_INDEX_OUT_OF_BOUNDS;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.parseCommand("desc 1", 2);
			fail();
		}catch (Exception e) {
			final String expectedMessage = StreamParser.ERROR_INCOMPLETE_INPUT;
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserDelTest() {
		
		try{
			
			stparser.parseCommand("del ", 1);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INCOMPLETE_INDEX;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			
			stparser.parseCommand("del as", 5);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_INDEX;
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserMarkTest() {
		
		try{
			
			stparser.parseCommand("done tutorial", 3);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_UNKNOWN_COMMAND;
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserRankTest() {
		
		try{
			
			stparser.parseCommand("rank as high", 5);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_INDEX;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		//This test and the following one demonstrate the ranking type and 
		//the boundary: you cannot modify a task whose index is not real
		try{
			
			stparser.parseCommand("rank 1 small", 1);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_RANK;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.parseCommand("rank 1 high",0);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INDEX_OUT_OF_BOUNDS;
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	
	@Test
	public void parserTagTest() {
		
		try{
			stparser.parseCommand("tag 1", 3);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INCOMPLETE_INPUT;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.parseCommand("tag 80 home", 78);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INDEX_OUT_OF_BOUNDS;
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	
	@Test
	public void parserFilterTest() {
		
		try{
			stparser.parseCommand("filter", 5);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INCOMPLETE_INPUT;
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.parseCommand("filter rank no", 3);
			fail();
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_FILTER;
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	
	@Test
	public void parserSortTest() {
		
		try{
			stparser.parseCommand("sort command", 3);
		}catch (StreamParserException e) {
			final String expectedMessage = StreamParser.ERROR_INVALID_SORT;
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	

}
