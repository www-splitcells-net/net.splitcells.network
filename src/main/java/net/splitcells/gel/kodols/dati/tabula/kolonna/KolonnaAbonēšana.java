package net.splitcells.gel.kodols.dati.tabula.kolonna;

public interface KolonnaAbonēšana<T> {

	void reģistrē_papildinājums(T papildinājums, int indekss);

	void reģistē_noņemšana(T noņemšana, int indekss);

}
