package restaurant.nonrunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.employees.runnable.Chef;
import restaurant.employees.types.RunnableEmployee;
import restaurant.utilities.Order;

public class Kitchen {

	private final static Logger LOGGER = LogManager.getLogger("kitchen");

	private final List<Chef> chefs = new ArrayList<>();
	private final List<Food> menu = new ArrayList<>();

	protected volatile int dailyFoodWaste = 0;

	protected final BlockingDeque<Order> currentOrders = new LinkedBlockingDeque<>();
	protected final ConcurrentMap<String, Queue<Food>> preparedFood = new ConcurrentHashMap<>();

	protected Kitchen() {
	}

	public void addChef(Chef... newChefs) {
		for (Chef c : newChefs) {
			chefs.add(c);
			LOGGER.info(c + " joined " + this);
		}
		LOGGER.info(this + " current has " + chefs.size() + " chefs");
	}

	public void addCompletedOrder(Food completedOrder) {
		preparedFood.get(completedOrder.getName()).add(completedOrder);
		LOGGER.info("A completed order of " + completedOrder + " has been added to " + this);
	}

	public void addFoodToMenu(Food... newFoods) {
		for (Food newFood : newFoods) {
			preparedFood.putIfAbsent(newFood.getName(), new ConcurrentLinkedQueue<>());
			menu.add(newFood);
			LOGGER.info(newFood + " has been added to the menu at " + this);
		}
	}

	public void addOrder(Order order) {
		if (order.isImportant()) {
			currentOrders.addFirst(order);
		} else {
			currentOrders.addLast(order);
		}
		LOGGER.info(order + " has been added to " + this);
	}

	public void cancelOrder(Order order) {
		boolean result = currentOrders.remove(order);
		if (result) {
			LOGGER.warn(order + " has been cancelled at " + this);
		} else {
			LOGGER.warn("Attempted to cancel: " + order + ", but the order was not found at " + this);
		}
	}

	protected void clearKitchen() {
		currentOrders.clear();
		dailyFoodWaste = 0;
		for (Queue<Food> foodQueue : preparedFood.values()) {
			foodQueue.clear();
		}
		LOGGER.info(this + " has been cleared for the day");
	}

	public Optional<Food> findFood(Order o) {
		Objects.requireNonNull(o);
		Optional<Food> result = menu.stream().filter(f -> f.getName().equals(o.getNameOfFood())).findFirst();
		if (!result.isPresent()) {
			LOGGER.warn(o + " was not on the menu at " + this);
		}
		return result;
	}

	public Optional<Food> findOrder(Order o) {
		Queue<Food> completedOrders = preparedFood.get(o.getNameOfFood());
		return Optional.ofNullable(completedOrders.poll());
	}

	public int getFoodWaste() {
		return dailyFoodWaste;
	}

	public List<Food> getMenu() {
		return menu;
	}

	public Optional<Order> getNextOrder() {
		Order nextOrder = currentOrders.pollFirst();
		return Optional.ofNullable(nextOrder);
	}

	public int getNumChefs() {
		return chefs.size();
	}

	public boolean hasOrders() {
		return currentOrders.size() > 0;
	}

	protected void setFoodWaste() {
		Collection<Queue<Food>> queuesOfUnusedFood = preparedFood.values();
		int totalWaste = 0;
		for (Queue<Food> unusedFoodQueue : queuesOfUnusedFood) {
			while (!unusedFoodQueue.isEmpty()) {
				totalWaste += unusedFoodQueue.remove().getPrice();
			}
		}
		dailyFoodWaste = totalWaste;
		LOGGER.warn("Daily food waste at " + this + " was $" + dailyFoodWaste);
	}

	protected Map<Chef, Thread> startChefs() throws InterruptedException {
		Map<Chef, Thread> cThreads = new HashMap<>();
		for (Chef c : chefs) {
			c.setWorking(true);
			Thread cThread = new Thread(c);
			cThreads.put(c, cThread);
			cThread.start();
			LOGGER.info(this + " has started " + c);
			Thread.sleep(10);
		}
		return cThreads;
	}

	protected void stopChefs(Map<Chef, Thread> cThreads) throws InterruptedException {
		for (RunnableEmployee c : chefs) {
			c.setWorking(false);
			LOGGER.warn(this + " has stopped " + c);
		}
		for (Thread cThread : cThreads.values()) {
			cThread.join();
		}
	}

}