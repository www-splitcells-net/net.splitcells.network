package net.splitcells.gel.data.table.column;

public interface ColumnSubscriber<T> {

	void register_addition(T addition, int index);

	void register_removal(T removal, int index);

}
