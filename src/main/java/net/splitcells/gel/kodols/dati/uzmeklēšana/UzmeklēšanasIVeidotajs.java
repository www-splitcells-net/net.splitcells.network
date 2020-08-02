package net.splitcells.gel.kodols.dati.uzmeklēšana;

import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

public class UzmeklēšanasIVeidotajs implements UzmeklēšanasVeidotajs {
    @Override
    public <R> Uzmeklēšana<R> uzmeklē(Tabula tabula, Atribūts<R> atribūts) {
        return new UzmeklēšanaI<>(tabula, atribūts);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
