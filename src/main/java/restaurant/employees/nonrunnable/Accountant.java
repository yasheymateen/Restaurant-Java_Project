package restaurant.employees.nonrunnable;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.employees.types.Employee;
import restaurant.runnable.Restaurant;
import restaurant.utilities.Receipt;

public class Accountant extends Employee {

	private static final Logger LOGGER = LogManager.getLogger("accountant");

	private static final int FOOD_COST = 3;
	
	private static final int CHEF_SALARY = 20;
	private static final int HOST_SALARY = 15;
	private static final int WAIT_STAFF_SALARY = 10;

	private volatile int dailyCost;
	private volatile int dailyFails;
	private volatile int dailyProfits;
	private volatile int dailySuccesses;
	private volatile int runningProfits;

	protected Accountant(int id, Restaurant r) {
		super(id, r);
		LOGGER.info("A new Accountant was created: " + this);
	}

	public boolean approvesAnotherDay() {
		if (!r.isOpen()) {
			boolean approval = ((dailyFails > 0) && (runningProfits > 0));
			if (!approval) {
				if (runningProfits <= 0) {
					LOGGER.fatal(r + " ran out of money and closed forever");
				} else {
					LOGGER.info(r + " successfully served every customer");
				}
			}
			return approval;
		}
		return false;
	}

	public boolean bookkeeping(int dayCounter) {
		if (!r.isOpen()) {
			LOGGER.info("###DAY " + dayCounter + " SUMMARY###");
			handleReceipts(r.getAllReceipts());
			calculateWaste();
			payCurrentEmployees();
			return true;
		}
		return false;
	}

	private void calculateWaste() {
		int waste = r.getFoodWaste();
		runningProfits -= waste;
		LOGGER.warn("Daily waste: $" + waste);
	}

	public boolean canAffordToHire() {
		boolean approval = (!r.isOpen() && (dailyFails > 0) && (runningProfits >= dailyCost));
		if (approval) {
			LOGGER.info(r + " could afford to hire a new employee");
		} else {
			LOGGER.warn(r + " could not afford to hire a new employee");
		}
		return approval;
	}

	private void countSuccessesAndFails(Receipt r) {
		if (r.isSuccess()) {
			dailySuccesses++;
		} else {
			dailyFails++;
		}
	}

	private void handleReceipts(List<Receipt> allReceipts) {
		allReceipts.forEach(r -> countSuccessesAndFails(r));
		LOGGER.info("Daily successes: " + dailySuccesses);
		if (dailyFails > 0) {
			LOGGER.warn("Daily fails: " + dailyFails);
		} else {
			LOGGER.info("Daily fails: " + dailyFails);
		}
		allReceipts.stream().filter(Receipt::isSuccess).forEach(r -> dailyProfits += r.getPrice());
		if (dailyProfits == 0) {
			LOGGER.warn("Daily profits: $" + dailyProfits);
		} else {
			LOGGER.info("Daily profits: $" + dailyProfits);
		}
		runningProfits += dailyProfits;
	}

	private void payCurrentEmployees() {
		int numChefs = r.getNumChefs();
		int numWaitStaff = r.getNumWaitStaff();
		LOGGER.info(r + " currently employs " + numChefs + " chefs and " + numWaitStaff + " wait staff");
		int dailySalary = (HOST_SALARY + (numChefs * CHEF_SALARY) + (numWaitStaff * WAIT_STAFF_SALARY));
		int dailyFood = (dailySuccesses * FOOD_COST);
		dailyCost = (dailySalary + dailyFood);
		runningProfits -= dailyCost;
		if (runningProfits < dailyCost) {
			LOGGER.warn("Running profits after paying salaries and buying food: $" + runningProfits);
		} else {
			LOGGER.info("Running profits after paying salaries and buying food: $" + runningProfits);
		}
	}

	public boolean resetDailyTotals() {
		if (!r.isOpen()) {
			dailySuccesses = 0;
			dailyFails = 0;
			dailyProfits = 0;
			dailyCost = 0;
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Accountant " + id;
	}

}