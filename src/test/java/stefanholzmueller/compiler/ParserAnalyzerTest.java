package stefanholzmueller.compiler;

import org.junit.Assert;
import org.junit.Test;

public class ParserAnalyzerTest {

	private Parser parser = new SourceParser();
	private Analyzer analyzer = new SemanticAnalyzer();

	@Test
	public void analyzeParameterReference() throws Exception {
		assertAnalyzed("id(x: Int): Int = x",
				"Program(List(FunctionDefinition(NameIdentifier(id),TypeIdentifier(Int),List(Parameter(NameIdentifier(x),TypeIdentifier(Int))),Variable(NameIdentifier(x),TypeIdentifier(Int)))),None)");
	}

	@Test
	public void analyzeLibraryFunction() throws Exception {
		assertAnalyzed("1 `plus` 2", "Program(List(),Some(LibraryFunctionApplication(NameIdentifier(plus),List(IntLiteral(1), IntLiteral(2)),TypeIdentifier(java.math.BigDecimal))))");
	}

	@Test
	public void analyzeUserFunction() throws Exception {
		assertAnalyzed("answer(): Int = 42\nanswer",
				"Program(List(FunctionDefinition(NameIdentifier(answer),TypeIdentifier(Int),List(),IntLiteral(42))),Some(UserFunctionApplication(NameIdentifier(answer),List(),TypeIdentifier(Int))))");
	}

	@Test
	public void analyzeUserFunctionRecursion() throws Exception {
		assertAnalyzed(
				"id(x: Int): Int = id x",
				"Program(List(FunctionDefinition(NameIdentifier(id),TypeIdentifier(Int),List(Parameter(NameIdentifier(x),TypeIdentifier(Int))),UserFunctionApplication(NameIdentifier(id),List(Variable(NameIdentifier(x),TypeIdentifier(Int))),TypeIdentifier(Int)))),None)");
	}

	private void assertAnalyzed(String input, String expected) {
		AbstractSyntaxTree ast = parser.parse(input);
		AbstractSyntaxTree ast2 = analyzer.analyze(ast);
		Assert.assertEquals(expected, ast2.toString());
	}

}
