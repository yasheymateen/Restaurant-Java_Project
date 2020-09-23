package restaurant.nonrunnable;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import restaurant.factories.types.RandomSizeGroupFactory;
import restaurant.utilities.AtomicIdGen;
import restaurant.utilities.IdGen;

public class PatronFactory implements RandomSizeGroupFactory<Patron> {

	private final static int CHANCE_OF_VIP = 3;
	private final static int MAX_PARTY_SIZE = 4;

	private final IdGen idgen;

	public PatronFactory(AtomicIdGen idgen) {
		this.idgen = Objects.requireNonNull(idgen);
	}

	@Override
	public Patron create() {
		boolean vip = ThreadLocalRandom.current().nextInt(CHANCE_OF_VIP) == 0;
		return new Patron(vip, idgen.getNextVal());
	}

	@Override
	public Patron[] createTeam() {
		int sizeOfParty = (ThreadLocalRandom.current().nextInt(MAX_PARTY_SIZE) + 1);
		Patron[] newParty = new Patron[sizeOfParty];
		Arrays.setAll(newParty, $ -> create());
		return newParty;
	}

}
