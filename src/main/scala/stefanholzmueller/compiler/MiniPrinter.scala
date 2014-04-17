package stefanholzmueller.compiler

class MiniPrinter extends Printer {
	def print(ast: AbstractSyntaxTree): String = {
		ast match {
			case Variable(name) => name
			case BoolLiteral(value) => java.lang.Boolean.toString(value)
			case IntLiteral(value) => Integer.toString(value)
			case StringLiteral(value) => "\"" + value + "\""
			case IfExpression(c, t, e) => "if " + print(c) + " then " + print(t) + " else " + print(e) + " fi"
			case FunctionDefinition(n, r, p, b) => print(n) + "(" + p.map(print(_)).mkString(", ") + "): " + print(r) + " = " + print(b)
			case Parameter(n, t) => print(n) + ": " + print(t)
			case Identifier(n) => n
			case x: AST => x.toString()
		}
	}
}