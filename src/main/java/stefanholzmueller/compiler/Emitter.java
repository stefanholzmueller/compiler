package stefanholzmueller.compiler;

import stefanholzmueller.compiler.Generator.CompilationUnit;

public interface Emitter {

	void emit(CompilationUnit compilationUnit);

}
