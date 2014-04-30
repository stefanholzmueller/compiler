package stefanholzmueller.compiler.library.desugared;

import java.math.BigDecimal;

public class minus {

	public BigDecimal apply(BigDecimal a, BigDecimal b) {
		return a.subtract(b);
	}

}
