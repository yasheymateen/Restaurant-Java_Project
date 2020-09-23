package restaurant.factories.types;

@FunctionalInterface
public interface Factory<T> {

	T create();
}
