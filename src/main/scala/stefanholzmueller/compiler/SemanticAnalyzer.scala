package stefanholzmueller.compiler

class SemanticAnalyzer extends Analyzer {

	sealed trait Name extends IntermediateRepresentation {
		def name: String
	}
	case class LibraryFunctionName(name: String, returnType: TypeIdentifier) extends Name
	case class UserFunctionName(name: String, returnType: TypeIdentifier) extends Name
	case class ParameterName(name: String, returnType: TypeIdentifier) extends Name

	def analyze[T <: AbstractSyntaxTree](ast: T): T = {
		val env = collection.mutable.HashSet[Name]() // TODO maintain
		env += LibraryFunctionName("lessThan", TypeIdentifier(Types.BOOL));
		env += LibraryFunctionName("minus", TypeIdentifier(Types.INT));
		env += LibraryFunctionName("plus", TypeIdentifier(Types.INT));
		env += LibraryFunctionName("println", TypeIdentifier("V"));
		analyzeWithEnv(ast, env.map(e => (e.name, e)).toMap)
	}

	def analyzeWithEnv[T <: AbstractSyntaxTree](ast: T, env: Map[String, Name]): T = (ast match {
		case Program(fds, b) => {
			val env2 = env ++ (fds.map(fd => (fd.nameIdentifier.name, UserFunctionName(fd.nameIdentifier.name, fd.returnType))))
			Program(fds.map(fd => analyzeWithEnv(fd, env2)), b.map(e => analyzeWithEnv(e, env2)))
		}
		case FunctionDefinition(ni, rt, ps, body) => {
			val env2 = env ++ (ps.map(p => (p.nameIdentifier.name, ParameterName(p.nameIdentifier.name, p.typeIdentifier))))
			FunctionDefinition(ni, rt, ps, analyzeWithEnv(body, env2))
		}
		case FunctionApplication(NameIdentifier(n), args) => env.get(n) match {
			case Some(LibraryFunctionName(n, rt)) => LibraryFunctionApplication(NameIdentifier(n), args.map(a => analyzeWithEnv(a, env)), rt)
			case Some(UserFunctionName(n, rt)) => UserFunctionApplication(NameIdentifier(n), args.map(a => analyzeWithEnv(a, env)), rt)
			case Some(ParameterName(n, rt)) => if (args.isEmpty) Variable(NameIdentifier(n), rt) else throw new RuntimeException("parameter called with arguments")
			case None => throw new RuntimeException("unbound variable")
		}
		case IfExpression(c, t, e) => IfExpression(analyzeWithEnv(c, env), analyzeWithEnv(t, env), analyzeWithEnv(e, env))
		case x => x
	}).asInstanceOf[T]
}