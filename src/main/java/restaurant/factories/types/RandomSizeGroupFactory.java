package restaurant.factories.types;

public interface RandomSizeGroupFactory<T> extends Factory<T> {

	T[] createTeam();

}
