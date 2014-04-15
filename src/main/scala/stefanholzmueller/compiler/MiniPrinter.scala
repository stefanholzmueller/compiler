package stefanholzmueller.compiler

class MiniPrinter extends Printer {
	def print(ast: AbstractSyntaxTree): String = {
		ast match {
			case Variable(name) => name
			case BoolLiteral(value) => java.lang.Boolean.toString(value)
			case IntLiteral(value) => Integer.toString(value)
			case x: AST => x.toString()
		}
	}
}