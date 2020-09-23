package restaurant.utilities;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Order {

	private final static Logger LOGGER = LogManager.getLogger("order");

	private final boolean important;
	private final String nameOfFood;

	public Order(boolean important, String nameOfFood) {
		this.important = important;
		this.nameOfFood = Objects.requireNonNull(nameOfFood);
		LOGGER.info("A new Order was created: " + this);
	}

	public String getNameOfFood() {
		return nameOfFood;
	}

	public boolean isImportant() {
		return important;
	}

	@Override // Overly elaborate toString()
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (important) {
			builder.append("Important o");
		} else {
			builder.append("O");
		}
		builder.append("rder for ").append(nameOfFood);
		return builder.toString();
	}

}
