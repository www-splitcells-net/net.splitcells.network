package net.splitcells.gel.solution.history.meta;

public interface MetaDataWriter {

	<A> MetaDataWriter with(Class<A> type, A value);
}
