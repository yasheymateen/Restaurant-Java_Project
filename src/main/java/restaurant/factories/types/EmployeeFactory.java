package restaurant.factories.types;

import restaurant.runnable.Restaurant;

public interface EmployeeFactory<T> {

	T create(Restaurant r);

}
