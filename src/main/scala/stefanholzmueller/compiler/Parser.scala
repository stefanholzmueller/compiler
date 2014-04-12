package stefanholzmueller.compiler

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.PackratParsers

class Parser extends StdTokenParsers with PackratParsers {
	type Tokens = StdLexical
	val lexical = new StdLexical
	lexical.delimiters ++= Seq("(", ")")
	lexical.reserved ++= Seq("if", "then", "else", "fi")

	def parse(source: String): ParseResult[String] = {
		val tokens = new lexical.Scanner(source)
		phrase(ident)(tokens)
	}

	lazy val expression: PackratParser[AST] = variable
	lazy val variable: PackratParser[Variable] = ident ^^ Variable
}