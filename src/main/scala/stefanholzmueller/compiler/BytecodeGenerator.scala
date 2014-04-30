package stefanholzmueller.compiler

import java.util.ArrayList

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.tree._
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode

import stefanholzmueller.compiler.asm.ClassFile

class BytecodeGenerator extends Generator {

	def generateFunction(functionDefinition: AbstractSyntaxTree): ClassFile = {
		???
	}

	def generateMain(expression: AbstractSyntaxTree): ClassFile = {
		val classNode = new ClassNode(ASM5);
		classNode.version = V1_7;
		classNode.access = ACC_PUBLIC;
		classNode.signature = "LMain;";
		classNode.name = "Main";
		classNode.superName = "java/lang/Object";

		val mainMethod: MethodNode = new MethodNode(ASM5, ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);

		mainMethod.instructions.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
		mainMethod.instructions.add(new LdcInsnNode("Hello, World!"));
		mainMethod.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
		mainMethod.instructions.add(new InsnNode(RETURN));

		val m: java.util.List[MethodNode] = classNode.methods.asInstanceOf[java.util.List[MethodNode]]
		m.add(mainMethod);

		val cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(cw);
		val bytes = cw.toByteArray();
		new ClassFile("Main", bytes)
	}

	def generateInstructions(expression: AbstractSyntaxTree): java.util.List[AbstractInsnNode] = {
		new ArrayList
	}

}