package stefanholzmueller.compiler

sealed trait AST
case class Variable(name: String) extends AST