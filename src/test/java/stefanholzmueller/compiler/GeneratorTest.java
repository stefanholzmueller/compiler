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

public class GeneratorTest {

	private Parser parser = new MiniParser();
	private Generator generator = new BytecodeGenerator();

	@Test
	public void generateClassFileWithMainMethod() throws Exception {
		String source = "1";
		AbstractSyntaxTree ast = parser.parse(source);
		CompilationUnit compilationUnit = generator.generateMain(ast);

		assertEquals("HelloWorld", compilationUnit.getName());

		String decompiled = textifyBytecode(compilationUnit.getBytes());

		assertEquals("", decompiled);
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
