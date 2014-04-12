package stefanholzmueller.compiler

object REPL {
	def main(args: Array[String]) = {
		val parser = new Parser
		while (true) {
			val exprSrc = readLine("repl> ")
			import parser.{ Success, NoSuccess }
			parser.parse(exprSrc) match {
				case Success(expr, _) => println("Parsed: " + expr)
				case err: NoSuccess => println(err)
			}
		}
	}
}