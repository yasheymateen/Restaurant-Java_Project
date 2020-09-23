package restaurant.utilities;

import restaurant.nonrunnable.Food;

@FunctionalInterface
public interface CookingDevice {

	Food cook(Food food);

}
