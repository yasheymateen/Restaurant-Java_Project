package restaurant.utilities;

import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicIdGen implements IdGen {

	private final AtomicInteger currentValue;

	private final int incrementor;

	private AtomicIdGen(int startingValue, int incrementor) {
		currentValue = new AtomicInteger(startingValue);
		this.incrementor = incrementor;
	}

	public static AtomicIdGen createDefaultIdGen() {
		return new AtomicIdGen(1, 1);
	}

	public static AtomicIdGen createIdGenWithStartingValue(int startingValue) {
		return new AtomicIdGen(startingValue, 1);
	}

	public static AtomicIdGen createIdGen(int startingValue, int incrementor) {
		return new AtomicIdGen(startingValue, incrementor);
	}
	@Override
	public int getNextVal() {
		return currentValue.getAndAdd(incrementor);
	}

}
