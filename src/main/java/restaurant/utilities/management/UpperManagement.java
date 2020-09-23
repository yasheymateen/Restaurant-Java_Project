package restaurant.utilities.management;

import java.util.Objects;

import restaurant.employees.runnable.Chef;
import restaurant.employees.runnable.Manager;
import restaurant.employees.types.RunnableEmployee;

public class UpperManagement {

	private final HumanResources hr;

	public UpperManagement(HumanResources hr) {
		this.hr = Objects.requireNonNull(hr);
	}

	public RunnableEmployee promoteChefToManager(Chef chef) {
		return new Manager(chef, hr);
	}

}
