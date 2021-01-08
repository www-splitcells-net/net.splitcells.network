package net.splitcells.gel.atrisinājums.vēsture;

import static net.splitcells.gel.dati.tabula.atribūts.AtribūtsI.atributs;

import net.splitcells.gel.atrisinājums.vēsture.notikums.Piešķiršana;
import net.splitcells.gel.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.dati.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.dati.datubāze.PirmsNoņemšanasKlausītājs;
import net.splitcells.gel.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.atrisinājums.vēsture.refleksija.RefleksijaSkats;

public interface Vēsture extends Piešķiršanas, PapildinājumsKlausītājs, PirmsNoņemšanasKlausītājs {
    Atribūts<Integer> PIEŠĶIRŠANA_ID = atributs(Integer.class, "pieškiršana-id");
    Atribūts<Piešķiršana> PIEŠĶIRŠANAS_NOTIKUMS = atributs(Piešķiršana.class, "pieškiršana-notikums");
    Atribūts<RefleksijaSkats> REFLEKSIJAS_DATI = atributs(RefleksijaSkats.class, "refleksijas-dati");

    void atiestatUz(int index);
    int momentansIndekss();
}
