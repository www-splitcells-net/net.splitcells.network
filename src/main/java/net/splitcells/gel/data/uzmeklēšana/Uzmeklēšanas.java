package net.splitcells.gel.data.uzmeklēšana;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.data.table.Tabula;
import net.splitcells.gel.data.table.atribūts.Atribūts;

public class Uzmeklēšanas extends ResourceI<UzmeklēšanasVeidotajs> {
    public Uzmeklēšanas() {
        super(() -> new UzmeklēšanasIVeidotajs());
    }

    public static <R> Uzmeklēšana<R> uzmeklē(Tabula tabula, Atribūts<R> atribūts) {
        return new UzmeklēšanaI<>(tabula, atribūts);
    }
}
