package restaurant.employees.runnable;

import java.util.Objects;

import restaurant.employees.types.RunnableEmployee;
import restaurant.factories.types.EmployeeFactory;
import restaurant.runnable.Restaurant;
import restaurant.utilities.AtomicIdGen;
import restaurant.utilities.IdGen;

public class HostFactory implements EmployeeFactory<RunnableEmployee> {

	private final IdGen idgen;

	public HostFactory(AtomicIdGen idgen) {
		this.idgen = Objects.requireNonNull(idgen);
	}

	@Override
	public RunnableEmployee create(Restaurant r) {
		return new Host(idgen.getNextVal(), r);
	}

}
