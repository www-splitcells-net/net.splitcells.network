package net.splitcells.dem.utils.random;

import java.util.Random;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static org.assertj.core.api.Assertions.assertThat;

public class JavaRandomWrapper implements Randomness, RndSrcCrypt {
	private final Random rnd;

	protected JavaRandomWrapper(Random arg_rnd) {
		rnd = arg_rnd;
	}

	@Override
	public Random asRandom() {
		return rnd;
	}

	@Override
	public int integer() {
		return rnd.nextInt();
	}

	/**
	 * FIXME Allow negative min and max.
	 */
	@Override
	public int integer(final Integer min, final Integer max) {
		if (ENFORCING_UNIT_CONSISTENCY) {
			assertThat(min).isLessThanOrEqualTo(max);
			assertThat(min).isGreaterThanOrEqualTo(0);
			assertThat(max).isGreaterThanOrEqualTo(0);
			assertThat(max).isLessThan(Integer.MAX_VALUE);
		}
		int rVal = asRandom().nextInt(max + 1);
		// PERFORMANCE
		while (rVal < min) {
			rVal = asRandom().nextInt(max + 1);
		}
		return rVal;
	}
}
