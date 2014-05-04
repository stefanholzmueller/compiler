package stefanholzmueller.compiler

import stefanholzmueller.compiler.ast.BoolLiteral
import stefanholzmueller.compiler.ast.FunctionApplication
import stefanholzmueller.compiler.ast.FunctionDefinition
import stefanholzmueller.compiler.ast.IfExpression
import stefanholzmueller.compiler.ast.IntLiteral
import stefanholzmueller.compiler.ast.NameIdentifier
import stefanholzmueller.compiler.ast.Parameter
import stefanholzmueller.compiler.ast.Program
import stefanholzmueller.compiler.ast.StringLiteral
import stefanholzmueller.compiler.ast.TypeIdentifier
import stefanholzmueller.compiler.ir.Apply
import stefanholzmueller.compiler.ir.BoolLit
import stefanholzmueller.compiler.ir.Expr
import stefanholzmueller.compiler.ir.Fun
import stefanholzmueller.compiler.ir.IfExpr
import stefanholzmueller.compiler.ir.IntLit
import stefanholzmueller.compiler.ir.LibFun
import stefanholzmueller.compiler.ir.Param
import stefanholzmueller.compiler.ir.Prog
import stefanholzmueller.compiler.ir.Ref
import stefanholzmueller.compiler.ir.StrLit
import stefanholzmueller.compiler.ir.UserFun
import stefanholzmueller.compiler.ir.Var

class SemanticAnalyzer extends Analyzer {

	type Env = Map[String, Ref]
	case class NoOp(returnType: Type) extends Expr

	def analyze(ast: AbstractSyntaxTree): IntermediateRepresentation = {
		val library = collection.mutable.ListBuffer[Ref]() // TODO maintain
		library += LibFun("lessThan", Type.BOOL, List(Param("a", Type.INT, 1), Param("b", Type.INT, 2)));
		library += LibFun("minus", Type.INT, List(Param("a", Type.INT, 1), Param("b", Type.INT, 2)));
		library += LibFun("plus", Type.INT, List(Param("a", Type.INT, 1), Param("b", Type.INT, 2)));
		analyzeWithEnv(ast, pairWithName(library.toList))
	}

	private def analyzeWithEnv(ast: AbstractSyntaxTree, env: Env): IntermediateRepresentation = ast match {
		case Program(fds, b) => {
			val env2: Env = env ++ (fds map {
				case fd: FunctionDefinition =>
					(fd.nameIdentifier.name -> (fd match {
						case FunctionDefinition(NameIdentifier(n), TypeIdentifier(rt), ps, expr) => {
							val params = convertParams(ps)
							val env2 = env ++ pairWithName(params)
							UserFun(n, Type.fromString(rt), params, NoOp(Type.STR)) // TODO hack!
						}
					}))
			})
			Prog(recurseList(fds, env2).asInstanceOf[List[UserFun]], b.map(e => analyzeWithEnv(e, env2).asInstanceOf[Expr]))
		}
		case FunctionDefinition(NameIdentifier(n), TypeIdentifier(rt), ps, expr) => {
			val params = convertParams(ps)
			val env2 = env ++ pairWithName(params)
			UserFun(n, Type.fromString(rt), params, analyzeWithEnv(expr, env2).asInstanceOf[Expr])
		}
		case FunctionApplication(NameIdentifier(n), args) => env.get(n) match {
			case Some(LibFun(n, rt, ps)) => Apply("stefanholzmueller/compiler/library/" + n, rt, recurseList(args, env).asInstanceOf[List[Expr]])
			case Some(UserFun(n, rt, ps, expr)) => Apply(n, rt, recurseList(args, env).asInstanceOf[List[Expr]])
			case Some(Param(n, rt, pos)) => {
				if (args.isEmpty) Var(n, rt, pos)
				else throw new RuntimeException("parameter called with arguments")
			}
			case Some(_) => throw new AssertionError("unhandled Ref")
			case None => throw new RuntimeException("unbound reference")
		}
		case IfExpression(c, t, e) => IfExpr(analyzeWithEnv(c, env).asInstanceOf[Expr], analyzeWithEnv(t, env).asInstanceOf[Expr], analyzeWithEnv(e, env).asInstanceOf[Expr])
		case IntLiteral(v) => IntLit(v)
		case BoolLiteral(v) => BoolLit(v)
		case StringLiteral(v) => StrLit(v)
	}

	private def recurseList(list: List[AbstractSyntaxTree], env: Env): List[IntermediateRepresentation] = list.map(a => analyzeWithEnv(a, env))

	private def convertParams(ps: List[Parameter]): List[Param] = ps.zipWithIndex map { case (Parameter(NameIdentifier(n), TypeIdentifier(t)), i) => Param(n, Type.fromString(t), i) }
	private def pairWithName(refs: List[Ref]): Env = refs.map(r => (r.name -> r)).toMap

}