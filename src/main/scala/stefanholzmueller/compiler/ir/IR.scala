package stefanholzmueller.compiler.ir

import stefanholzmueller.compiler.IntermediateRepresentation
import stefanholzmueller.compiler.Types

sealed trait IR extends IntermediateRepresentation

trait Named {
	def name: String
}
trait Typed {
	def returnType: String
}
trait Ref extends IR with Named with Typed

case class Param(name: String, returnType: String, pos: Int) extends IR with Ref

trait Function extends IR with Ref {
	def params: List[Param]
}
case class LibraryFunction(name: String, returnType: String, params: List[Param]) extends Function
case class UserFunction(name: String, returnType: String, params: List[Param], expr: Expr) extends Function

trait Expr extends IR with Typed
case class IfExpr(condExpr: Expr, thenExpr: Expr, elseExpr: Expr) extends Expr {
	def returnType = if (thenExpr.returnType == elseExpr.returnType) thenExpr.returnType else throw new RuntimeException("mixed types in if: " + thenExpr.returnType + " vs. " + elseExpr.returnType)
}
case class Apply(function: Function, args: List[Expr]) extends Expr {
	def returnType = function.returnType
}

trait Literal[T] extends Expr {
	def value: T
}
case class BoolLit(value: Boolean) extends Literal[Boolean] {
	def returnType = "Bool"
}
case class IntLit(value: Int) extends Literal[Int] {
	def returnType = "Int"
}
case class StrLit(value: String) extends Literal[String] {
	def returnType = "Str"
}

case class Prog(funs: List[Function], expr: Option[Expr]) extends IR