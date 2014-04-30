package stefanholzmueller.compiler;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import stefanholzmueller.compiler.Generator.CompilationUnit;
import stefanholzmueller.compiler.asm.AsmBytecodeGenerator;
import stefanholzmueller.compiler.asm.ClassFile;

public class BytecodeEmitter implements Emitter {

	@Override
	public void emit(CompilationUnit compilationUnit) {
		byte[] bytecode = compilationUnit.getBytes();
		String fileName = compilationUnit.getName() + ".class";

		try {
			IOUtils.write(bytecode, new FileOutputStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ClassFile generateFunctionClass = new AsmBytecodeGenerator()
				.generateFunctionClass();
		new BytecodeEmitter().emit(generateFunctionClass);
	}
}
