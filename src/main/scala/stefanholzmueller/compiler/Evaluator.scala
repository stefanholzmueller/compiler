package stefanholzmueller.compiler

import java.math.BigDecimal
import stefanholzmueller.compiler.library.function.Function

class Evaluator {

	def eval(program: AbstractSyntaxTree): Any = program match {
		case Program(fns, main) => {
			if (main.isEmpty) null else evalWithEnv(main.get, fns)
		}
	}

	def evalWithEnv(expression: Expression, env: List[FunctionDefinition]): Any = expression match {
		case IntLiteral(v) => new BigDecimal(v)
		case FunctionApplication(NameIdentifier(name), args) => {
			val optFn = env.find(_ == name)
			if (optFn.isEmpty) {
				evalLibrary("stefanholzmueller.compiler.library.desugared." + name, args.map(evalWithEnv(_, env)))
			} else {
				val fn = optFn.get
				evalWithEnv(fn.body, env)
			}
		}
	}

	def evalWithEnv(expression: Expression, env: Map[String, AST]): Any = expression match {
		case IntLiteral(v) => new BigDecimal(v)
		case FunctionApplication(NameIdentifier(name), args) => {
			val optAst = env.get(name)
			if (optAst.isEmpty) {
				evalLibrary("stefanholzmueller.compiler.library.desugared." + name, args.map(evalWithEnv(_, env)))
			} else {
				optAst.get match {
					case FunctionDefinition(n, r, ps, body) => evalWithEnv(body, env)
					//					case FunctionApplication(n, args) =>
					case e: Expression => evalWithEnv(e, env)
					case _ => throw new RuntimeException
				}
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
