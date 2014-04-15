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
		assertParsed("123", "123");
	}

	@Test
	public void parseBooleanLiteral() throws Exception {
		assertParsed("true", "true");
		assertParsed("false", "false");
	}

	private void assertParsed(String input, String expected) {
		AbstractSyntaxTree ast = parser.parse(input);
		String string = printer.print(ast);
		Assert.assertEquals(expected, string);
	}
}
