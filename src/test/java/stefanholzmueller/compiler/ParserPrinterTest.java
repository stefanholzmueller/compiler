package stefanholzmueller.compiler;

import org.junit.Assert;
import org.junit.Test;

public class ParserPrinterTest {

	private Parser parser = new SourceParser();
	private Printer printer = new PrettyPrinter();

	@Test
	public void parseIntegerLiteral() throws Exception {
		assertParsed("123");
	}

	@Test
	public void parseBooleanLiteral() throws Exception {
		assertParsed("true");
		assertParsed("false");
	}

	@Test
	public void parseStringLiteral() throws Exception {
		assertParsed("\"hi\"");
		// assertParsedLiteral("\"back\\\"slash\"");
	}

	@Test
	public void parseIfExpression() throws Exception {
		assertParsed("if true then 1 else 0 fi", "if true then 1 else 0 fi");
		assertParsed("if false\n  then \"\"\n  else \"hi\"\nfi", "if false then \"\" else \"hi\" fi");
	}

	@Test
	public void parseExplicitParens() throws Exception {
		assertParsed("(123)", "123");
	}

	@Test
	public void parseFunctionDefinition() throws Exception {
		assertParsed("answer():Int=42", "answer(): Int = 42");
		assertParsed("id(n: Int): Int = n", "id(n: Int): Int = n");
		assertParsed("f (n:Int, b:Bool):Int=n", "f(n: Int, b: Bool): Int = n");
		assertParsed("succ(n: Int): Int = addInt n 1", "succ(n: Int): Int = addInt n 1");
	}

	@Test
	public void parseProgram() throws Exception {
		assertParsed("answer(): Int = 42\n\nanswer");
	}

	@Test
	public void parseInfixNotation() throws Exception {
		assertParsed("1 `plus` 2", "plus 1 2");
	}

	@Test
	public void testIdentityOfOne() throws Exception {
		assertParsed("id(x: Int): Int = x\n\nid 1");
	}

	private void assertParsed(String input, String expected) {
		AbstractSyntaxTree ast = parser.parse(input);
		String string = printer.print(ast);
		Assert.assertEquals(expected, string);
	}

	private void assertParsed(String input) {
		AbstractSyntaxTree ast = parser.parse(input);
		String string = printer.print(ast);
		Assert.assertEquals(input, string);
	}
}
