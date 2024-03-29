package stefanholzmueller.compiler

import stefanholzmueller.compiler.ast.TypeIdentifier

import stefanholzmueller.compiler.ast.StringLiteral

import stefanholzmueller.compiler.ast.Program

import stefanholzmueller.compiler.ast.Parameter

import stefanholzmueller.compiler.ast.NameIdentifier

import stefanholzmueller.compiler.ast.IntLiteral

import stefanholzmueller.compiler.ast.IfExpression

import stefanholzmueller.compiler.ast.FunctionDefinition

import stefanholzmueller.compiler.ast.FunctionApplication

import stefanholzmueller.compiler.ast.BoolLiteral

import stefanholzmueller.compiler.ast.AST

class PrettyPrinter extends Printer {
	def print(ast: AbstractSyntaxTree): String = {
		ast match {
			case Program(fns, main) => printList(fns, "\n\n") + (if (!fns.isEmpty && !main.isEmpty) "\n\n" else "") + main.map(print(_)).getOrElse("")
			case BoolLiteral(value) => java.lang.Boolean.toString(value)
			case IntLiteral(value) => Integer.toString(value)
			case StringLiteral(value) => "\"" + value + "\""
			case IfExpression(c, t, e) => "if " + print(c) + " then " + print(t) + " else " + print(e) + " fi"
			case FunctionDefinition(n, r, params, b) => print(n) + "(" + printList(params, ", ") + "): " + print(r) + " = " + print(b)
			case Parameter(n, t) => print(n) + ": " + print(t)
			case NameIdentifier(n) => n
			case TypeIdentifier(n) => n
			case FunctionApplication(n, args) => print(n) + (if (args.isEmpty) "" else " ") + printList(args, " ")
			case x: AST => x.toString()
		}
	}

	def printList(list: List[AST], separator: String) = {
		list.map(print(_)).mkString(separator)
	}
}