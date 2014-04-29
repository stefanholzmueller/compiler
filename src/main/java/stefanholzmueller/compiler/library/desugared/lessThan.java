package stefanholzmueller.compiler.library.desugared;

import java.math.BigDecimal;

import stefanholzmueller.compiler.library.function.Function2;

public class lessThan implements Function2<BigDecimal, BigDecimal, Boolean> {

	@Override
	public Boolean apply(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) < 0;
	}

}
