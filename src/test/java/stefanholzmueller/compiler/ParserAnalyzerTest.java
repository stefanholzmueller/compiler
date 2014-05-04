package stefanholzmueller.compiler;

import org.junit.Assert;
import org.junit.Test;

public class ParserAnalyzerTest {

	private Parser parser = new SourceParser();
	private Analyzer analyzer = new SemanticAnalyzer();

	@Test
	public void analyzeParameterReference() throws Exception {
		assertAnalyzed("id(x: Int): Int = x",
				"Program(List(FunctionDefinition(NameIdentifier(id),TypeIdentifier(Int),List(Parameter(NameIdentifier(x),TypeIdentifier(Int))),Variable(NameIdentifier(x),TypeIdentifier(Int),Position(1))),None)");
	}

	@Test
	public void analyzeLibraryFunction() throws Exception {
		assertAnalyzed("1 `plus` 2", "Prog(List(),Some(Apply(LibraryFunction(plus,Int,List(Param(a,Int,1), Param(b,Int,2))),List(IntLit(1), IntLit(2)))))");
	}

	@Test
	public void analyzeUserFunction() throws Exception {
		assertAnalyzed("answer(): Int = 42\nanswer", "Prog(List(UserFunction(answer,Int,List(),IntLit(42))),Some(Apply(UserFunction(answer,Int,List(),IntLit(42)),List())))");
	}

	@Test
	public void analyzeUserFunctionRecursion() throws Exception {
		assertAnalyzed(
				"id(x: Int): Int = id x",
				"Program(List(FunctionDefinition(NameIdentifier(id),TypeIdentifier(Int),List(Parameter(NameIdentifier(x),TypeIdentifier(Int))),UserFunctionApplication(NameIdentifier(id),List(Variable(NameIdentifier(x),TypeIdentifier(Int))),TypeIdentifier(Int)))),None)");
	}

	private void assertAnalyzed(String input, String expected) {
		AbstractSyntaxTree ast = parser.parse(input);
		IntermediateRepresentation ir = analyzer.analyze(ast);
		Assert.assertEquals(expected, ir.toString());
	}

}
