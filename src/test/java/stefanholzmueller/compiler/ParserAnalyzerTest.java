package stefanholzmueller.compiler;

import org.junit.Assert;
import org.junit.Test;

public class ParserAnalyzerTest {

	private Parser parser = new SourceParser();
	private Analyzer analyzer = new SemanticAnalyzer();

	@Test
	public void analyzeParameterReference() throws Exception {
		assertAnalyzed("id(x: Int): Int = x", "Prog(List(UserFun(id,INT,List(Param(x,INT,0)),Var(x,INT,0))),None)");
	}

	@Test
	public void analyzeLibraryFunction() throws Exception {
		assertAnalyzed("1 `plus` 2", "Prog(List(),Some(Apply(stefanholzmueller/compiler/library/plus,INT,List(IntLit(1), IntLit(2)))))");
	}

	@Test
	public void analyzeUserFunction() throws Exception {
		assertAnalyzed("answer(): Int = 42\nanswer", "Prog(List(UserFun(answer,INT,List(),IntLit(42))),Some(Apply(answer,INT,List())))");
	}

	@Test
	public void analyzeUserFunctionRecursion() throws Exception {
		assertAnalyzed("id(x: Int): Int = id x", "Prog(List(UserFun(id,INT,List(Param(x,INT,0)),Apply(id,INT,List(Var(x,INT,0))))),None)");
	}

	private void assertAnalyzed(String input, String expected) {
		AbstractSyntaxTree ast = parser.parse(input);
		IntermediateRepresentation ir = analyzer.analyze(ast);
		Assert.assertEquals(expected, ir.toString());
	}

}
