package stefanholzmueller.compiler

import scala.util.parsing.input.Positional

sealed trait AST extends AbstractSyntaxTree with Positional
trait Expression extends AST
trait Literal extends Expression

case class BoolLiteral(value: Boolean) extends Literal
case class IntLiteral(value: Int) extends Literal
case class StringLiteral(value: String) extends Literal
case class IfExpression(condExpr: Expression, thenExpr: Expression, elseExpr: Expression) extends Expression

case class FunctionDefinition(nameIdentifier: NameIdentifier, returnType: TypeIdentifier, parameters: List[Parameter], body: Expression) extends AST
case class Parameter(nameIdentifier: NameIdentifier, typeIdentifier: TypeIdentifier) extends AST
case class NameIdentifier(name: String) extends AST
case class TypeIdentifier(name: String) extends AST

case class FunctionApplication(nameIdentifier: NameIdentifier, arguments: List[Expression]) extends Expression
case class LibraryFunctionApplication(nameIdentifier: NameIdentifier, arguments: List[Expression]) extends SemanticFunctionApplication
case class UserFunctionApplication(nameIdentifier: NameIdentifier, arguments: List[Expression]) extends SemanticFunctionApplication

case class Program(functionDefinitions: List[FunctionDefinition], main: Option[Expression]) extends AST

trait IntermediateRepresentation {
	//	def returnType: TypeIdentifier
}
trait SemanticFunctionApplication extends Expression with IntermediateRepresentation
trait PseudoFunctionApplication extends SemanticFunctionApplication

case class Variable(nameIdentifier: NameIdentifier) extends PseudoFunctionApplication
