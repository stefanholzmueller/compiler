package stefanholzmueller.compiler

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.PackratParsers

class MiniParser extends Parser with StdTokenParsers with PackratParsers {
	type Tokens = StdLexical
	val lexical = new StdLexical
	lexical.delimiters ++= Seq("(", ")", "=", ":", ",")
	lexical.reserved ++= Seq("true", "false", "if", "then", "else", "fi")

	def parseResult(source: String): ParseResult[AST] = {
		val tokens = new lexical.Scanner(source)
		phrase(program)(tokens)
	}

	def parse(source: String): AST = {
		parseResult(source) match {
			case Success(program, _) => program
			case err: NoSuccess => throw new RuntimeException(err.toString())
		}
	}

	type P[+T] = PackratParser[T]
	lazy val expression: P[Expression] = explicitParens | functionApplication | literal | ifExpression
	lazy val explicitParens = "(" ~> expression <~ ")"
	lazy val literal: P[Literal] = boolLiteral | intLiteral | stringLiteral
	lazy val boolLiteral: P[BoolLiteral] = ("true" | "false") ^^ (str => BoolLiteral(java.lang.Boolean.parseBoolean(str)))
	lazy val intLiteral: P[IntLiteral] = numericLit ^^ (str => IntLiteral(java.lang.Integer.parseInt(str)))
	lazy val stringLiteral: P[StringLiteral] = stringLit ^^ (str => StringLiteral(str)) // TODO escaping broken
	lazy val ifExpression: P[IfExpression] = "if" ~ expression ~ "then" ~ expression ~ "else" ~ expression ~ "fi" ^^ { case "if" ~ condExpr ~ "then" ~ thenExpr ~ "else" ~ elseExpr ~ "fi" => IfExpression(condExpr, thenExpr, elseExpr) }

	lazy val functionDefinition: P[FunctionDefinition] = nameIdentifier ~ parameterList ~ ":" ~ typeIdentifier ~ "=" ~ expression ^^ { case name ~ parameterList ~ ":" ~ returnType ~ "=" ~ body => FunctionDefinition(name, returnType, parameterList, body) }
	lazy val parameterList: P[List[Parameter]] = "(" ~> repsep(parameter, ",") <~ ")"
	lazy val parameter: P[Parameter] = nameIdentifier ~ ":" ~ typeIdentifier ^^ { case nameIdentifier ~ ":" ~ typeIdentifier => Parameter(nameIdentifier, typeIdentifier) }
	lazy val nameIdentifier: P[Identifier] = ident ^^ Identifier
	lazy val typeIdentifier: P[Identifier] = ident ^^ Identifier

	lazy val functionApplication: P[FunctionApplication] = nameIdentifier ~ rep(expression) ^^ { case nameIdentifier ~ arguments => FunctionApplication(nameIdentifier, arguments) }
	lazy val program: P[Program] = rep(functionDefinition) ~ opt(expression) ^^ { case functionDefinitions ~ expression => Program(functionDefinitions, expression) }

}