package net.splitcells.gel.solution.history.meta;

public interface MetaDataWriter {

	<A> MetaDataWriter ar(Class<A> type, A value);
}
