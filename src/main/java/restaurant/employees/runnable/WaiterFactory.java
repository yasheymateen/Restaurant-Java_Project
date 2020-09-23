package restaurant.employees.runnable;

import java.util.Arrays;
import java.util.Objects;

import restaurant.factories.types.TeamFactory;
import restaurant.runnable.Restaurant;
import restaurant.utilities.AtomicIdGen;
import restaurant.utilities.IdGen;

public class WaiterFactory  implements TeamFactory<Waiter> {

	private final IdGen idgen;

	public WaiterFactory(AtomicIdGen idgen) {
		this.idgen = Objects.requireNonNull(idgen);
	}
	
	@Override
	public Waiter create(Restaurant restaurant) {
		return new Waiter(idgen.getNextVal(), restaurant);
	}

	@Override
	public Waiter[] createTeam(int numStaff, Restaurant restaurant) {
		Waiter[] newWaitStaff = new Waiter[numStaff];
		Arrays.setAll(newWaitStaff, $ -> create(restaurant));
		return newWaitStaff;
	}

}
