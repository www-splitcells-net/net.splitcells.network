package net.splitcells.gel.atrisinājums.vēsture.refleksija;

public interface RefleksijaRakstnieks {

	<A> RefleksijaRakstnieks ar(Class<A> type, A value);
}
