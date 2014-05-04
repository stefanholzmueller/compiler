package stefanholzmueller.compiler.ir

import stefanholzmueller.compiler.IntermediateRepresentation
import stefanholzmueller.compiler.Type

sealed trait IR extends IntermediateRepresentation

trait Named {
	def name: String
}
trait Typed {
	def returnType: Type
	def javaType: String = returnType.getJavaName()
	def internalType: String = returnType.getInternalType()
}
trait Ref extends IR with Named with Typed

case class Param(name: String, returnType: Type, pos: Int) extends IR with Ref
case class Var(name: String, returnType: Type, paramPos: Int) extends IR with Expr

trait Fun extends IR with Ref {
	def params: List[Param]
}
case class LibFun(name: String, returnType: Type, params: List[Param]) extends Fun
case class UserFun(name: String, returnType: Type, params: List[Param], expr: Expr) extends Fun

trait Expr extends IR with Typed
case class IfExpr(condExpr: Expr, thenExpr: Expr, elseExpr: Expr) extends Expr {
	def returnType = if (thenExpr.returnType == elseExpr.returnType) thenExpr.returnType else throw new RuntimeException("mixed types in if: " + thenExpr.returnType + " vs. " + elseExpr.returnType)
}
case class Apply(name: String, returnType: Type, args: List[Expr]) extends Expr

trait Literal[T] extends Expr {
	def value: T
}
case class BoolLit(value: Boolean) extends Literal[Boolean] {
	def returnType = Type.BOOL
}
case class IntLit(value: Int) extends Literal[Int] {
	def returnType = Type.INT
}
case class StrLit(value: String) extends Literal[String] {
	def returnType = Type.STR
}

case class Prog(funs: List[UserFun], expr: Option[Expr]) extends IR