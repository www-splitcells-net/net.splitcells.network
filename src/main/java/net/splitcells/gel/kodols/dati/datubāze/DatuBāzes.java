package net.splitcells.gel.kodols.dati.datubāze;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.kodols.atrisinājums.vēsture.Vēstures;
import net.splitcells.gel.kodols.atrisinājums.vēsture.VēsturesVeidotajs;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import org.w3c.dom.Element;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class DatuBāzes extends ResourceI<DatuBāzesVeidotajs> {
    public DatuBāzes() {
        super(() -> new DatuBāzesIVeidotajs());
    }

    public static DatuBāze datuBāze(String vārds, Atribūts<? extends Object>... atribūti) {
        return environment().config().configValue(DatuBāzes.class).datuBāze(vārds, atribūti);
    }

    public static DatuBāze datuBāze(Atribūts<? extends Object>... atribūti) {
        return environment().config().configValue(DatuBāzes.class).datuBāze(atribūti);
    }

    public static DatuBāze datuBāze(List<Atribūts<? extends Object>> atribūti, List<List<Object>> rindasVertības) {
        return environment().config().configValue(DatuBāzes.class).datuBāze(atribūti, rindasVertības);
    }

    @Deprecated
    public static DatuBāze datuBāze(String vārds, Discoverable vecāks, Atribūts<? extends Object>... atribūti) {
        return environment().config().configValue(DatuBāzes.class).datuBāze(vārds, vecāks, atribūti);
    }

    public static DatuBāze datuBāze(String vārds, Discoverable vecāks, List<Atribūts<? extends Object>> atribūti) {
        return environment().config().configValue(DatuBāzes.class).datuBāze(vārds, vecāks, atribūti);
    }

    public static DatuBāze datuBāze(List<Atribūts<?>> atribūti) {
        return environment().config().configValue(DatuBāzes.class).datuBāze(atribūti);
    }

    public static DatuBāze datuBāzeNoObjektiem(List<Atribūts<Object>> atribūti) {
        return environment().config().configValue(DatuBāzes.class)
                .datuBāze
                        (atribūti.stream()
                                .map(a -> (Atribūts<Object>) a)
                                .collect(toList()));
    }

    public static DatuBāze datuBāzeNoFods(List<Atribūts<?>> atribūti, Element fods) {
        return environment().config().configValue(DatuBāzes.class).datuBāzeNoFods(atribūti, fods);
    }
}
