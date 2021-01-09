package net.splitcells.gel.data.uzmeklēšana;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.data.table.Tabula;
import net.splitcells.gel.data.table.atribūts.Atribūts;

public interface UzmeklēšanasVeidotajs extends Closeable, Flushable {
    <R> Uzmeklēšana<R> uzmeklē(Tabula tabula, Atribūts<R> atribūts);
}
