package net.splitcells.gel.data.tabula.kolonna;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.data.datubāze.PirmsNoņemšanasKlausītājs;

public interface Kolonna<T> extends List<T>, PapildinājumsKlausītājs, PirmsNoņemšanasKlausītājs, KolonnaSkats<T> {
}
