package stefanholzmueller.compiler;

import stefanholzmueller.compiler.Generator.CompilationUnit;

public interface Writer {

	void write(CompilationUnit compilationUnit, String basePath);

}
