package stefanholzmueller.compiler

object REPL {
	val parser = new MiniParser
	def main(args: Array[String]) = {
		while (true) {
			val exprSrc = readLine("repl> ")
			import parser.{ Success, NoSuccess }
			parser.parseResult(exprSrc) match {
				case Success(expr, _) => println("Parsed: " + expr)
				case err: NoSuccess => println(err)
			}
		}
	}
}