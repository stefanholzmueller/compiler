package stefanholzmueller.compiler.library.desugared;

import stefanholzmueller.compiler.library.function.Function1;

public class println implements Function1<Object, Void> {

	@Override
	public Void apply(Object o) {
		System.out.println(o);
		return null;
	}

}
