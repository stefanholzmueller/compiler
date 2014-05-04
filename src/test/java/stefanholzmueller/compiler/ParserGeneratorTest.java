package stefanholzmueller.compiler;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import stefanholzmueller.compiler.Generator.CompilationUnit;

public class ParserGeneratorTest {

	private Parser parser = new SourceParser();
	private Analyzer analyzer = new SemanticAnalyzer();
	private Generator generator = new BytecodeGenerator();

	@Test
	public void generateClassFileWithMainMethod() throws Exception {
		String source = "\"Hello, World!\"";

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
		expected.append("    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V\n");
		expected.append("    RETURN\n");
		expected.append("    MAXSTACK = 2\n");
		expected.append("    MAXLOCALS = 1\n");
		expected.append("}\n");
		assertBytecode(source, expected);
	}

	@Test
	public void generateClassFileWithMainMethodAndIntLiteral() throws Exception {
		String source = "123";

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
		expected.append("    NEW java/math/BigDecimal\n");
		expected.append("    DUP\n");
		expected.append("    SIPUSH 123\n");
		expected.append("    INVOKESPECIAL java/math/BigDecimal.<init> (I)V\n");
		expected.append("    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V\n");
		expected.append("    RETURN\n");
		expected.append("    MAXSTACK = 4\n");
		expected.append("    MAXLOCALS = 1\n");
		expected.append("}\n");

		assertBytecode(source, expected);
	}

	@Test
	public void generateClassFileWithMainMethodAndFunctionApplication() throws Exception {
		String source = "\"hi\"";

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
		expected.append("    LDC \"hi\"\n");
		expected.append("    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V\n");
		expected.append("    RETURN\n");
		expected.append("    MAXSTACK = 2\n");
		expected.append("    MAXLOCALS = 1\n");
		expected.append("}\n");

		assertBytecode(source, expected);
	}

	@Test
	public void generateClassFileWithMainMethodAndInfixFunctionApplication() throws Exception {
		String source = "321 `minus` 123";

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
		expected.append("    NEW stefanholzmueller/compiler/library/minus\n");
		expected.append("    DUP\n");
		expected.append("    INVOKESPECIAL stefanholzmueller/compiler/library/minus.<init> ()V\n");
		expected.append("    NEW java/math/BigDecimal\n");
		expected.append("    DUP\n");
		expected.append("    SIPUSH 321\n");
		expected.append("    INVOKESPECIAL java/math/BigDecimal.<init> (I)V\n");
		expected.append("    NEW java/math/BigDecimal\n");
		expected.append("    DUP\n");
		expected.append("    SIPUSH 123\n");
		expected.append("    INVOKESPECIAL java/math/BigDecimal.<init> (I)V\n");
		expected.append("    INVOKEVIRTUAL stefanholzmueller/compiler/library/minus.apply (Ljava/math/BigDecimal;Ljava/math/BigDecimal;)Ljava/math/BigDecimal;\n");
		expected.append("    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V\n");
		expected.append("    RETURN\n");
		expected.append("    MAXSTACK = 6\n");
		expected.append("    MAXLOCALS = 1\n");
		expected.append("}\n");

		assertBytecode(source, expected);
	}

	@Test
	public void generateClassFileUserFunctionAndMain() throws Exception {
		String source = "id(x: Int): Int = x\nid 1";

		StringBuilder expected = new StringBuilder();
		expected.append("// class version 51.0 (51)\n");
		expected.append("// access flags 0x1\n");
		expected.append("// signature Lid;\n");
		expected.append("// declaration: id extends id\n");
		expected.append("public class id {\n");
		expected.append("\n");
		expected.append("\n");
		expected.append("  // access flags 0x1\n");
		expected.append("  public <init>()V\n");
		expected.append("    ALOAD 0\n");
		expected.append("    INVOKESPECIAL java/lang/Object.<init> ()V\n");
		expected.append("    RETURN\n");
		expected.append("    MAXSTACK = 1\n");
		expected.append("    MAXLOCALS = 1\n");
		expected.append("\n");
		expected.append("  // access flags 0x1\n");
		expected.append("  public apply(Ljava/math/BigDecimal;)Ljava/math/BigDecimal;\n");
		expected.append("    ALOAD 1\n");
		expected.append("    ARETURN\n");
		expected.append("    MAXSTACK = 1\n");
		expected.append("    MAXLOCALS = 2\n");
		expected.append("}\n");
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
		expected.append("    NEW id\n");
		expected.append("    DUP\n");
		expected.append("    INVOKESPECIAL id.<init> ()V\n");
		expected.append("    NEW java/math/BigDecimal\n");
		expected.append("    DUP\n");
		expected.append("    SIPUSH 1\n");
		expected.append("    INVOKESPECIAL java/math/BigDecimal.<init> (I)V\n");
		expected.append("    INVOKEVIRTUAL id.apply (Ljava/math/BigDecimal;)Ljava/math/BigDecimal;\n");
		expected.append("    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V\n");
		expected.append("    RETURN\n");
		expected.append("    MAXSTACK = 5\n");
		expected.append("    MAXLOCALS = 1\n");
		expected.append("}\n");
		assertBytecode(source, expected);
	}

