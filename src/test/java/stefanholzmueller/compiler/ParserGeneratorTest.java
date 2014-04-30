package stefanholzmueller.compiler;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import stefanholzmueller.compiler.Generator.CompilationUnit;

public class ParserGeneratorTest {

	private Parser parser = new MiniParser();
	private Generator generator = new BytecodeGenerator();

	@Test
	public void generateClassFileWithMainMethod() throws Exception {
		String source = "1";
		AbstractSyntaxTree ast = parser.parse(source);
		CompilationUnit compilationUnit = generator.generateMain(ast);

		assertEquals("Main", compilationUnit.getName());

		String text = textifyBytecode(compilationUnit.getBytes());

		StringBuilder expected = new StringBuilder();
		expected.append("// class version 51.0 (51)\n");
		expected.append("// access flags 0x1\n");
		expected.append("// signature LMain;\n");
		expected.append("// declaration: Main extends Main\n");
		expected.append("public class Main {\n");
		expected.append("\n");
		expected.append("\n");
		expected.append("  // access flags 0x9\n");
		expected.append("  public static main([Ljava/lang/String;)V\n");
		expected.append("    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;\n");
		expected.append("    LDC \"Hello, World!\"\n");
		expected.append("    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V\n");
		expected.append("    RETURN\n");
		expected.append("    MAXSTACK = 2\n");
		expected.append("    MAXLOCALS = 1\n");
		expected.append("}\n");
		assertEquals(expected.toString(), text);
	}

	private String textifyBytecode(byte[] bytes) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		ClassReader cr = new ClassReader(is);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		cr.accept(new TraceClassVisitor(pw), 0);
		return sw.toString();
	}
}
