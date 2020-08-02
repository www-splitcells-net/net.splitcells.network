package net.splitcells.gel.kodols.dati.uzmeklēšana;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

public class Uzmeklēšanas extends ResourceI<UzmeklēšanasVeidotajs> {
    public Uzmeklēšanas() {
        super(() -> new UzmeklēšanasIVeidotajs());
    }

    public static <R> Uzmeklēšana<R> uzmeklē(Tabula tabula, Atribūts<R> atribūts) {
        return new UzmeklēšanaI<>(tabula, atribūts);
    }
}
