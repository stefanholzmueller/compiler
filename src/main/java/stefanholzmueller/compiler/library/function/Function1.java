package stefanholzmueller.compiler.library.function;


public interface Function1<T1, R> extends Function {
	R apply(T1 t);
}
