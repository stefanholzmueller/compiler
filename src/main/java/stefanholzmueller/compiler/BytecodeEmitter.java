package stefanholzmueller.compiler;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import stefanholzmueller.compiler.Generator.ClassFile;
import stefanholzmueller.compiler.Generator.CompilationUnit;

public class BytecodeEmitter implements Emitter {

	@Override
	public void emit(CompilationUnit compilationUnit) {
		byte[] bytecode = compilationUnit.getBytes();
		String fileName = compilationUnit.getName() + ".class";

		try {
			IOUtils.write(bytecode, new FileOutputStream(fileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		AST ast = new MiniParser().parse("minus 321 123");
		ClassFile main = new BytecodeGenerator().generateMain(ast);
		new BytecodeEmitter().emit(main);
	}
}
