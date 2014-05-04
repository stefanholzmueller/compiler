package stefanholzmueller.compiler.library;

import java.math.BigDecimal;

public class lessThan {

	public Boolean apply(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) < 0;
	}

}
