import stefanholzmueller.compiler.Function;

public class hello implements Function {
	@Override
	public Object apply(Object... args) {
		return "Hello, World!";
	}
}
