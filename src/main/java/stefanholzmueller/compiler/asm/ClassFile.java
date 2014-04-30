package stefanholzmueller.compiler.asm;

import stefanholzmueller.compiler.Generator.CompilationUnit;

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