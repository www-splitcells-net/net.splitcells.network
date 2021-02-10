package net.splitcells.dem.utils.lambdas;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
	void apply(A a, B b, C c);
}
