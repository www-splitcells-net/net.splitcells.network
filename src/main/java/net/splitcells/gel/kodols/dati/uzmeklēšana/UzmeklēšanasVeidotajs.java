package net.splitcells.gel.kodols.dati.uzmeklēšana;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

public interface UzmeklēšanasVeidotajs extends Closeable, Flushable {
    <R> Uzmeklēšana<R> uzmeklē(Tabula tabula, Atribūts<R> atribūts);
}
