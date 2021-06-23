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

    public static Database database(String name, Attribute<? extends Object>... attributes) {
        return environment().config().configValue(Databases.class).database(name, attributes);
    }

    public static Database database(Attribute<? extends Object>... attributes) {
        return environment().config().configValue(Databases.class).database(attributes);
    }

    public static Database database(List<Attribute<? extends Object>> attributes, List<List<Object>> linesValues) {
        return environment().config().configValue(Databases.class).database(attributes, linesValues);
    }

    @Deprecated
    public static Database database(String name, Discoverable parent, Attribute<? extends Object>... attributes) {
        return environment().config().configValue(Databases.class).database(name, parent, attributes);
    }

    public static Database database(String name, Discoverable parent, List<Attribute<? extends Object>> attributes) {
        return environment().config().configValue(Databases.class).database(name, parent, attributes);
    }

    public static Database database(List<Attribute<?>> attributes) {
        return environment().config().configValue(Databases.class).database(attributes);
    }

    public static Database database2(List<Attribute<Object>> attributes) {
        return environment().config().configValue(Databases.class).database(attributes.mapped(a -> (Attribute<Object>) a));
    }

    public static Database database2(String name, Discoverable parent, List<Attribute<Object>> attributes) {
        return environment().config().configValue(Databases.class).database2(name, parent, attributes);
    }

    public static Database databaseOfFods(List<Attribute<?>> attributes, Element fods) {
        return environment().config().configValue(Databases.class).databaseOfFods(attributes, fods);
    }

    public static List<Attribute<? extends Object>> objectAttributes(List<Attribute<Object>> attributes) {
        return attributes.stream()
                .map(a -> (Attribute<? extends Object>) a)
                .collect(toList());
    }
}
