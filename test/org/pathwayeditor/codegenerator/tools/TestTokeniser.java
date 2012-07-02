package org.pathwayeditor.codegenerator.tools;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.regex.PatternSyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.pathwayeditor.codegenerator.tools.RETokenizer;

public class TestTokeniser {

	private static final String TEST_STRING = "###File1\nTestString1\n###File2\nTestString2\n###File3\nTestString3";
	RETokenizer tokeniser;
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=PatternSyntaxException.class)
	public void testRETokenizer() {
		tokeniser=new RETokenizer(TEST_STRING,"((])",true);
	}

	@Ignore
	@Test
	public void testHasNext() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testNext() {
		tokeniser=new RETokenizer(TEST_STRING,"###.*?\n",true);
		assertTrue("First call of hasNext",tokeniser.hasNext());
		assertFalse("First call of isNextToken",tokeniser.isNextToken());
		String token=tokeniser.next();
		assertEquals("First delimeter","###File1\n" ,token);
		assertTrue("Second call of hasNext",tokeniser.hasNext());
		assertTrue("Secobd call of isNextToken",tokeniser.isNextToken());
		token=tokeniser.next();
		assertEquals("First token","TestString1\n" ,token);
		int i=2;
		while(tokeniser.hasNext()){
			i++;
			System.out.println(tokeniser.isNextToken());
			token=tokeniser.next();
		System.out.println(token);
		}
		assertEquals("number of parts",6,i);
	}
	
	public void testOtherDelimeter(){
		tokeniser=new RETokenizer(TEST_STRING,"%%%.*?\n",true);
		assertTrue("First call of hasNext",tokeniser.hasNext());
		assertTrue("First call of isNextToken",tokeniser.isNextToken());
		String token=tokeniser.next();
		assertEquals("First delimeter",TEST_STRING ,token);
		assertTrue("Second call of hasNext",tokeniser.hasNext());
		
	}

	@Ignore
	@Test
	public void testIsNextToken() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	@Test
	public void testRemove() {
		fail("Not yet implemented"); // TODO
	}

}


/*
 * $Log$
 * Revision 1.1  2008/06/06 12:57:01  asorokin
 * New Writer
 *
 */