import java.util.function.IntBinaryOperator;

public class addInt implements IntBinaryOperator {

	@Override
	public int applyAsInt(int left, int right) {
		return left + right;
	}

	public int apply(int left, int right) {
		return left + right;
	}
}
