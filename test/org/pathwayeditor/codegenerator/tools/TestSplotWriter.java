package org.pathwayeditor.codegenerator.tools;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pathwayeditor.codegenerator.tools.FolderSplitWriter;

public class TestSplotWriter {
	private static final String TEST_STRING = "###File1\nTestString1\n###Folder1/File2\nTestString2\n###Folder1/Folder2/File3\nTestString3";

	private FolderSplitWriter fsw; 
	@Before
	public void setUp() throws Exception {
		File f=new File("File1");
		f.delete();
		f=new File("Folder1/Folder2/File3");
		f.delete();
		f=new File("Folder1/Folder2");
		f.delete();
		f=new File("Folder1/File2");
		f.delete();
		f=new File("Folder1");
		f.delete();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test(expected=IllegalArgumentException.class)
	public void testCloseIllegalArgumentExceptionNoDelimDirTarget() throws IOException {
		fsw=new FolderSplitWriter();
		fsw.write("Wrong string");
		fsw.close();
	}
	
	public void testCloseNoDelimFileTarget() throws IOException {
		String fname = "File1";
		fsw=new FolderSplitWriter(fname);
		fsw.write("Wrong string");
		fsw.close();
		File f=new File(fname);
		assertTrue("First file exists",f.exists());
		f.deleteOnExit();
	}	
	
	@Test
	public void testClose() throws IOException {
		fsw=new FolderSplitWriter();
		fsw.write(TEST_STRING);
		fsw.close();
		File f=new File("File1");
		assertTrue("First file exists",f.exists());
//		f.deleteOnExit();
		f=new File("Folder1/File2");
		assertTrue("Second file exists",f.exists());
//		f.deleteOnExit();
		f=new File("Folder1/Folder2/File3");
		assertTrue("Third file exists",f.exists());
//		f.deleteOnExit();
	}

}


/*
 * $Log$
 * Revision 1.1  2008/06/06 12:57:01  asorokin
 * New Writer
 *
 */