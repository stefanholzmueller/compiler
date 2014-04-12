package stefanholzmueller.compiler

import scala.util.parsing.input.Positional

sealed trait AST extends AbstractSyntaxTree with Positional
case class IntLiteral(value: Int) extends AST
case class Variable(name: String) extends AST