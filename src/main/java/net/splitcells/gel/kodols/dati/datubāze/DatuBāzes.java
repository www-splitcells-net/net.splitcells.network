package net.splitcells.gel.kodols.dati.datubāze;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class DatuBāzes {
    private DatuBāzes() {
        throw constructorIllegal();
    }

    public static DatuBāze datuBāze(String vārds, Atribūts<? extends Object>... atribūti) {
        return new DatuBāzeI(vārds, null, atribūti);
    }

    public static DatuBāze datuBāze(Atribūts<? extends Object>... atribūti) {
        return new DatuBāzeI(atribūti);
    }

    public static DatuBāze datuBāze(List<Atribūts<? extends Object>> atribūti, List<List<Object>> rindasVertības) {
        return new DatuBāzeI(atribūti, rindasVertības);
    }

    @Deprecated
    public static DatuBāze datuBāze(String vārds, Discoverable vecāks, Atribūts<? extends Object>... atribūti) {
        return new DatuBāzeI(vārds, vecāks, listWithValuesOf(atribūti).mapped(a -> (Atribūts<Object>) a));
    }

    public static DatuBāze datuBāze(String vārds, Discoverable vecāks, List<Atribūts<? extends Object>> atribūti) {
        return new DatuBāzeI(vārds, vecāks, atribūti.mapped(a -> (Atribūts<Object>) a));
    }
}
