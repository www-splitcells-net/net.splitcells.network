package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.data.table.attribute.Attribute;
import org.w3c.dom.Element;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.Xml.directChildElementByName;
import static net.splitcells.dem.lang.Xml.directChildElementsByName;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;

public interface DatabaseFactory extends Closeable, Flushable {
    Database datuBāze(String vārds, Attribute<? extends Object>... atribūti);

    Database datuBāze(Attribute<? extends Object>... atribūti);

    @Deprecated
    default Database database(List<Attribute<?>> atribūti) {
        return datuBāze(atribūti);
    }

    Database datuBāze(List<Attribute<?>> atribūti);

    Database datuBāze(List<Attribute<? extends Object>> atribūti, List<List<Object>> rindasVertības);

    @Deprecated
    Database datuBāze(String vārds, Discoverable vecāks, Attribute<? extends Object>... atribūti);

    Database datuBāze(String vārds, Discoverable vecāks, List<Attribute<? extends Object>> atribūti);

    default Database datuBāzeNoFods(List<Attribute<?>> atribūti, Element fods) {
        final var datuBāzeNoFods = datuBāze(atribūti);
        final var body = directChildElementByName(fods, "body", FODS_OFFICE);
        final var speardsheet = directChildElementByName(body, "spreadsheet", FODS_OFFICE);
        final var table = directChildElementByName(speardsheet, "table", FODS_TABLE);
        directChildElementsByName(table, "table-row", FODS_TABLE)
                .skip(1)
                .map(row -> rindaNoFodsRow(atribūti, row))
                .forEach(rindasVērtības -> datuBāzeNoFods.addTranslated(rindasVērtības));
        return datuBāzeNoFods;
    }

    private static List<Object> rindaNoFodsRow(List<Attribute<?>> atribūti, Element row) {
        final var tableCells = directChildElementsByName(row, "table-cell", FODS_TABLE)
                .collect(toList());
        return range(0, atribūti.size())
                .mapToObj(i -> atribūti.get(i).deserializeValue(
                        Xml.directChildElements(tableCells.get(i))
                                .filter(e -> FODS_TEXT.uri().equals(e.getNamespaceURI()))
                                .findFirst()
                                .get()
                                .getTextContent()))
                .collect(toList());
    }
}
