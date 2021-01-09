package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.attribute.Attribute;
import org.w3c.dom.Element;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.list.Lists.*;

public class Databases extends ResourceI<DatabaseFactory> {
    public Databases() {
        super(() -> new DatabaseIFactory());
    }

    public static Database datuBāze(String vārds, Attribute<? extends Object>... atribūti) {
        return environment().config().configValue(Databases.class).datuBāze(vārds, atribūti);
    }

    public static Database datuBāze(Attribute<? extends Object>... atribūti) {
        return environment().config().configValue(Databases.class).datuBāze(atribūti);
    }

    public static Database datuBāze(List<Attribute<? extends Object>> atribūti, List<List<Object>> rindasVertības) {
        return environment().config().configValue(Databases.class).datuBāze(atribūti, rindasVertības);
    }

    @Deprecated
    public static Database datuBāze(String vārds, Discoverable vecāks, Attribute<? extends Object>... atribūti) {
        return environment().config().configValue(Databases.class).datuBāze(vārds, vecāks, atribūti);
    }

    public static Database datuBāze(String vārds, Discoverable vecāks, List<Attribute<? extends Object>> atribūti) {
        return environment().config().configValue(Databases.class).datuBāze(vārds, vecāks, atribūti);
    }

    public static Database datuBāze(List<Attribute<?>> atribūti) {
        return environment().config().configValue(Databases.class).datuBāze(atribūti);
    }

    public static Database datuBāzeNoFods(List<Attribute<?>> atribūti, Element fods) {
        return environment().config().configValue(Databases.class).datuBāzeNoFods(atribūti, fods);
    }

    public static List<Attribute<? extends Object>> objektuAtribūti(List<Attribute<Object>> atribūti) {
        return atribūti.stream()
                .map(a -> (Attribute<? extends Object>) a)
                .collect(toList());
    }
}
