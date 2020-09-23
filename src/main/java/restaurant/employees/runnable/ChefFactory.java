package restaurant.employees.runnable;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import restaurant.factories.types.TeamFactory;
import restaurant.nonrunnable.Food;
import restaurant.runnable.Restaurant;
import restaurant.utilities.AtomicIdGen;
import restaurant.utilities.CookingDevice;
import restaurant.utilities.CookingStyle;
import restaurant.utilities.IdGen;

public class ChefFactory implements TeamFactory<Chef> {

	private final IdGen idgen;

	public ChefFactory(AtomicIdGen idgen) {
		this.idgen = Objects.requireNonNull(idgen);
	}

	@Override
	public Chef create(Restaurant r) {
		CookingStyle cs = randomStyle();
		CookingDevice cd = createCookingDevice(cs);
		return new Chef(idgen.getNextVal(), r, cd);
	}

	private CookingDevice createCookingDevice(CookingStyle cs) {
		return food -> new Food(food.getName(), cs, food.getPrice());
	}

	@Override
	public Chef[] createTeam(int size, Restaurant r) {
		Chef[] newChefTeam = new Chef[size];
		Arrays.setAll(newChefTeam, $ -> create(r));
		return newChefTeam;
	}

	private CookingStyle randomStyle() {
		CookingStyle[] styles = CookingStyle.values();
		return styles[ThreadLocalRandom.current().nextInt(styles.length)];
	}

}
