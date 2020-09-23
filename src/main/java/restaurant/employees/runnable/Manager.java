package restaurant.employees.runnable;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.employees.nonrunnable.Accountant;
import restaurant.nonrunnable.Food;
import restaurant.utilities.management.HumanResources;

public class Manager extends Chef {

	private static Logger LOGGER = LogManager.getLogger("manager");
	private static Logger ERR_LOGGER = LogManager.getLogger("error");

	private static long WORK_DAY_TIME = 2500;

	private static Food[] MENU = { new Food("Lobster", 9), new Food("Steak", 8), new Food("Fish", 7),
			new Food("Chicken", 6), new Food("Dessert", 5), new Food("Vegetables", 4) };

	private volatile double dailyRating = 0; // Out of 5
	private volatile int dayCounter = 0;

	private final HumanResources hr;
	private final Accountant a;

	public Manager(Chef chef, HumanResources hr) {
		super(chef);
		this.hr = Objects.requireNonNull(hr);
		a = hr.hireAccountant();
		LOGGER.info("A new Manager was created: " + this);
	}

	private void afterHours() {
		LOGGER.info(this + " gave records to the " + a);
		a.bookkeeping(dayCounter);
		if (a.canAffordToHire()) {
			hireNewEmployee();
			LOGGER.info(this + " hired a new employee");
		} else {
			LOGGER.warn(this + " could not afford to hire a new employee");
		}
		calculateNewRating();
	}

	private void calculateNewRating() {
		double prevRating = (r.getRating() / 2);
		r.setDailyRating();
		dailyRating = (r.getRating() / 2);
		if (prevRating < dailyRating) {
			LOGGER.info(r + "'s rating improved! Now a " + dailyRating + " out of 5");
		} else if (prevRating > dailyRating) {
			LOGGER.warn(r + "'s rating got worse. Now a " + dailyRating + " out of 5");
		} else {
			LOGGER.info(r + "'s rating is still a " + dailyRating + " out of 5");
		}
	}

	private void hireNewEmployee() {
		Runnable newEmployee = hr.hireNewEmployee();
		if (newEmployee instanceof Chef) {
			r.addChef((Chef) newEmployee);
		} else if (newEmployee instanceof Waiter) {
			r.addWaiter((Waiter) newEmployee);
		} else {
			throw new IllegalArgumentException("Something went wrong with the HumanResourcesFactory");
		}
	}

	@Override
	public void run() {
		try {
			runRestaurant();
		} catch (InterruptedException e) {
			ERR_LOGGER.error("A horrible accident happened to " + this + ".");
			Thread.currentThread().interrupt();
		}
	}

	private void runRestaurant() throws InterruptedException {
		setupRestaurant();
		do {
			workDay();
		} while (a.approvesAnotherDay());
		LOGGER.info(this + " permanently shut down " + r);
		r.setShutDown(true);
	}

	private void setupRestaurant() {
		r.addFoodToMenu(MENU);
		LOGGER.info(this + " created the initial menu for" + r);
		r.addHost(hr.hireHost());
		LOGGER.info(this + " hired a host for " + r);
		r.addChef(hr.hireChef());
		LOGGER.info(this + " hired the first chef for " + r);
		r.addWaiter(hr.hireWaiter());
		LOGGER.info(this + " hired the first waiter for " + r);
	}

	private Thread startRestaurantDay() {
		Thread rThread = new Thread(r);
		rThread.start();
		LOGGER.info(this + " opened " + r);
		return rThread;
	}

	private void stopRestaurantDay(Thread rThread) throws InterruptedException {
		r.setOpen(false);
		rThread.join();
		LOGGER.info(this + " closed " + r);
	}

	@Override
	public String toString() {
		return "Manager " + id;
	}

	@Override
	protected void workDay() throws InterruptedException {
		LOGGER.info("###Day " + (++dayCounter) + "###");
		a.resetDailyTotals();
		Thread rThread = startRestaurantDay();
		Thread.sleep(WORK_DAY_TIME);
		stopRestaurantDay(rThread);
		afterHours();
		System.out.println("Day: " + dayCounter + " has completed");
	}

}
