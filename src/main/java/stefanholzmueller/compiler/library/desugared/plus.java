package stefanholzmueller.compiler.library.desugared;

import java.math.BigDecimal;

import stefanholzmueller.compiler.library.function.Function2;

public class plus implements Function2<BigDecimal, BigDecimal, BigDecimal> {

	@Override
	public BigDecimal apply(BigDecimal a, BigDecimal b) {
		return a.add(b);
	}

}
