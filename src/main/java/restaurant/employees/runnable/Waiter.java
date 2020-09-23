package restaurant.employees.runnable;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.employees.types.RunnableEmployee;
import restaurant.nonrunnable.Food;
import restaurant.nonrunnable.Patron;
import restaurant.runnable.Restaurant;
import restaurant.utilities.Order;
import restaurant.utilities.Receipt;

public class Waiter extends RunnableEmployee {

	private static Logger LOGGER = LogManager.getLogger("waiter");

	private final Deque<Patron> patrons = new ConcurrentLinkedDeque<>();
	private final List<Receipt> receipts = new ArrayList<>();

	private volatile double dailySurveyScore = 0;

	protected Waiter(int id, Restaurant r) {
		super(id, r);
		LOGGER.info("A new Waiter was created: " + this);
	}

	public void addPatron(Patron... newPatrons) {
		for (Patron p : newPatrons) {
			if (p.isVIP()) {
				patrons.addFirst(p);
			} else {
				patrons.addLast(p);
			}
			LOGGER.info(p + " was seated with " + this);
		}
	}

	private void cancelOrder(Patron p) {
		Optional<Order> currentOrder = p.getOrder();
		if (currentOrder.isPresent()) {
			Order order = currentOrder.get();
			r.cancelOrder(order);
			LOGGER.warn(this + " cancelled an " + order);
		}
	}

	private void checkOnOrder(Patron p, Order o) {
		Optional<Food> completedOrder = r.findOrder(o);
		if (completedOrder.isPresent()) {
			Food food = completedOrder.get();
			LOGGER.info(this + " found a completed order for " + p + ", consisting of " + food);
			deliverFood(p, food);
		} else {
			LOGGER.warn(this + " did not find a completed order for " + p);
			p.incrementWait(1);
		}
	}

	private void clearPatron(Patron p) {
		if (p.isHungry()) {
			cancelOrder(p);
			receipts.add(Receipt.failed());
			LOGGER.warn(this + " collected a failed receipt for " + p);
		}
		getSurveyScore(p);
	}

	private void deliverFood(Patron p, Food food) {
		LOGGER.info(this + " delivered an order for " + p);
		p.receiveFood(food);
		receipts.add(Receipt.success(food));
		LOGGER.info(this + " collected a successful receipt for " + p);
	}

	@Override
	protected boolean didWork() throws InterruptedException {
		if (patrons.size() > 0) {
			LOGGER.info(this + " is beginning a loop through their " + patrons.size() + " Patrons");
		} else {
			LOGGER.warn(this + " currently has no patrons");
		}
		Iterator<Patron> patronIterator = patrons.iterator();
		while (patronIterator.hasNext()) {
			Patron p = patronIterator.next();
			if (patronIsFinished(p)) {
				patronIterator.remove();
				LOGGER.info(p + " has finished their transaction with " + this);
			}
		}
		return true;
	}

	public double getDailySurveyScore() {
		return dailySurveyScore;
	}

	public long getNumPatrons() {
		return patrons.size();
	}

	public int getNumServed() {
		return receipts.size();
	}

	public List<Receipt> getReceipts() {
		List<Receipt> dailyReceipts = new ArrayList<>(receipts);
		LOGGER.info(this + " turned in " + dailyReceipts.size() + " receipts");
		return dailyReceipts;
	}

	private void getSurveyScore(Patron p) {
		Optional<Double> surveyScore = p.getSurveyScore();
		if (surveyScore.isPresent()) {
			dailySurveyScore += surveyScore.get();
			LOGGER.info(this + " got a completed survey from " + p);
		} else {
			LOGGER.warn(this + " did not get a survey from " + p);
		}
	}

	private boolean patronIsFinished(Patron p) {
		if (!p.hasLeft()) {
			LOGGER.info(this + " is attending to " + p);
			if (!p.isWaitingForOrder()) {
				takeOrder(p);
			} else {
				Order currentOrder = p.getOrder().get();
				checkOnOrder(p, currentOrder);
			}
			return false;
		}
		clearPatron(p);
		return true;
	}

	public void takeOrder(Patron p) {
		Order newOrder = p.placeOrder(r.getMenu());
		r.addOrder(newOrder);
		LOGGER.info(this + " placed an " + newOrder + ", for " + p);
	}

	@Override
	public String toString() {
		return "Waiter " + id;
	}

	@Override
	protected void workDay() throws InterruptedException {
		receipts.clear();
		patrons.clear();
		dailySurveyScore = 0.0;
		super.workDay();
	}

}
