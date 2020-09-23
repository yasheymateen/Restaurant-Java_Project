package restaurant.utilities;

public enum CookingStyle {

	BAKED("Baked "), BBQ("Barbequed "), DEEP_FRIED("Deep-Fried "), NONE(""), RAW("Raw "), ROASTED("Roasted ");

	private String name;

	private CookingStyle(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
