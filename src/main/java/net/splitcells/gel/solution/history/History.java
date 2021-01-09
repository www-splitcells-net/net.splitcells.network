package net.splitcells.gel.solution.history;

import static net.splitcells.gel.data.tabula.atribūts.AtribūtsI.atributs;

import net.splitcells.gel.solution.history.event.Allocation;
import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.data.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.data.datubāze.PirmsNoņemšanasKlausītājs;
import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.solution.history.meta.MetaDataView;

public interface History extends Piešķiršanas, PapildinājumsKlausītājs, PirmsNoņemšanasKlausītājs {
    Atribūts<Integer> PIEŠĶIRŠANA_ID = atributs(Integer.class, "pieškiršana-id");
    Atribūts<Allocation> PIEŠĶIRŠANAS_NOTIKUMS = atributs(Allocation.class, "pieškiršana-notikums");
    Atribūts<MetaDataView> REFLEKSIJAS_DATI = atributs(MetaDataView.class, "refleksijas-dati");

    void atiestatUz(int index);
    int momentansIndekss();
}
