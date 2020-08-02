package net.splitcells.gel.kodols.dati.datubāze;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

public class DatuBāzesIVeidotajs implements DatuBāzesVeidotajs {

    @Override
    public DatuBāze datuBāze(String vārds, Atribūts<? extends Object>... atribūti) {
        return new DatuBāzeI(vārds, null, atribūti);
    }

    @Override
    public DatuBāze datuBāze(Atribūts<? extends Object>... atribūti) {
        return new DatuBāzeI(atribūti);
    }

    @Override
    public DatuBāze database(List<Atribūts<?>> atribūti) {
        return new DatuBāzeI(atribūti);
    }

    @Override
    public DatuBāze datuBāze(List<Atribūts<? extends Object>> atribūti, List<List<Object>> rindasVertības) {
        return new DatuBāzeI(atribūti, rindasVertības);
    }

    @Override
    @Deprecated
    public DatuBāze datuBāze(String vārds, Discoverable vecāks, Atribūts<? extends Object>... atribūti) {
        return new DatuBāzeI(vārds, vecāks, listWithValuesOf(atribūti).mapped(a -> (Atribūts<Object>) a));
    }

    @Override
    public DatuBāze datuBāze(String vārds, Discoverable vecāks, List<Atribūts<? extends Object>> atribūti) {
        return new DatuBāzeI(vārds, vecāks, atribūti.mapped(a -> (Atribūts<Object>) a));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
