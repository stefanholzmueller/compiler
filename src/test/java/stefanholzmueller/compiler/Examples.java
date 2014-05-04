package stefanholzmueller.compiler;

public class Examples {

	public static final String SUCC = "succ(n: Int): Int = (n `plus` 1)\n(succ 3)";
	public static final String FACT = "fact(n: Int): Int = if (n `lessThan` 2) then 1 else (n `mult` (fact (n `minus` 1))) fi\n(fact 4)";
	public static final String FIB = "fib(n: Int): Int = if (n `lessThan` 2) then 1 else ((fib (n `minus` 1)) `plus` (fib (n `minus` 2))) fi\n(fib 6)";

}
