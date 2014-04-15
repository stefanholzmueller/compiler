package stefanholzmueller.compiler;

import org.junit.Assert;
import org.junit.Test;

public class ParserPrinterTest {

	private Parser parser = new MiniParser();
	private Printer printer = new MiniPrinter();

	@Test
	public void parseVariable() throws Exception {
		assertParsed("asd", "asd");
	}

	@Test
	public void parseIntegerLiteral() throws Exception {
		assertParsedLiteral("123");
	}

	@Test
	public void parseBooleanLiteral() throws Exception {
		assertParsedLiteral("true");
		assertParsedLiteral("false");
	}

	@Test
	public void parseStringLiteral() throws Exception {
		assertParsedLiteral("\"hi\"");
		// assertParsedLiteral("\"back\\\"slash\"");
	}

	private void assertParsed(String input, String expected) {
		AbstractSyntaxTree ast = parser.parse(input);
		String string = printer.print(ast);
		Assert.assertEquals(expected, string);
	}

	private void assertParsedLiteral(String literal) {
		AbstractSyntaxTree ast = parser.parse(literal);
		String string = printer.print(ast);
		Assert.assertEquals(literal, string);
	}
}
