package net.splitcells.gel.kodols.dati.datubāze;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import org.w3c.dom.Element;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.Xml.directChildElementByName;
import static net.splitcells.dem.lang.Xml.directChildElementsByName;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_OFFICE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TABLE;

public interface DatuBāzesVeidotajs extends Closeable, Flushable {
    DatuBāze datuBāze(String vārds, Atribūts<? extends Object>... atribūti);

    DatuBāze datuBāze(Atribūts<? extends Object>... atribūti);

    @Deprecated
    default DatuBāze database(List<Atribūts<?>> atribūti) {
        return datuBāze(atribūti);
    }

    DatuBāze datuBāze(List<Atribūts<?>> atribūti);

    DatuBāze datuBāze(List<Atribūts<? extends Object>> atribūti, List<List<Object>> rindasVertības);

    @Deprecated
    DatuBāze datuBāze(String vārds, Discoverable vecāks, Atribūts<? extends Object>... atribūti);

    DatuBāze datuBāze(String vārds, Discoverable vecāks, List<Atribūts<? extends Object>> atribūti);

    default DatuBāze datuBāzeNoFods(List<Atribūts<?>> atribūti, Element fods) {
        final var datuBāzeNoFods = datuBāze(atribūti);
        {
            final var body = directChildElementByName(fods, "body", FODS_OFFICE);
            final var speardsheet = directChildElementByName(body, "spreadsheet", FODS_OFFICE);
            final var table = directChildElementByName(speardsheet, "table", FODS_TABLE);
            directChildElementsByName(table, "table-row", FODS_TABLE)
                    .map(row -> rindaNoFodsRow(atribūti, row))
                    .forEach(rindasVērtības -> datuBāzeNoFods.pieliktUnPārtulkot(rindasVērtības));
        }
        return datuBāzeNoFods;
    }

    private static List<Object> rindaNoFodsRow(List<Atribūts<?>> atribūti, Element row) {
        final var tableCells = directChildElementsByName(row, "table-cell", FODS_TABLE)
                .collect(toList());
        return range(0, tableCells.size())
                .mapToObj(i -> atribūti.get(i).deserializēVērtību(tableCells.get(i).getNodeValue()))
                .collect(toList());
    }
}
