package org.pathwayeditor.codegenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.stringtemplate.AutoIndentWriter;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.StringTemplateWriter;
import org.pathwayeditor.codegenerator.ContextTreeParser.context_return;
import org.pathwayeditor.codegenerator.tools.FolderSplitWriter;

public class SyntaxDefinitionGenerator {
//	private static final String SYNTAX_DEFN_TEMPLATE = "org.pathwayeditor.codegenerator.Java.stg";
	private static final String SYNTAX_DEFN_TEMPLATE = "Java.stg";
	private static int SUCCESS = 0;
	private static int FAIL = 1;

	private File fileName = null;
	private String packageName;
	private File target = new File(".");
	private final OptionParser cmdLineParser = new OptionParser();
	// private final OptionSpec<File> fileNameOption;
	private final OptionSpec<File> targetDirOption;
	private final OptionSpec<String> packageNameOption;
	private final OptionSpec<Void> helpOption;
	private int exitStatus = FAIL;
	private boolean areParamatersValid;
	private static final String USAGE = "program <options> <spec file>";
	private static final int EXPECTED_NUM_SPEC_FILES = 1;
	private static final Pattern VALID_PACKAGE_REGEX = Pattern
			.compile("\\w((\\.|\\w)*\\w)?");
	private static final List<String> TARGET_DIR_OPTIONS = Arrays.asList("t", "targetDir");
	private static final List<String> PACKAGE_OPTIONS = Arrays.asList("p", "package");
	private static final List<String> HELP_OPTIONS = Arrays.asList("?", "h", "help");
	private CommonTokenStream tokens;
	private ContextTreeParser parser;
	private CommonTreeNodeStream nodes;

	public SyntaxDefinitionGenerator() {
		// this.fileNameOption = cmdLineParser.accepts("f",
		// "The specification file to read").withRequiredArg().ofType(File.class).describedAs("file name");
		this.cmdLineParser.posixlyCorrect(true);
		this.targetDirOption = cmdLineParser.acceptsAll(TARGET_DIR_OPTIONS, "The target directory").withRequiredArg().ofType(File.class)
				.describedAs("directory name");
		this.packageNameOption = this.cmdLineParser.acceptsAll(PACKAGE_OPTIONS, "The package name for the new Context Adapter")
				.withRequiredArg().ofType(String.class).describedAs("package name");
		this.helpOption = this.cmdLineParser.acceptsAll(HELP_OPTIONS, "Display command line usage");
	}

	public void runApplication(String[] args) {
		readCommandLine(args);
		if (this.fileName != null) {
			validateParameters();
			if (this.areParamatersValid()) {
				try {
					run();
					this.exitStatus = SUCCESS;
				} catch (RuntimeException e) {
					System.err.println("Error: A bug has been detected. Stacktrace below:");
					e.printStackTrace(System.err);
				} catch (Exception e) {
					System.err.println("An error was detected: ");
				}
			}
		}
	}

