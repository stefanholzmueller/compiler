package stefanholzmueller.compiler.library.function;


public interface Function2<T1, T2, R> extends Function {
	R apply(T1 a, T2 b);
}
