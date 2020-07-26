package net.splitcells.gel.kodols.atrisinājums.vēsture.refleksija;

public interface RefleksijaRakstnieks {

	<A> RefleksijaRakstnieks ar(Class<A> type, A value);
}
