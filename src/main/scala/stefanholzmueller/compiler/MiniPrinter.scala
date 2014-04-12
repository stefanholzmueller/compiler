package stefanholzmueller.compiler

class MiniPrinter extends Printer {
	def print(ast: AbstractSyntaxTree): String = {
		ast match {
			case Variable(name) => name
			case IntLiteral(value) => Integer.toString(value)
		}
	}
}