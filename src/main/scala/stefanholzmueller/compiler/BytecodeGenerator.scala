package stefanholzmueller.compiler

import java.util.ArrayList
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.tree._
import stefanholzmueller.compiler.Generator.ClassFile
import stefanholzmueller.compiler.Generator.CompilationUnit
import stefanholzmueller.compiler.ast.TypeIdentifier
import stefanholzmueller.compiler.ast.StringLiteral
import stefanholzmueller.compiler.ast.Program
import stefanholzmueller.compiler.ast.IntLiteral
import stefanholzmueller.compiler.ast.IfExpression
import stefanholzmueller.compiler.ast.Expression
import stefanholzmueller.compiler.ast.BoolLiteral

class BytecodeGenerator extends Generator {

	def generate(ir: IntermediateRepresentation): java.util.Collection[CompilationUnit] = {
		???
	}

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
		case x => throw new RuntimeException(x.toString())
	}

	def generateInstructions(expression: AbstractSyntaxTree): List[AbstractInsnNode] = expression match {
		case BoolLiteral(i) => {
			val iconst = if (i) ICONST_1 else ICONST_0
			List(new TypeInsnNode(NEW, Types.BOOL), new InsnNode(DUP), new InsnNode(iconst), new MethodInsnNode(INVOKESPECIAL, Types.BOOL, "<init>", "(Z)V", false))
		}
		case IntLiteral(i) => { // TODO cleverer alternative to SIPUSH
			List(new TypeInsnNode(NEW, Types.INT), new InsnNode(DUP), new IntInsnNode(SIPUSH, i), new MethodInsnNode(INVOKESPECIAL, Types.INT, "<init>", "(I)V", false))
		}
		case StringLiteral(s) => List(new LdcInsnNode(s))
		case IfExpression(c, t, e) => {
			val l1 = new LabelNode
			val l2 = new LabelNode
			generateInstructions(c) ++ List(new MethodInsnNode(INVOKEVIRTUAL, Types.BOOL, "booleanValue", "()Z", false), new JumpInsnNode(IFEQ, l1)) ++ generateInstructions(t) ++ List(new JumpInsnNode(GOTO, l2), l1) ++ generateInstructions(e) ++ List(l2)
		}
		//		case LibraryFunctionApplication(NameIdentifier(n), args, rt) => {
		//			val name = "stefanholzmueller/compiler/library/" + n
		//			List(new TypeInsnNode(NEW, name), new InsnNode(DUP), new MethodInsnNode(INVOKESPECIAL, name, "<init>", "()V", false)) ++ args.map(generateInstructions(_)).flatten ++ List(new MethodInsnNode(INVOKEVIRTUAL, name, "apply", deduceDescription(args, rt), false))
		//		}
		//		case UserFunctionApplication(NameIdentifier(name), args, rt) => {
		//			List(new TypeInsnNode(NEW, name), new InsnNode(DUP), new MethodInsnNode(INVOKESPECIAL, name, "<init>", "()V", false)) ++ args.map(generateInstructions(_)).flatten ++ List(new MethodInsnNode(INVOKEVIRTUAL, name, "apply", deduceDescription(args, rt), false))
		//		}
		//		case Variable(n, rt, p) => ???
		case x => throw new RuntimeException(x.toString())
	}

	private def bytecodify(ti: TypeIdentifier): String = if (ti.name.size == 1) ti.name else "L" + ti.name.replaceAll("\\.", "/") + ";"
	private def description(pts: List[TypeIdentifier], rt: TypeIdentifier): String = "(" + pts.map(bytecodify).mkString + ")" + bytecodify(rt)
	private def deduceDescription(args: List[Expression], rt: TypeIdentifier): String = description(deduceTypes(args), rt)

	private def deduceTypes(args: List[Expression]): List[TypeIdentifier] = args map {
		case BoolLiteral(v) => TypeIdentifier(Types.BOOL)
		case IntLiteral(v) => TypeIdentifier(Types.INT)
		case StringLiteral(v) => TypeIdentifier(Types.STRING)
		//		case semanticFunctionApplication: SemanticFunctionApplication => semanticFunctionApplication.returnType
		case x => throw new RuntimeException(x.toString())
	}

}