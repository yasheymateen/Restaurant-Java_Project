package restaurant.employees.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.runnable.Restaurant;

public abstract class RunnableEmployee extends Employee implements Runnable {

	private final static Logger LOGGER = LogManager.getLogger("employee");
	private final static Logger ERR_LOGGER = LogManager.getLogger("error");

	protected volatile boolean working = true;

	protected RunnableEmployee(int id, Restaurant r) {
		super(id, r);
	}

	protected abstract boolean didWork() throws InterruptedException;

	@Override
	public void run() {
		try {
			workDay();
		} catch (InterruptedException e) {
			ERR_LOGGER.error("A horrible accident happened to " + this);
			Thread.currentThread().interrupt();
		}
	}

	public void setWorking(boolean isWorking) {
		working = isWorking;
	}

	protected void workDay() throws InterruptedException {
		LOGGER.info(this + " has arrived at " + r);
		while (working) {
			if (didWork() && working) {
				LOGGER.warn(this + " is on break");
				Thread.sleep(100);
			}
		}
		LOGGER.info(this + " has left " + r);
	}
}
