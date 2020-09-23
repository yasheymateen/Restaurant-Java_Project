package restaurant.nonrunnable;

import java.util.Objects;

import restaurant.utilities.CookingStyle;
import restaurant.utilities.Order;

public class Food {

	private final String name;
	private final int priceInDollars;
	private final CookingStyle style;

	public Food(String name, CookingStyle style, int priceInDollars) {
		this.name = Objects.requireNonNull(name);
		this.style = Objects.requireNonNull(style);
		this.priceInDollars = priceInDollars;
	}

	public Food(String name, int priceInDollars) {
		this(name, CookingStyle.NONE, priceInDollars);
	}

	public boolean fulfillsOrder(Order order) {
		return name.equals(order.getNameOfFood());
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return priceInDollars;
	}

	@Override
	public String toString() {
		return "$" + priceInDollars + " " + style + name;
	}

}
