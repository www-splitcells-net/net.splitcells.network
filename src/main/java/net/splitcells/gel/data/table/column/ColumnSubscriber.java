package net.splitcells.gel.data.table.column;

public interface ColumnSubscriber<T> {

	void reģistrē_papildinājums(T papildinājums, int indekss);

	void reģistē_noņemšana(T noņemšana, int indekss);

}
