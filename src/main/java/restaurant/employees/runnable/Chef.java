package restaurant.employees.runnable;

import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.employees.types.RunnableEmployee;
import restaurant.nonrunnable.Food;
import restaurant.runnable.Restaurant;
import restaurant.utilities.CookingDevice;
import restaurant.utilities.Order;

public class Chef extends RunnableEmployee {

	private static final Logger LOGGER = LogManager.getLogger("chef");

	private static final int COOKING_TIME = 20;

	protected final CookingDevice cd;

	protected Chef(Chef copy) {
		super(copy.id, copy.r);
		cd = copy.cd;
		LOGGER.warn(copy + " was copied into: " + this);
	}

	protected Chef(int id, Restaurant r, CookingDevice cd) {
		super(id, r);
		this.cd = Objects.requireNonNull(cd);
		LOGGER.info("A new Chef was made: " + this);
	}

	private void cook(Optional<Food> menuItem) throws InterruptedException {
		Food completedOrder = cd.cook(menuItem.get());
		Thread.sleep(completedOrder.getPrice() * COOKING_TIME);
		if (working) {
			r.addCompletedOrder(completedOrder);
			LOGGER.info(this + " completed an order for " + completedOrder);
		}
	}

	@Override
	protected boolean didWork() throws InterruptedException {
		boolean hasWorked = false;
		while (r.hasOrders() && working) {
			hasWorked = prepareOrder();
		}
		return hasWorked;
	}

	private boolean prepareOrder() throws InterruptedException {
		Optional<Order> currentOrder = r.getNextOrder();
		if (currentOrder.isPresent()) {
			LOGGER.info(this + " found a current order");
			Optional<Food> menuItem = r.findFood(currentOrder.get());
			if (menuItem.isPresent()) {
				cook(menuItem);
				return true;
			} else {
				LOGGER.warn(currentOrder.get() + " was not found on the menu");
			}
		} else {
			LOGGER.warn(this + " did not find an order to make");
		}
		return false;
	}

	@Override
	public String toString() {
		return "Chef " + id;
	}

}
