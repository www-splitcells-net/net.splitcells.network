package net.splitcells.gel.dati.uzmeklēšana;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.dati.tabula.Tabula;
import net.splitcells.gel.dati.tabula.atribūts.Atribūts;

public interface UzmeklēšanasVeidotajs extends Closeable, Flushable {
    <R> Uzmeklēšana<R> uzmeklē(Tabula tabula, Atribūts<R> atribūts);
}
