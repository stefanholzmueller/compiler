package stefanholzmueller.compiler;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import stefanholzmueller.compiler.Generator.CompilationUnit;

public class BytecodeWriter implements Writer {

	@Override
	public void write(CompilationUnit compilationUnit, String basePath) {
		byte[] bytecode = compilationUnit.getBytes();
		String fileName = basePath + "/" + compilationUnit.getName() + ".class";

		try {
			IOUtils.write(bytecode, new FileOutputStream(fileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
