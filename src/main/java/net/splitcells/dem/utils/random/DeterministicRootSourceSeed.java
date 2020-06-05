package net.splitcells.dem.utils.random;

import net.splitcells.dem.environment.config.OptionI;

public class DeterministicRootSourceSeed extends OptionI<Long> {

	public DeterministicRootSourceSeed() {
		super(() -> 3L);
	}

}
