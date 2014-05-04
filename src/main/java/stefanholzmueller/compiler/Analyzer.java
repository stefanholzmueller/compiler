package stefanholzmueller.compiler;

public interface Analyzer {

	<T extends AbstractSyntaxTree> T analyze(T ast);

}
