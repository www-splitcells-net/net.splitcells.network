package net.splitcells.dem.utils;

public final class MathUtils {

	private MathUtils() {
		throw new ConstructorIllegal();
	}

	public static double distance(double a, double b) {
		if (a < b) {
			return Math.abs(b - a);
		} else {
			return Math.abs(a - b);
		}
	}

	public static double distance(int a, int b) {
		if (a < b) {
			return Math.abs(b - a);
		} else {
			return Math.abs(a - b);
		}
	}

}
