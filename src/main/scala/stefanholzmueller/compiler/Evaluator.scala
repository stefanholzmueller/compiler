package stefanholzmueller.compiler

import java.math.BigDecimal

class Evaluator {
	def eval(fa: FunctionApplication): Any = fa match {
		case FunctionApplication(Identifier(name), args) => {
			val clazz: Class[Function] = Class.forName(name).asInstanceOf[Class[Function]]
			val instance: Function = clazz.newInstance()
			instance.apply()
		}
	}
}

object Evaluator {
	def main(args: Array[String]) {
		val evaluator = new Evaluator();
		val evaluated = evaluator.eval(FunctionApplication(Identifier("hello"), List()))
		println(evaluated)

		println(new BigDecimal(2).divide(new BigDecimal(3), 100, BigDecimal.ROUND_HALF_EVEN))
	}
}