	@Test
	public void succ() throws Exception {
		String source = "succ(n: Int): Int = (n `plus` 1)\n(succ 3)";

		StringBuilder expected = new StringBuilder();
		expected.append("// class version 51.0 (51)\n");
		expected.append("// access flags 0x1\n");
		expected.append("// signature Lsucc;\n");
		expected.append("// declaration: succ extends succ\n");
		expected.append("public class succ {\n");
		expected.append("\n");
		expected.append("\n");
		expected.append("  // access flags 0x1\n");
		expected.append("  public <init>()V\n");
		expected.append("    ALOAD 0\n");
		expected.append("    INVOKESPECIAL java/lang/Object.<init> ()V\n");
		expected.append("    RETURN\n");
		expected.append("    MAXSTACK = 1\n");
		expected.append("    MAXLOCALS = 1\n");
		expected.append("\n");
		expected.append("  // access flags 0x1\n");
		expected.append("  public apply(Ljava/math/BigDecimal;)Ljava/math/BigDecimal;\n");
		expected.append("    NEW stefanholzmueller/compiler/library/plus\n");
		expected.append("    DUP\n");
		expected.append("    INVOKESPECIAL stefanholzmueller/compiler/library/plus.<init> ()V\n");
		expected.append("    ALOAD 1\n");
		expected.append("    NEW java/math/BigDecimal\n");
		expected.append("    DUP\n");
		expected.append("    SIPUSH 1\n");
		expected.append("    INVOKESPECIAL java/math/BigDecimal.<init> (I)V\n");
		expected.append("    INVOKEVIRTUAL stefanholzmueller/compiler/library/plus.apply (Ljava/math/BigDecimal;Ljava/math/BigDecimal;)Ljava/math/BigDecimal;\n");
		expected.append("    ARETURN\n");
		expected.append("    MAXSTACK = 5\n");
		expected.append("    MAXLOCALS = 2\n");
		expected.append("}\n");
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
		expected.append("    NEW succ\n");
		expected.append("    DUP\n");
		expected.append("    INVOKESPECIAL succ.<init> ()V\n");
		expected.append("    NEW java/math/BigDecimal\n");
		expected.append("    DUP\n");
		expected.append("    SIPUSH 3\n");
		expected.append("    INVOKESPECIAL java/math/BigDecimal.<init> (I)V\n");
		expected.append("    INVOKEVIRTUAL succ.apply (Ljava/math/BigDecimal;)Ljava/math/BigDecimal;\n");
		expected.append("    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V\n");
		expected.append("    RETURN\n");
		expected.append("    MAXSTACK = 5\n");
		expected.append("    MAXLOCALS = 1\n");
		expected.append("}\n");
		assertBytecode(source, expected);
	}

	private void assertBytecode(String source, StringBuilder expected) throws IOException {
		AbstractSyntaxTree ast = parser.parse(source);
		IntermediateRepresentation ir = analyzer.analyze(ast);
		Collection<CompilationUnit> compilationUnits = generator.generate(ir);

		StringBuilder text = new StringBuilder();
		for (CompilationUnit compilationUnit : compilationUnits) {
			text.append(textifyBytecode(compilationUnit.getBytes()));
		}
		assertEquals(expected.toString(), text.toString());
	}

	private String textifyBytecode(byte[] bytes) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		ClassReader cr = new ClassReader(is);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		TraceClassVisitor tcv = new TraceClassVisitor(pw);
		CheckClassAdapter cca = new CheckClassAdapter(tcv);
		cr.accept(cca, 0);
		return sw.toString();
	}

}