	private void readCommandLine(String[] args) {
		try {
			OptionSet options = cmdLineParser.parse(args);
			if (options.has(helpOption)) {
				try {
					System.out.println(USAGE);
					this.cmdLineParser.printHelpOn(System.out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (options.nonOptionArguments().size() == EXPECTED_NUM_SPEC_FILES) {
				this.fileName = new File(options.nonOptionArguments().get(0));
				if (options.has(this.packageNameOption)) {
					this.packageName = options.valueOf(this.packageNameOption);
				}
				if (options.has(this.targetDirOption)) {
					this.target = options.valueOf(this.targetDirOption);
				}
			} else {
				System.err
						.println("Invalid arguments: type --help for correct usage.");
			}
		} catch (OptionException e) {
			System.err.println(e.getMessage());
		}
	}

	private void validateParameters() {
		this.setParamatersValid(true);
		if (!this.fileName.isFile() || !this.fileName.canRead()) {
			System.err.println(this.fileName
					+ " is not a file or cannot be read");
			this.setParamatersValid(false);
		}
		if (!this.target.isDirectory() || !this.target.canWrite()) {
			System.err.println(this.target
					+ " is not a directory or cannot be written to");
			this.setParamatersValid(false);
		}
		if (this.packageName != null
				&& !VALID_PACKAGE_REGEX.matcher(packageName).matches()) {
			System.err.println(this.packageName
					+ " is not a valid java package name");
			this.setParamatersValid(false);
		}
	}

	private void setParamatersValid(boolean areParamatersValid) {
		this.areParamatersValid = areParamatersValid;
	}

	private boolean areParamatersValid() {
		return areParamatersValid;
	}

	private void run() throws Exception {
		ContextTreeParser.context_return r = parseInput();
//		CommonTree t = parseAST(r);
		writeSyntaxDefinition(r);
//		writeSupportClasses(t);
	}

	private ContextTreeParser.context_return parseInput() throws IOException, RecognitionException{
		InputStream inStream = new FileInputStream(fileName);
		ANTLRInputStream input = new ANTLRInputStream(inStream);
		ContextTreeLexer lexer = new ContextTreeLexer(input);
		tokens = new CommonTokenStream(lexer);
		parser = new ContextTreeParser(tokens);
		ContextTreeParser.context_return r = parser.context();
		if (packageName == null) {
			packageName = parser.packageName.toString();
		}
		inStream.close();
		return r;
	}
	
//	private void writeSupportClasses(CommonTree t) throws RecognitionException, IOException{
//		nodes = new CommonTreeNodeStream(t);
//		nodes.setTokenStream(tokens);
//		StringTemplate stj = walkTreeWithServiceProvider(parser, nodes);
//		// System.out.println(stj.toString());
//		// typeName = "ContextAdapterServiceProvider";
//		String prefix = target.toString();// +"src";
//		sendOut(prefix, stj);
//		copyDefinition(fileName, new File(packageName));
//	}
	
	private void writeSyntaxDefinition(context_return r) throws RecognitionException, IOException{
		CommonTree t = (CommonTree) r.getTree();
		nodes = new CommonTreeNodeStream(t);
		nodes.setTokenStream(tokens);
		StringTemplate stj = walkTreeWithJava(parser, nodes);
		// System.out.println(stj.toString());
		String prefix = target.toString();// +"src";
		// String typeName = "ContextAdapterSyntaxService";
		sendOut(prefix, stj);
	}
	
//	private CommonTree parseAST(ContextTreeParser.context_return r) throws IOException{
//		CommonTree t = (CommonTree) r.getTree();
//		DOTTreeGenerator gen = new DOTTreeGenerator();
//		StringTemplate st = gen.toDOT(t);
//		File tmpDotFile = File.createTempFile(packageName, ".dot");
//		FileWriter fw = new FileWriter(tmpDotFile);
//		fw.write(st.toString());
//		System.out.println(t.toStringTree());
//		fw.close();
//		System.out.println(parser.shapeList);
//		tmpDotFile.deleteOnExit();
//		return t;
//	}
	
//	private static void copyDefinition(File srcFile, File destinationFolder)
//			throws IOException {
//		// Create channel on the source
//		FileChannel srcChannel = new FileInputStream(srcFile).getChannel();
//		File destFile = new File(destinationFolder, srcFile.getName());
//		// Create channel on the destination
//		FileChannel dstChannel = new FileOutputStream(destFile).getChannel();
//
//		// Copy file contents from source to destination
//		dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
//
//		// Close the channels
//		srcChannel.close();
//		dstChannel.close();
//	}

	private void sendOut(String prefix,	StringTemplate stj) throws IOException {
		File f = new File(prefix);
		if (!f.exists()) {
			boolean res = f.mkdirs();
			if (!res) {
				System.err.println("Directory is not created:\n\t"
						+ f.getAbsolutePath());
				return;
			}
		}
		FolderSplitWriter fw = new FolderSplitWriter(prefix);
		StringTemplateWriter wr = new AutoIndentWriter(fw);
		stj.write(wr);
		fw.close();
	}

	private StringTemplate walkTreeWithJava(ContextTreeParser parser,
			CommonTreeNodeStream nodes) throws RecognitionException,
			IOException {
		InputStream is = this.getClass().getResourceAsStream(SYNTAX_DEFN_TEMPLATE);
		if(is == null){
			throw new RuntimeException("Cannot open Resource: " + SYNTAX_DEFN_TEMPLATE);
		}
//		FileReader fr = new FileReader(SYNTAX_DEFN_TEMPLATE);
		Reader fr = new InputStreamReader(is);
		StringTemplateGroup templates = new StringTemplateGroup(fr);
		fr.close();
		return walkTree(parser, nodes, templates);
	}

//	private StringTemplate walkTreeWithServiceProvider(
//			ContextTreeParser parser, CommonTreeNodeStream nodes)
//			throws RecognitionException, IOException {
//		FileReader fr = new FileReader("grammar/ServiceProvider.stg");
//		StringTemplateGroup templates = new StringTemplateGroup(fr);
//		fr.close();
//		return walkTree(parser, nodes, templates);
//	}

	// private StringTemplate walkTreeWithJUnit(ContextTreeParser parser,
	// CommonTreeNodeStream nodes) throws RecognitionException, IOException {
	// FileReader fr = new FileReader("grammar/JUnit.stg");
	// StringTemplateGroup templates = new StringTemplateGroup(fr);
	// fr.close();
	// return walkTree(parser, nodes, templates);
	// }

	private StringTemplate walkTree(ContextTreeParser parser,
			CommonTreeNodeStream nodes, StringTemplateGroup templates)
			throws RecognitionException {
		ContextTreeWalker walker = new ContextTreeWalker(nodes);
		walker.setTemplateLib(templates);
		RuleReturnScope rw = (RuleReturnScope) walker.context(parser.shapeList,
				parser.linkList, packageName);
		return (StringTemplate) rw.getTemplate();
	}

	public int getExitStatus() {
		return this.exitStatus;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SyntaxDefinitionGenerator cag = new SyntaxDefinitionGenerator();
		cag.runApplication(args);
		System.exit(cag.getExitStatus());
	}

}

/*
 * $Log$ Revision 1.9 2008/06/07 16:23:02 asorokin Notation definition file is
 * copied to target as well
 * 
 * Revision 1.8 2008/06/07 16:09:23 asorokin removed unnecessary directory
 * creation
 * 
 * Revision 1.7 2008/06/06 17:57:31 asorokin Creates proper Plug-in Project
 * structure
 * 
 * Revision 1.6 2008/06/06 12:56:36 asorokin New Writer
 * 
 * Revision 1.5 2008/06/04 13:34:51 nhanlon fixed imports
 * 
 * Revision 1.4 2008/05/05 13:35:47 asorokin *** empty log message ***
 * 
 * Revision 1.3 2008/04/29 06:13:11 asorokin Bug fixes
 * 
 * Revision 1.2 2008/04/22 23:04:25 asorokin Working CA syntax service provider
 * generator
 * 
 * Revision 1.1 2008/04/21 07:48:14 asorokin ContextAdapter initial commit
 */