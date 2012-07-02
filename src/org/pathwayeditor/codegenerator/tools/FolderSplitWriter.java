package org.pathwayeditor.codegenerator.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * Class will write several files to the folder. It write everything into the
 * string buffer and then split it when closed. Place to split is defined by
 * beginning-of-file marker, followed by name of the file to be created.
 * 
 * <br>
 * $Id: FolderSplitWriter.java 4715 2009-03-21 20:38:50Z smoodie $
 * 
 * @author Anatoly Sorokin
 * @date 5 Jun 2008
 * 
 */
public class FolderSplitWriter extends StringWriter {

	public static final String DEFAULT_BOF = "###";
	public static final String FILE_NAME = ".*?\r?\n";
	private File dir = new File("./");
	private String bof = DEFAULT_BOF;

	public FolderSplitWriter() {
		super();
	}

	public FolderSplitWriter(int initialSize) {
		super(initialSize);
	}

	public FolderSplitWriter(File target) {
		this.dir = target;
	}

	public FolderSplitWriter(String target) {
		this.dir = new File(target);
	}

	/**
	 * 
	 * @see java.io.StringWriter#close()
	 */
	@Override
	public void close() throws IOException {
		flush();
		writeFiles();
	}

	/**
	 * Splits input into chunks according to delimeter patterns and save chunks
	 * to file, which name is encoded in delimeter string.<br>
	 * 
	 * Preconditions:<br>
	 * <ul>
	 * <li>Input string do not contains delimeter lines, then:
	 * <ul>
	 * <li>target does not exits, or</li>
	 * <li>target is writabe file</li>
	 * </ul>
	 * </li>
	 * <li>Input string contains delimeter lines then target is directory and
	 * it is writable</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there are problems with file writing
	 * @throws IllegalArgumentException
	 *             if preconditions are not met
	 * 
	 */
	private void writeFiles() throws IOException {
		// Set to false if only the tokens that match the pattern are to be
		// returned.
		// If true, the text between matching tokens are also returned.
		boolean returnDelims = true;
		FileWriter fw = null;
		// Create the tokenizer
		Iterator<String> tokenizer = new RETokenizer(getBuffer().toString(),
				getBof() + FILE_NAME, returnDelims);
		// Get the tokens (and delimiters)
		for (; tokenizer.hasNext();) {
			try {
				if (((RETokenizer) tokenizer).isNextToken()) {
					// there is no delimeters in the file
					// we will try write it to the dir file
					if (dir.exists() && dir.isDirectory())
						throw new IllegalArgumentException(
								"Imput contains no file names and target exists and directory:"
										+ dir.getCanonicalPath());
					fw = writeContent(tokenizer, dir);
				} else {
					String fname = tokenizer.next();
					fname = fname.replace(getBof(), "").trim();
					File f = new File(dir, fname);
					File dir = new File(f.getParent());
//					System.out.println(f.getParent() + ";" + dir.exists());
					if (tokenizer.hasNext()) {
						dir.mkdirs();
						if (dir.exists()) {
//							System.out.println(f.getAbsolutePath());
							fw = writeContent(tokenizer, f);
						} else {
//							System.out.println(dir.getAbsolutePath());
						}
					}
				}
			} catch (IOException e) {
				throw e;
			} finally {
				if (fw != null)
					fw.close();
			}
		}

	}

	private FileWriter writeContent(Iterator<String> tokenizer, File f)
			throws IOException {
		FileWriter fw;
		fw = new FileWriter(f);
		String content = tokenizer.next();
		fw.write(content);
		return fw;
	}

	File getDir() {
		return dir;
	}

	String getBof() {
		return bof;
	}

	void setBof(String bof) {
		this.bof = bof;
	}

}

/*
 * $Log$
 * Revision 1.1  2008/06/06 12:56:47  asorokin
 * New Writer
 *
 */