package stefanholzmueller.compiler

import scala.beans.BeanProperty
import stefanholzmueller.compiler.asm.AsmBytecodeGenerator;
import stefanholzmueller.compiler.asm.ClassFile;

class BytecodeGenerator extends Generator {

	val asm = new AsmBytecodeGenerator

	def generate(fd: FunctionDefinition): ClassFile = fd match {
		case FunctionDefinition(n, r, ps, b) => asm.generateFunctionClass
	}

}