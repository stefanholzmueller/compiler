package stefanholzmueller.compiler;

public enum Type {

	BOOL("Bool", "java/lang/Boolean"), INT("Int", "java/math/BigDecimal"), STR("Str", "java/lang/String");

	private final String name;
	private final String javaName;

	private Type(String name, String javaName) {
		this.name = name;
		this.javaName = javaName;
	}

	public String getName() {
		return name;
	}

	public String getJavaName() {
		return javaName;
	}

	public static Type fromString(String string) {
		for (Type type : values()) {
			if (type.getName().equals(string))
				return type;
		}
		throw new IllegalArgumentException(string + " is no known type");
	}

}
