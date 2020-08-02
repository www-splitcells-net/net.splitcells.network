package net.splitcells.gel.kodols.dati.datubāze;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

public interface DatuBāzesVeidotajs  extends Closeable, Flushable {
    DatuBāze datuBāze(String vārds, Atribūts<? extends Object>... atribūti);

    DatuBāze datuBāze(Atribūts<? extends Object>... atribūti);

    DatuBāze database(List<Atribūts<?>> atribūti);

    DatuBāze datuBāze(List<Atribūts<? extends Object>> atribūti, List<List<Object>> rindasVertības);

    @Deprecated
    DatuBāze datuBāze(String vārds, Discoverable vecāks, Atribūts<? extends Object>... atribūti);

    DatuBāze datuBāze(String vārds, Discoverable vecāks, List<Atribūts<? extends Object>> atribūti);
}
