package restaurant.utilities;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.nonrunnable.Food;

public class Receipt {

	private final static Logger LOGGER = LogManager.getLogger("receipt");

	private final Food food;

	private Receipt(Food food) {
		this.food = food;
	}

	public static Receipt failed() {
		LOGGER.warn("A failed Receipt was created");
		return new Receipt(null);
	}

	public static Receipt success(Food food) {
		LOGGER.info("A successful Receipt was created for: " + food);
		return new Receipt(Objects.requireNonNull(food));
	}

	public int getPrice() {
		return (food != null) ? food.getPrice() : 0;
	}

	public boolean isSuccess() {
		return food != null;
	}

	@Override
	public String toString() {
		return (food == null) ? "Failed transaction " : "Successful transaction for " + food;
	}

}
