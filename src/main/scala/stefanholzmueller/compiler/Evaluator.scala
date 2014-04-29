package stefanholzmueller.compiler

import java.math.BigDecimal
import stefanholzmueller.compiler.library.function.Function

class Evaluator {
	def evalLibrary(fa: FunctionApplication): Any = fa match {
		case FunctionApplication(Identifier(name), args) => {
			val clazz: Class[Function] = Class.forName(name).asInstanceOf[Class[Function]]
			val instance: Function = clazz.newInstance()
			instance.apply()
		}
	}

	def eval(program: AbstractSyntaxTree): Any = program match {
		case Program(fns, main) => {
			if (main.isEmpty) null else evalWithEnv(main.get, fns)
		}
	}

	def evalWithEnv(expression: Expression, env: List[FunctionDefinition]): Any = expression match {
		case IntLiteral(v) => new BigDecimal(v)
		case FunctionApplication(name, args) => {
			val optFn = env.find(_.name == name)
			if (optFn.isEmpty) {
				evalLibrary("stefanholzmueller.compiler.library.desugared." + name.name, args.map(evalWithEnv(_, env)))
			} else {
				val fn = optFn.get
				evalWithEnv(fn.body, env)
			}
		}
	}

	def evalLibrary(className: String, args: List[Any]) = {
		val clazz: Class[Function] = Class.forName(className).asInstanceOf[Class[Function]]
		val instance: Function = clazz.newInstance()
		val a: Array[Object] = args.toArray.asInstanceOf[Array[Object]]
		instance.apply(a: _*)
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