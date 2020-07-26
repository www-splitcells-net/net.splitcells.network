package net.splitcells.gel.kodols.dati.tabula.kolonna;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.uzmeklēšana.Uzmeklēšana;
import net.splitcells.gel.kodols.dati.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.kodols.dati.datubāze.NoņemšanasKlausītājs;

public interface Kolonna<T> extends List<T>, PapildinājumsKlausītājs, NoņemšanasKlausītājs, KolonnaSkats<T> {
}
