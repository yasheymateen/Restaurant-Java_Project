package restaurant.runnable;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.employees.runnable.Chef;
import restaurant.employees.runnable.Waiter;
import restaurant.employees.types.RunnableEmployee;
import restaurant.nonrunnable.Kitchen;
import restaurant.nonrunnable.Patron;
import restaurant.utilities.Receipt;

public class Restaurant extends Kitchen implements Runnable {

	private final static Logger LOGGER = LogManager.getLogger("restaurant");
	private final static Logger ERR_LOGGER = LogManager.getLogger("error");

	private final String name;
	private final Queue<Patron> newPatrons = new ConcurrentLinkedQueue<>();
	private final List<Waiter> waitStaff = new ArrayList<>();

	private volatile int rating = 0; // Out of 10
	private volatile boolean open = false;
	private volatile boolean shutDown = false;

	private RunnableEmployee h;

	public Restaurant(String name) {
		this.name = Objects.requireNonNull(name);
		LOGGER.info("A new Restaurant was created: " + this);
	}

	public void addHost(RunnableEmployee h) {
		this.h = h;
		LOGGER.info(h + " joined " + this);
	}

	public void addPatrons(Patron... newPatrons) {
		for (Patron p : newPatrons) {
			this.newPatrons.add(p);
			LOGGER.info(p + " walked into " + this);
		}
	}

	public void addWaiter(Waiter... newWaiters) {
		for (Waiter w : newWaiters) {
			waitStaff.add(w);
			LOGGER.info(w + " joined " + this);
		}
		LOGGER.info(this + " currently has " + waitStaff.size() + " waiters");
	}

	public List<Receipt> getAllReceipts() {
		return waitStaff.stream().map(Waiter::getReceipts).flatMap(List::stream).collect(toList());
	}

	private int getDailyServed() {
		int dailyServed = waitStaff.stream().map(Waiter::getNumServed).mapToInt(Integer::intValue).sum();
		LOGGER.info(this + " served " + dailyServed + " Patrons today");
		return dailyServed;
	}

	public Queue<Patron> getNewPatrons() {
		return newPatrons;
	}

	public int getNumWaitStaff() {
		return waitStaff.size();
	}

	public int getRating() {
		return rating;
	}

	public List<Waiter> getWaitStaff() {
		return waitStaff;
	}

	public boolean isOpen() {
		return open;
	}

	public boolean isShutDown() {
		return shutDown;
	}

	private boolean patronsPresent() {
		for (Waiter w : waitStaff) {
			if (w.getNumPatrons() > 0) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void run() {
		try {
			if (h == null) {
				ERR_LOGGER.fatal("Attempted to start Restaurant without hired a host first");
				throw new IllegalArgumentException("A host must be hired before the restaurant is run");
			}
			workDay();
		} catch (InterruptedException e) {
			ERR_LOGGER.error("There has been an accident and " + this + " has been shut down");
			Thread.currentThread().interrupt();
		}
	}

	private void serveRemainingPatrons() throws InterruptedException {
		LOGGER.warn(this + " is checking for remaining Patrons");
		while (patronsPresent()) {
			Thread.sleep(50);
		}
		LOGGER.info(this + " has no more Patrons to serve");
	}

	public void setDailyRating() {
		if (!open && !shutDown) {
			int dailyServed = getDailyServed();
			double totalDailySurveyScore = waitStaff.stream().map(Waiter::getDailySurveyScore)
					.mapToDouble(Double::doubleValue).sum();
			rating = (int) ((2 * totalDailySurveyScore) / dailyServed);
		}
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}

	private Thread startHost() {
		h.setWorking(true);
		Thread hThread = new Thread(h);
		hThread.start();
		LOGGER.info(this + " has started " + h);
		return hThread;
	}

	private Map<Waiter, Thread> startWaiters() throws InterruptedException {
		Map<Waiter, Thread> wsThreads = new HashMap<>();
		for (Waiter w : waitStaff) {
			w.setWorking(true);
			Thread wThread = new Thread(w);
			wsThreads.put(w, wThread);
			wThread.start();
			LOGGER.info(this + " has started " + w);
			Thread.sleep(10);
		}
		return wsThreads;
	}

	private void stopHost(Thread hThread) throws InterruptedException {
		h.setWorking(false);
		LOGGER.warn(this + " has stopped " + h);
		hThread.join();
	}

	private void stopWaiters(Map<Waiter, Thread> wsThreads) throws InterruptedException {
		for (RunnableEmployee w : waitStaff) {
			LOGGER.warn(this + " has stopped " + w);
			w.setWorking(false);
		}
		for (Thread wThread : wsThreads.values()) {
			wThread.join();
		}
	}

	@Override
	public String toString() {
		return name;
	}

	public void workDay() throws InterruptedException {
		newPatrons.clear();
		clearKitchen();
		Thread hThread = startHost();
		Map<Waiter, Thread> wsThreads = startWaiters();
		Map<Chef, Thread> cThreads = startChefs();
		LOGGER.info(this + " has opened for the day");
		open = true;
		while (open) {
			Thread.sleep(50);
		}
		LOGGER.info(this + " has begun closing for the day");
		stopHost(hThread);
		serveRemainingPatrons();
		LOGGER.info(this + " has told the Kitchen to close for the day");
		stopChefs(cThreads);
		stopWaiters(wsThreads);
		LOGGER.info(this + " has closed for the day");
		setFoodWaste();
	}

}
