package stefanholzmueller.compiler

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.PackratParsers

class MiniParser extends Parser with StdTokenParsers with PackratParsers {
	type Tokens = StdLexical
	val lexical = new StdLexical
	lexical.delimiters ++= Seq("(", ")", ";")
	lexical.reserved ++= Seq("true", "false", "if", "then", "else", "fi")

	def parseResult(source: String): ParseResult[AST] = {
		val tokens = new lexical.Scanner(source)
		phrase(ast)(tokens)
	}

	def parse(source: String): AST = {
		parseResult(source) match {
			case Success(expr, _) => expr
			case err: NoSuccess => throw new RuntimeException(err.toString())
		}
	}

	lazy val ast: PackratParser[AST] = expression
	lazy val expression: PackratParser[Expression] = variable | literal
	lazy val literal: PackratParser[Literal] = boolLiteral | intLiteral | stringLiteral
	lazy val variable: PackratParser[Variable] = ident ^^ Variable
	lazy val boolLiteral: PackratParser[BoolLiteral] = ("true" | "false") ^^ (str => BoolLiteral(java.lang.Boolean.parseBoolean(str)))
	lazy val intLiteral: PackratParser[IntLiteral] = numericLit ^^ (str => IntLiteral(java.lang.Integer.parseInt(str)))
	lazy val stringLiteral: PackratParser[StringLiteral] = stringLit ^^ (str => StringLiteral(str))

}