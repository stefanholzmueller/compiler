package stefanholzmueller.compiler

import java.util.ArrayList
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.tree._
import stefanholzmueller.compiler.Generator.ClassFile

class BytecodeGenerator extends Generator {

	def generateFunction(functionDefinition: AbstractSyntaxTree): ClassFile = {
		???
	}

	def generateMain(program: AbstractSyntaxTree): ClassFile = program match {
		case Program(_, Some(expression)) => {
			val className = "Main"

			val classNode = new ClassNode(ASM5);
			classNode.version = V1_7;
			classNode.access = ACC_PUBLIC;
			classNode.signature = "L" + className + ";";
			classNode.name = className;
			classNode.superName = "java/lang/Object";

			val mainMethod: MethodNode = new MethodNode(ASM5, ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);

			mainMethod.instructions.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));

			val instructions = generateInstructions(expression)
			for (i <- instructions) {
				mainMethod.instructions.add(i);
			}

			mainMethod.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false));
			mainMethod.instructions.add(new InsnNode(RETURN));

			val m: java.util.List[MethodNode] = classNode.methods.asInstanceOf[java.util.List[MethodNode]]
			m.add(mainMethod);

			val cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(cw);
			val bytes = cw.toByteArray()

			new ClassFile(className, bytes)
		}
		case _ => throw new RuntimeException
	}

	def generateInstructions(expression: AbstractSyntaxTree): List[AbstractInsnNode] = expression match {
		case StringLiteral(s) => List(new LdcInsnNode(s))
		case IntLiteral(i) => { // TODO cleverer alternative to SIPUSH
			List(new TypeInsnNode(NEW, "java/math/BigDecimal"), new InsnNode(DUP), new IntInsnNode(SIPUSH, i), new MethodInsnNode(INVOKESPECIAL, "java/math/BigDecimal", "<init>", "(I)V", false))
		}
		case FunctionApplication(NameIdentifier(n), args) => {
			val name = "stefanholzmueller/compiler/library/desugared/" + n // TODO hack
			List(new TypeInsnNode(NEW, name), new InsnNode(DUP), new MethodInsnNode(INVOKESPECIAL, name, "<init>", "()V", false)) ++ args.map(generateInstructions(_)).flatten ++ List(new MethodInsnNode(INVOKEVIRTUAL, name, "apply", "(Ljava/math/BigDecimal;Ljava/math/BigDecimal;)Ljava/math/BigDecimal;", false))
		}
		case _ => throw new RuntimeException
	}

}