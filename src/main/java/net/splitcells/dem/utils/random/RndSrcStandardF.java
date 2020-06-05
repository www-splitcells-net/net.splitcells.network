package net.splitcells.dem.utils.random;

import java.security.SecureRandom;
import java.util.Random;

public class RndSrcStandardF implements RndSrcF {

	public final Random seedSrc = new Random();

	public RndSrcStandardF() {
	}

	@Override
	public Randomness rnd() {
		return new JavaRandomWrapper(new Random());
	}

	@Override
	public RndSrcCrypt rndCrypt() {
		return new JavaRandomWrapper(new SecureRandom());
	}
}