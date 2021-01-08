package net.splitcells.gel.solution.history.refleksija;

public interface RefleksijaRakstnieks {

	<A> RefleksijaRakstnieks ar(Class<A> type, A value);
}
