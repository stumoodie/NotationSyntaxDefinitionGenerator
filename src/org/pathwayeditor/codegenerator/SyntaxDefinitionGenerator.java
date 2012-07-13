package org.pathwayeditor.codegenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.DOTTreeGenerator;
import org.antlr.runtime.tree.Tree;
import org.antlr.stringtemplate.AutoIndentWriter;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateWriter;
import org.pathwayeditor.codegenerator.gen.NotationGenLexer;
import org.pathwayeditor.codegenerator.gen.NotationGenParser;

public class SyntaxDefinitionGenerator {
//	private static final String SYNTAX_DEFN_TEMPLATE = "org.pathwayeditor.codegenerator.Java.stg";
	private static final String SYNTAX_DEFN_TEMPLATE = "SyntaxServiceTemplate.stg";
	private static int SUCCESS = 0;
	private static int FAIL = 1;

	private File fileName = null;
//	private String packageName;
	private String qualifiedName;
	private File target = new File(".");
	private final OptionParser cmdLineParser = new OptionParser();
	// private final OptionSpec<File> fileNameOption;
	private final OptionSpec<File> targetDirOption;
//	private final OptionSpec<String> packageNameOption;
	private final OptionSpec<Void> helpOption;
	private int exitStatus = FAIL;
	private boolean areParamatersValid;
	private static final String USAGE = "program <options> <spec file>";
	private static final int EXPECTED_NUM_SPEC_FILES = 1;
//	private static final Pattern VALID_PACKAGE_REGEX = Pattern
//			.compile("\\w((\\.|\\w)*\\w)?");
	private static final List<String> TARGET_DIR_OPTIONS = Arrays.asList("t", "targetDir");
//	private static final List<String> PACKAGE_OPTIONS = Arrays.asList("p", "package");
	private static final List<String> HELP_OPTIONS = Arrays.asList("?", "h", "help");
	private CommonTokenStream tokens;
	private NotationGenParser parser;
	private CommonTreeNodeStream nodes;
//	private String className;

	public SyntaxDefinitionGenerator() {
		// this.fileNameOption = cmdLineParser.accepts("f",
		// "The specification file to read").withRequiredArg().ofType(File.class).describedAs("file name");
		this.cmdLineParser.posixlyCorrect(true);
		this.targetDirOption = cmdLineParser.acceptsAll(TARGET_DIR_OPTIONS, "The target directory").withRequiredArg().ofType(File.class)
				.describedAs("directory name");
//		this.packageNameOption = this.cmdLineParser.acceptsAll(PACKAGE_OPTIONS, "The package name for the new Context Adapter")
//				.withRequiredArg().ofType(String.class).describedAs("package name");
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
					System.err.println("An error was detected: " + e.getMessage());
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
//				if (options.has(this.packageNameOption)) {
//					this.packageName = options.valueOf(this.packageNameOption);
//				}
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
//		if (this.packageName != null
//				&& !VALID_PACKAGE_REGEX.matcher(packageName).matches()) {
//			System.err.println(this.packageName
//					+ " is not a valid java package name");
//			this.setParamatersValid(false);
//		}
	}

	private void setParamatersValid(boolean areParamatersValid) {
		this.areParamatersValid = areParamatersValid;
	}

	private boolean areParamatersValid() {
		return areParamatersValid;
	}

	private void run() throws Exception {
		InputStream inStream = new FileInputStream(fileName);
		ANTLRInputStream input = new ANTLRInputStream(inStream);
		NotationGenLexer lexer = new NotationGenLexer(input);
		tokens = new CommonTokenStream(lexer);
		parser = new NotationGenParser(tokens);
		NotationGenParser.notation_spec_return r = parser.notation_spec();
		dumpTree(r.getTree(), new File("treeGrammar.dot"));
		inStream.close();
//		CommonTree t = (CommonTree) r.getTree();
//		nodes = new CommonTreeNodeStream(t);
//		nodes.setTokenStream(tokens);
//		InputStream is = this.getClass().getResourceAsStream(SYNTAX_DEFN_TEMPLATE);
//		if(is == null){
//			throw new RuntimeException("Cannot open Resource: " + SYNTAX_DEFN_TEMPLATE);
//		}
//		Reader fr = new InputStreamReader(is);
//		StringTemplateGroup templates = new StringTemplateGroup(fr);
//		fr.close();
//		NotationGenTree walker = new NotationGenTree(nodes);
//		walker.setTemplateLib(templates);
//		NotationGenTree.notation_spec_return walkerRet = walker.notation_spec();
//		
//		this.qualifiedName = walker.getQualifiedName();
//		StringTemplate stj = (StringTemplate)walkerRet.getTemplate();
//		System.out.println(stj.toString());
//		String prefix = target.toString();
//		sendOut(prefix, stj);
	}

    private static void dumpTree(Object ast, File oFile) throws FileNotFoundException{
		DOTTreeGenerator dtg = new DOTTreeGenerator();
		StringTemplate st = dtg.toDOT((Tree)ast);
		PrintStream os = new PrintStream(new FileOutputStream(oFile)); 
		os.println(st);
		os.close();
    }

	private void sendOut(String prefix, StringTemplate stj) throws IOException {
		StringBuilder buf = new StringBuilder(prefix);
		buf.append("/");
		buf.append(this.qualifiedName.replace('.', '/'));
		buf.append("NotationSyntaxService.java");
		File outFile = new File(buf.toString());
		Writer w = new FileWriter(outFile);
		StringTemplateWriter wr = new AutoIndentWriter(w);
		stj.write(wr);
		w.close();
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