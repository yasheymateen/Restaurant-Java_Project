package restaurant.utilities.management;

import java.util.Objects;

import restaurant.employees.nonrunnable.Accountant;
import restaurant.employees.runnable.Chef;
import restaurant.employees.runnable.ChefFactory;
import restaurant.employees.runnable.Waiter;
import restaurant.employees.runnable.WaiterFactory;
import restaurant.employees.types.RunnableEmployee;
import restaurant.factories.types.EmployeeFactory;
import restaurant.runnable.Restaurant;

public class HumanResources {

	private final EmployeeFactory<Accountant> af;
	private final EmployeeFactory<RunnableEmployee> hf;
	private final ChefFactory cf;
	private final WaiterFactory wf;
	private final Restaurant r;
	
	public HumanResources(ChefFactory cf, WaiterFactory wf, EmployeeFactory<Accountant> af,
			EmployeeFactory<RunnableEmployee> hf, Restaurant r) {
		this.cf = Objects.requireNonNull(cf);
		this.wf = Objects.requireNonNull(wf);
		this.af = Objects.requireNonNull(af);
		this.hf = Objects.requireNonNull(hf);
		this.r = Objects.requireNonNull(r);
	}

	public Accountant hireAccountant() {
		return af.create(r);
	}

	public Chef hireChef() {
		return cf.create(r);
	}

	public RunnableEmployee hireHost() {
		return hf.create(r);
	}

	public Runnable hireNewEmployee() {
		double numChefs = r.getNumChefs();
		double numWaitStaff = r.getNumWaitStaff();
		if ((numWaitStaff / numChefs) < 2) {
			return hireWaiter();
		} else {
			return hireChef();
		}
	}

	public Waiter hireWaiter() {
		return wf.create(r);
	}

}
