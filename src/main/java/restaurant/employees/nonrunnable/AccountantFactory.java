package restaurant.employees.nonrunnable;

import java.util.Objects;

import restaurant.factories.types.EmployeeFactory;
import restaurant.runnable.Restaurant;
import restaurant.utilities.AtomicIdGen;
import restaurant.utilities.IdGen;

public class AccountantFactory implements EmployeeFactory<Accountant> {

	private final IdGen idgen;

	public AccountantFactory(AtomicIdGen idgen) {
		this.idgen = Objects.requireNonNull(idgen);
	}

	@Override
	public Accountant create(Restaurant r) {
		return new Accountant(idgen.getNextVal(), r);
	}

}
