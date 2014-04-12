package stefanholzmueller.compiler

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.PackratParsers

class MiniParser extends Parser with StdTokenParsers with PackratParsers {
	type Tokens = StdLexical
	val lexical = new StdLexical
	lexical.delimiters ++= Seq("(", ")")
	lexical.reserved ++= Seq("if", "then", "else", "fi")

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

	lazy val ast: PackratParser[AST] = variable | numLiteral
	lazy val variable: PackratParser[Variable] = ident ^^ Variable
	lazy val intLiteral: PackratParser[IntLiteral] = rep("0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9") ^^ parseIntLiteral
	lazy val numLiteral: PackratParser[IntLiteral] = numericLit ^^ parseNumLiteral

	def parseIntLiteral(digits: List[String]): IntLiteral = {
		val str = digits.mkString("")
		val int = java.lang.Integer.parseInt(str)
		IntLiteral(int)
	}
	def parseNumLiteral(str: String): IntLiteral = {
		val int = java.lang.Integer.parseInt(str)
		IntLiteral(int)
	}

}