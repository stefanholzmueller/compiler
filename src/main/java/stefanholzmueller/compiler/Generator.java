package stefanholzmueller.compiler;

public interface Generator {

	CompilationUnit generateFunction(AbstractSyntaxTree functionDefinition);

	CompilationUnit generateMain(AbstractSyntaxTree expression);

	public interface CompilationUnit {

		String getName();

		byte[] getBytes();

	}

	public class ClassFile implements CompilationUnit {

		private String name;
		private byte[] bytes;

		public ClassFile(String name, byte[] bytes) {
			this.name = name;
			this.bytes = bytes;
		}

		public String getName() {
			return name;
		}

		public byte[] getBytes() {
			return bytes;
		}

	}

}
