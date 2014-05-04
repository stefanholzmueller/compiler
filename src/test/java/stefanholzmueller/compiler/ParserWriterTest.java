package stefanholzmueller.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import stefanholzmueller.compiler.Generator.CompilationUnit;

public class ParserWriterTest {

	private static final String LIBRARY_PATH = "stefanholzmueller/compiler/library";
	private static final String OUTPUT_PATH = "target/compiled";

	private Parser parser = new SourceParser();
	private Analyzer analyzer = new SemanticAnalyzer();
	private Generator generator = new BytecodeGenerator();
	private Writer emitter = new BytecodeWriter();

	@BeforeClass
	public static void setUp() throws IOException {
		FileUtils.copyDirectory(new File("target/classes/" + LIBRARY_PATH), new File(OUTPUT_PATH + "/" + LIBRARY_PATH));
	}

	@Test
	public void evaluateMinus() throws Exception {
		assertProgramOutput("321 `minus` 123", "198\n");
	}

	@Test
	public void evaluateMinusMinus() throws Exception {
		assertProgramOutput("7 `minus` 5 `minus` 3", "5\n");
	}

	@Test
	public void evaluateMinusMinusWithExplicitParens() throws Exception {
		assertProgramOutput("(7 `minus` 5) `minus` 3", "-1\n");
	}

	@Test
	public void evaluateIf() throws Exception {
		assertProgramOutput("if true then 1 else 0 fi", "1\n");
	}

	@Test
	public void evaluateNestedIf() throws Exception {
		assertProgramOutput("if true then if false then 2 else 3 fi else 0 fi", "3\n");
	}

	@Test
	public void evaluateLessThanIf() throws Exception {
		assertProgramOutput("if (1 `lessThan` 2) then 1 else 0 fi", "1\n");
	}

	@Test
	public void println() throws Exception {
		assertProgramOutput("\"hi\"", "hi\n");
	}

	private void assertProgramOutput(String source, String expected) throws IOException, InterruptedException {
		AbstractSyntaxTree ast = parser.parse(source);
		AbstractSyntaxTree ast2 = analyzer.analyze(ast);
		CompilationUnit main = generator.generateMain(ast2);
		emitter.write(main, OUTPUT_PATH);

		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec("java -cp " + OUTPUT_PATH + " Main");
		proc.waitFor();

		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		String stdErr = read(stdError);
		String stdOut = read(stdInput);

		Assert.assertEquals("", stdErr);
		Assert.assertEquals(expected, stdOut);
	}

	private String read(BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = reader.readLine()) != null) {
			sb.append(s);
			sb.append('\n');
		}
		return sb.toString();
	}
}
