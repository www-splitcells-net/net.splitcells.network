package net.splitcells.dem.utils.random;

import net.splitcells.dem.environment.config.framework.OptionI;

public class DeterministicRootSourceSeed extends OptionI<Long> {

	public DeterministicRootSourceSeed() {
		super(() -> 3L);
	}

}
