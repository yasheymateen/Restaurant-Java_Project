package restaurant.factories.types;

import restaurant.runnable.Restaurant;

public interface TeamFactory<T> extends EmployeeFactory<T> {

	T[] createTeam(int size, Restaurant r);

}
