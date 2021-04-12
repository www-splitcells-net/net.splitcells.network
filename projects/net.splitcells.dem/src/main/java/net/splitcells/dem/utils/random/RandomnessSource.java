package net.splitcells.dem.utils.random;

import net.splitcells.dem.utils.ConstructorIllegal;

public class RandomnessSource {

	private static final BuilderRandomConfigurable factory = new BuilderRandomConfigurable();

	private RandomnessSource() {
		throw new ConstructorIllegal();
	}

	public static Randomness randomness() {
		return factory.rnd();
	}

	public static Randomness randomness(Long seed) {
		return factory.rnd(seed);
	}

}
