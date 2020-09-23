package restaurant.employees.types;

import java.util.Objects;

import restaurant.runnable.Restaurant;

public class Employee {

	protected final int id;
	protected final Restaurant r;

	protected Employee(int id, Restaurant r) {
		this.id = id;
		this.r = Objects.requireNonNull(r);
	}

}