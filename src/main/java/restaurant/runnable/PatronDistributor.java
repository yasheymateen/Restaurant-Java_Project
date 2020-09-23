package restaurant.runnable;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.factories.types.RandomSizeGroupFactory;
import restaurant.nonrunnable.Patron;
import restaurant.nonrunnable.PatronFactory;

public class PatronDistributor implements Runnable {

	private final static Logger LOGGER = LogManager.getLogger("patronDistributor");
	private final static Logger ERR_LOGGER = LogManager.getLogger("error");

	private volatile boolean active = true;
	private volatile int dayCounter = 1;

	private final RandomSizeGroupFactory<Patron> pf;
	private final Restaurant r;

	public PatronDistributor(PatronFactory pf, Restaurant restaurant) {
		this.pf = Objects.requireNonNull(pf);
		r = Objects.requireNonNull(restaurant);
		LOGGER.info("A new PatronDistributor was created");
	}

	private int howBusyIsItToday() {
		int timeBetween = ThreadLocalRandom.current().nextInt(1, 7);
		String busyness = "Day: " + dayCounter + " looks like a ";
		if (timeBetween < 3) {
			busyness += "busy day";
		} else if (timeBetween < 5) {
			busyness += "normal day";
		} else if (timeBetween < 7) {
			busyness += "slow day";
		}
		LOGGER.info(busyness);
		return (timeBetween + 1);
	}

	private void newPartyArrives() {
		Patron[] newParty = pf.createTeam();
		r.addPatrons(newParty);
		LOGGER.info(newParty.length + " Patrons added to " + r);
	}

	private void patronsArrive() throws InterruptedException {
		int timeBetween = howBusyIsItToday();
		while (r.isOpen()) {
			newPartyArrives();
			int timeBetweenParties = (ThreadLocalRandom.current().nextInt(timeBetween) * 50);
			Thread.sleep(timeBetweenParties);
		}
	}

	@Override
	public void run() {
		try {
			LOGGER.info("PatronDistributor has started");
			sendPatrons();
			LOGGER.warn("PatronDistributor has stopped");
		} catch (InterruptedException e) {
			ERR_LOGGER.error("There has been an accident and no more patrons will arrive.");
			Thread.currentThread().interrupt();
		}
	}

	private void sendPatrons() throws InterruptedException {
		boolean newDay = false;
		while (active) {
			if (r.isOpen()) {
				newDay = true;
				patronsArrive();
			} else {
				if (newDay) {
					dayCounter++;
					newDay = false;
				}
			}
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
