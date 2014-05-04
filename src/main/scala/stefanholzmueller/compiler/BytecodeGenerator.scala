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
import stefanholzmueller.compiler.ir.Prog
import stefanholzmueller.compiler.ir.UserFun
import stefanholzmueller.compiler.ir.Expr
import stefanholzmueller.compiler.ir.BoolLit
import stefanholzmueller.compiler.ir.IntLit
import stefanholzmueller.compiler.ir.StrLit
import stefanholzmueller.compiler.ir.IfExpr
import stefanholzmueller.compiler.ir.LibFun
import stefanholzmueller.compiler.ir.LibFun
import stefanholzmueller.compiler.ir.Apply
import stefanholzmueller.compiler.ir.Var
import stefanholzmueller.compiler.ir.Typed

class BytecodeGenerator extends Generator {

	def generate(ir: IntermediateRepresentation): java.util.Collection[CompilationUnit] = ir match {
		case Prog(funs, Some(expr)) => {
			val cus = new java.util.ArrayList[CompilationUnit]()
			for (f <- funs) {
				cus.add(generateFunction(f))
			}
			cus.add(generateMain(expr))
			cus
		}
		case otherwise => throw new RuntimeException(otherwise.toString())
	}

	def toClassFile(classNode: ClassNode): ClassFile = {
		val cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(cw);
		val bytes = cw.toByteArray()

		new ClassFile(classNode.name, bytes)
	}

	def generateFunction(fun: UserFun): ClassFile = {
		val className = fun.name

		val classNode = new ClassNode(ASM5);
		classNode.version = V1_7;
		classNode.access = ACC_PUBLIC;
		classNode.signature = "L" + className + ";";
		classNode.name = className;
		classNode.superName = "java/lang/Object";

		val m: java.util.List[MethodNode] = classNode.methods.asInstanceOf[java.util.List[MethodNode]]
		m.add(generateFunctionConstructor());
		m.add(generateFunctionMethod(fun));

		toClassFile(classNode)
	}

	def generateFunctionConstructor(): MethodNode = {
		val constructor: MethodNode = new MethodNode(ASM5, ACC_PUBLIC, "<init>", "()V", null, null);

		constructor.instructions.add(new VarInsnNode(ALOAD, 0))
		constructor.instructions.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
		constructor.instructions.add(new InsnNode(RETURN));

		constructor
	}

	def generateFunctionMethod(fun: UserFun): MethodNode = fun match {
		case UserFun(n, rt, ps, expr) => {
			val method: MethodNode = new MethodNode(ASM5, ACC_PUBLIC, "apply", description(ps, rt), null, null);

			val instructions = generateInstructions(expr)
			for (i <- instructions) {
				method.instructions.add(i);
			}
			method.instructions.add(new InsnNode(ARETURN));

			method
		}
	}

	def generateMain(expr: Expr): ClassFile = {
		val className = "Main"

		val classNode = new ClassNode(ASM5);
		classNode.version = V1_7;
		classNode.access = ACC_PUBLIC;
		classNode.signature = "L" + className + ";";
		classNode.name = className;
		classNode.superName = "java/lang/Object";

		val mainMethod = generateMainMethod(expr)

		val m: java.util.List[MethodNode] = classNode.methods.asInstanceOf[java.util.List[MethodNode]]
		m.add(mainMethod);

		toClassFile(classNode)
	}

	def generateMainMethod(expr: Expr): MethodNode = {
		val mainMethod: MethodNode = new MethodNode(ASM5, ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);

		mainMethod.instructions.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));

		val instructions = generateInstructions(expr)
		for (i <- instructions) {
			mainMethod.instructions.add(i);
		}

		mainMethod.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false));
		mainMethod.instructions.add(new InsnNode(RETURN));
		mainMethod
	}

	def generateInstructions(expr: Expr): List[AbstractInsnNode] = expr match {
		case BoolLit(value) => {
			val iconst = if (value) ICONST_1 else ICONST_0
			List(new TypeInsnNode(NEW, Type.BOOL.getJavaName()), new InsnNode(DUP), new InsnNode(iconst), new MethodInsnNode(INVOKESPECIAL, Type.BOOL.getJavaName(), "<init>", "(Z)V", false))
		}
		case IntLit(value) => { // TODO cleverer alternative to SIPUSH
			List(new TypeInsnNode(NEW, Type.INT.getJavaName()), new InsnNode(DUP), new IntInsnNode(SIPUSH, value), new MethodInsnNode(INVOKESPECIAL, Type.INT.getJavaName(), "<init>", "(I)V", false))
		}
		case StrLit(value) => {
			List(new LdcInsnNode(value))
		}
		case IfExpr(c, t, e) => {
			val l1 = new LabelNode
			val l2 = new LabelNode
			generateInstructions(c) ++ List(new MethodInsnNode(INVOKEVIRTUAL, Type.BOOL.getJavaName(), "booleanValue", "()Z", false), new JumpInsnNode(IFEQ, l1)) ++ generateInstructions(t) ++ List(new JumpInsnNode(GOTO, l2), l1) ++ generateInstructions(e) ++ List(l2)
		}
		case Apply(n, rt, args) => {
			List(new TypeInsnNode(NEW, n), new InsnNode(DUP), new MethodInsnNode(INVOKESPECIAL, n, "<init>", "()V", false)) ++ args.map(generateInstructions(_)).flatten ++ List(new MethodInsnNode(INVOKEVIRTUAL, n, "apply", description(args, rt), false))
		}
		case Var(n, rt, pos) => {
			List(new VarInsnNode(ALOAD, pos + 1))
		}
		case x => throw new RuntimeException(x.toString())
	}

	private def description(pts: List[Typed], rt: Type): String = "(" + pts.map(_.internalType).mkString + ")" + rt.getInternalType()

}