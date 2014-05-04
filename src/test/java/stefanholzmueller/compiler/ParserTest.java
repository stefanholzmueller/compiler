package stefanholzmueller.compiler;

import org.junit.Assert;
import org.junit.Test;

public class ParserTest {

	private Parser parser = new SourceParser();

	@Test
	public void parseFunctionDefinition() throws Exception {
		assertParsed("id(n: Int): Int = n",
				"Program(List(FunctionDefinition(NameIdentifier(id),TypeIdentifier(Int),List(Parameter(NameIdentifier(n),TypeIdentifier(Int))),FunctionApplication(NameIdentifier(n),List()))),None)");
	}

	@Test
	public void succ() throws Exception {
		assertParsed(
				"succ(n: Int): Int = (x `plus` 1)\n(succ 3)",
				"Program(List(FunctionDefinition(NameIdentifier(succ),TypeIdentifier(Int),List(Parameter(NameIdentifier(n),TypeIdentifier(Int))),FunctionApplication(NameIdentifier(plus),List(FunctionApplication(NameIdentifier(x),List()), IntLiteral(1))))),Some(FunctionApplication(NameIdentifier(succ),List(IntLiteral(3)))))");
	}

	private void assertParsed(String input, String expected) {
		AbstractSyntaxTree ast = parser.parse(input);
		Assert.assertEquals(expected, ast.toString());
	}
}
