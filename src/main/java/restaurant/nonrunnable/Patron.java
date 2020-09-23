package restaurant.nonrunnable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.utilities.Order;

public class Patron {

	private final static Logger LOGGER = LogManager.getLogger("patron");

	private final static double STANDARD_MAX_WAIT = 10;

	private volatile Order order;
	private volatile double wait = 0;
	private volatile boolean correctOrder = false;
	private volatile boolean hungry = true;
	private volatile boolean left = false;

	private final int id;
	private final double maxWait;
	private final boolean vip;

	protected Patron(boolean vip, int id) {
		this.vip = vip;
		this.id = id;
		maxWait = vip ? STANDARD_MAX_WAIT / 2 : STANDARD_MAX_WAIT;
		LOGGER.info("A new patron was created: " + this);
	}

	public boolean fillOrder(Food food) {
		if (food.fulfillsOrder(order)) {
			LOGGER.info(this + " ate " + food + " after waiting for " + wait + " units of time");
			hungry = false;
			left = true;
			LOGGER.info(this + " left happy");
			return true;
		}
		LOGGER.error(this + " received an incorrect order and left unhappy");
		return false;
	}

	private double generateSurveyScore() {
		double score = (((maxWait - wait) * 5) / maxWait);
		if ((score < 1) && !hungry) {
			score = 1;
		} else if ((score >= 1) && !correctOrder) {
			score--;
		}
		return score;
	}

	public Optional<Order> getOrder() {
		return Optional.ofNullable(order);
	}

	public Optional<Double> getSurveyScore() {
		if (left) {
			double score = hungry ? 0.0 : generateSurveyScore();
			LOGGER.info(this + " gave a survey score of " + score + " out of 5");
			return Optional.of(score);
		}
		return Optional.empty();
	}

	public boolean hasLeft() {
		return left;
	}

	public void incrementWait(int i) {
		wait += i;
		LOGGER.warn(this + " has been waiting for " + wait + " units of time");
		left = (wait > maxWait);
		if (left) {
			LOGGER.error(this + " waited for too long and left");
		}
	}

	public boolean isHungry() {
		return hungry;
	}

	public boolean isVIP() {
		return vip;
	}

	public boolean isWaitingForOrder() {
		return order != null;
	}

	public Order placeOrder(List<Food> menu) {
		String foodName = menu.get(ThreadLocalRandom.current().nextInt(menu.size())).getName();
		order = new Order(vip, foodName);
		LOGGER.info(this + " ordered " + foodName);
		return order;
	}

	public void receiveFood(Food food) {
		if (order != null) {
			correctOrder = fillOrder(food);
			return;
		} else {
			LOGGER.error(this + " received food but has not placed an order yet");
		}
		correctOrder = false;
	}

	@Override // Overly elaborate toString()
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (vip) {
			builder.append("Very Important ");
		}
		if (hungry) {
			builder.append("Hungry ");
		}
		builder.append("Patron: ").append(id);
		return builder.toString();
	}

}
