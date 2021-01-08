package net.splitcells.gel.dati.tabula.kolonna;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.dati.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.dati.datubāze.PirmsNoņemšanasKlausītājs;

public interface Kolonna<T> extends List<T>, PapildinājumsKlausītājs, PirmsNoņemšanasKlausītājs, KolonnaSkats<T> {
}
