package net.splitcells.gel.dati.uzmeklēšana;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.dati.tabula.Tabula;
import net.splitcells.gel.dati.tabula.atribūts.Atribūts;

public class Uzmeklēšanas extends ResourceI<UzmeklēšanasVeidotajs> {
    public Uzmeklēšanas() {
        super(() -> new UzmeklēšanasIVeidotajs());
    }

    public static <R> Uzmeklēšana<R> uzmeklē(Tabula tabula, Atribūts<R> atribūts) {
        return new UzmeklēšanaI<>(tabula, atribūts);
    }
}
