package restaurant.employees.runnable;

import java.util.List;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.employees.types.RunnableEmployee;
import restaurant.nonrunnable.Patron;
import restaurant.runnable.Restaurant;

public class Host extends RunnableEmployee {

	private static Logger LOGGER = LogManager.getLogger("host");

	private final Queue<Patron> newPatrons;
	private final List<Waiter> waitStaff;

	protected Host(int id, Restaurant r) {
		super(id, r);
		waitStaff = r.getWaitStaff();
		newPatrons = r.getNewPatrons();
		LOGGER.info("A new Host was created: " + this);
	}

	@Override
	protected boolean didWork() throws InterruptedException {
		boolean didWork = false;
		while (!newPatrons.isEmpty() && working) {
			LOGGER.info(newPatrons.size() + " Patrons remaining to be seated");
			Patron nextPatron = newPatrons.remove();
			seatPatron(nextPatron);
			didWork = true;
		}
		return didWork;
	}

	private Waiter findAvailableWaiter() {
		Waiter next = null;
		for (Waiter w : waitStaff) {
			if ((next == null) || (next.getNumPatrons() > w.getNumPatrons())) {
				next = w;
			}
		}
		LOGGER.info(next + " had the least number of current Patrons: " + next.getNumPatrons());
		return next;
	}

	private void seatPatron(Patron p) {
		Waiter w = findAvailableWaiter();
		LOGGER.info(this + " seated " + p + " with " + w);
		w.addPatron(p);
	}

	@Override
	public String toString() {
		return "Host " + id;
	}

}
