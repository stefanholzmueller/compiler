package stefanholzmueller.compiler;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ParserEvaluatorTest {

	private Parser parser = new SourceParser();
	private Evaluator evaluator = new Evaluator();

	@Test
	public void evaluateIntLiteral() throws Exception {
		assertEvaluated("42", new BigDecimal(42));
	}

	@Test
	public void evaluateFunctionApplication() throws Exception {
		assertEvaluated("answer(): Int = 42\nanswer", new BigDecimal(42));
	}

	@Ignore
	@Test
	public void evaluateFunctionApplicationWithVariable() throws Exception {
		assertEvaluated("id(x: Int): Int = x\nid 1", new BigDecimal(1));
	}

	@Test
	public void evaluateLibraryFunctionApplication() throws Exception {
		assertEvaluated("plus 1 2", new BigDecimal(3));
	}

	private void assertEvaluated(String input, Object expected) {
		AbstractSyntaxTree ast = parser.parse(input);
		Object object = evaluator.eval(ast);
		Assert.assertEquals(expected, object);
	}

}
