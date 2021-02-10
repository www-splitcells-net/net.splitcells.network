package net.splitcells.dem.utils.lambdas;

@FunctionalInterface
public interface TriFunction<A, B, C, X> {
	X apply(A a, B b, C c);
}
