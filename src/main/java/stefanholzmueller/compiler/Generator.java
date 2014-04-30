package stefanholzmueller.compiler;

import stefanholzmueller.compiler.asm.ClassFile;

public interface Generator {

	ClassFile generate(FunctionDefinition functionDefinition);

	public interface CompilationUnit {

		String getName();

		byte[] getBytes();

	}

}
