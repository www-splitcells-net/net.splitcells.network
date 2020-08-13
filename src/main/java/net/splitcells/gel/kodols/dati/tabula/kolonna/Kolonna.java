package net.splitcells.gel.kodols.dati.tabula.kolonna;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.dati.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.kodols.dati.datubāze.PirmsNoņemšanasKlausītājs;

public interface Kolonna<T> extends List<T>, PapildinājumsKlausītājs, PirmsNoņemšanasKlausītājs, KolonnaSkats<T> {
}
