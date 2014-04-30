package stefanholzmueller.compiler;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;

public interface Generator {

	CompilationUnit generateFunction(AbstractSyntaxTree functionDefinition);

	CompilationUnit generateMain(AbstractSyntaxTree expression);

	List<AbstractInsnNode> generateInstructions(AbstractSyntaxTree expression);

	public interface CompilationUnit {

		String getName();

		byte[] getBytes();

	}

}
