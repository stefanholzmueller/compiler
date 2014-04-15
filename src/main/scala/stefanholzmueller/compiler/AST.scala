package stefanholzmueller.compiler

import scala.util.parsing.input.Positional

sealed trait AST extends AbstractSyntaxTree with Positional
case class BoolLiteral(value: Boolean) extends AST
case class IntLiteral(value: Int) extends AST
case class Variable(name: String) extends AST