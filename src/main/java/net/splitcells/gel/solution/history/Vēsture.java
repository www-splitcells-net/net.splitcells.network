package net.splitcells.gel.solution.history;

import static net.splitcells.gel.data.tabula.atribūts.AtribūtsI.atributs;

import net.splitcells.gel.solution.history.notikums.Piešķiršana;
import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.data.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.data.datubāze.PirmsNoņemšanasKlausītājs;
import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.solution.history.refleksija.RefleksijaSkats;

public interface Vēsture extends Piešķiršanas, PapildinājumsKlausītājs, PirmsNoņemšanasKlausītājs {
    Atribūts<Integer> PIEŠĶIRŠANA_ID = atributs(Integer.class, "pieškiršana-id");
    Atribūts<Piešķiršana> PIEŠĶIRŠANAS_NOTIKUMS = atributs(Piešķiršana.class, "pieškiršana-notikums");
    Atribūts<RefleksijaSkats> REFLEKSIJAS_DATI = atributs(RefleksijaSkats.class, "refleksijas-dati");

    void atiestatUz(int index);
    int momentansIndekss();
}
