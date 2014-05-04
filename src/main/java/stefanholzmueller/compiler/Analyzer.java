package stefanholzmueller.compiler;

public interface Analyzer {

	IntermediateRepresentation analyze(AbstractSyntaxTree ast);

}
