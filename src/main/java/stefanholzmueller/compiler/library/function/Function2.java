package stefanholzmueller.compiler.library.function;

public interface Function2<T1, T2, R> {
	R apply(T1 a, T2 b);
}
