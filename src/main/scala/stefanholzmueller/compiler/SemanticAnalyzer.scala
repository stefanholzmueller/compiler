package stefanholzmueller.compiler

class SemanticAnalyzer extends Analyzer {

	sealed trait Name
	case class LibraryFunctionName(name: String) extends Name
	case class UserFunctionName(name: String) extends Name
	case class ParameterName(name: String) extends Name

	def analyze[T <: AbstractSyntaxTree](ast: T): T = {
		val env = Map[String, Name]()
		val env2 = env.updated("plus", LibraryFunctionName("plus")) // TODO maintain
		analyzeWithEnv(ast, env2)
	}

	def analyzeWithEnv[T <: AbstractSyntaxTree](ast: T, env: Map[String, Name]): T = (ast match {
		case Program(fds, b) => {
			val env2 = env ++ (fds.map(fd => (fd.nameIdentifier.name, UserFunctionName(fd.nameIdentifier.name))))
			Program(fds.map(fd => analyzeWithEnv(fd, env2)), b.map(e => analyzeWithEnv(e, env2)))
		}
		case FunctionDefinition(ni, rt, ps, body) => {
			val env2 = env ++ (ps.map(p => (p.nameIdentifier.name, ParameterName(p.nameIdentifier.name))))
			FunctionDefinition(ni, rt, ps, analyzeWithEnv(body, env2))
		}
		case FunctionApplication(NameIdentifier(n), args) => env.get(n) match {
			case Some(LibraryFunctionName(n)) => LibraryFunctionApplication(NameIdentifier(n), args.map(a => analyzeWithEnv(a, env)))
			case Some(UserFunctionName(n)) => UserFunctionApplication(NameIdentifier(n), args.map(a => analyzeWithEnv(a, env)))
			case Some(ParameterName(n)) => if (args.isEmpty) Variable(NameIdentifier(n)) else throw new RuntimeException("parameter called with arguments")
			case None => throw new RuntimeException("unbound variable")
		}
		case IfExpression(c, t, e) => IfExpression(analyzeWithEnv(c, env), analyzeWithEnv(t, env), analyzeWithEnv(e, env))
		case x => x
	}).asInstanceOf[T]
}