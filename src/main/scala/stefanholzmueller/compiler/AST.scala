package stefanholzmueller.compiler

import scala.util.parsing.input.Positional

sealed trait AST extends AbstractSyntaxTree with Positional
trait Expression extends AST
trait Literal extends Expression
case class BoolLiteral(value: Boolean) extends Literal
case class IntLiteral(value: Int) extends Literal
case class StringLiteral(value: String) extends Literal
case class IfExpression(condExpr: Expression, thenExpr: Expression, elseExpr: Expression) extends Expression

case class FunctionDefinition(name: Identifier, returnType: Identifier, parameters: List[Parameter], body: Expression) extends AST
case class Parameter(nameIdentifier: Identifier, typeIdentifier: Identifier) extends AST
case class Identifier(name: String) extends AST

case class FunctionApplication(name: Identifier, arguments: List[Expression]) extends Expression

case class Program(main: Expression, functionDefinitions: List[FunctionDefinition])