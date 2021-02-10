package net.splitcells.dem.utils;

public final class ConstructorIllegal extends AssertionError {

	/**
	 * Generated via Eclipse.
	 */
	private static final long serialVersionUID = -4295942428118981774L;

	public static ConstructorIllegal constructorIllegal() {
		return new ConstructorIllegal();
	}

	@Deprecated
	public ConstructorIllegal() {
	}
}
