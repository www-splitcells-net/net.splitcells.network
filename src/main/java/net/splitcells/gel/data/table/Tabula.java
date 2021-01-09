package net.splitcells.gel.data.table;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_OFFICE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TABLE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TEXT;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.table.kolonna.Kolonna;
import net.splitcells.gel.data.table.kolonna.KolonnaSkats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.atribūts.Atribūts;

public interface Tabula extends Discoverable, Domable {
    List<Atribūts<Object>> nosaukumuSkats();

    <T> KolonnaSkats<T> kolonnaSkats(Atribūts<T> atribūts);

    List<Kolonna<Object>> kolonnaSkats();

    ListView<Rinda> jēlaRindasSkats();

    default boolean satur(Rinda rinda) {
        if (rinda.indekss() >= jēlaRindasSkats().size()) {
            return false;
        } else {
            return null != jēlaRindasSkats().get(rinda.indekss());
        }
    }

    default net.splitcells.dem.data.set.list.List<Rinda> gūtRindas() {
        return listWithValuesOf
                (jēlaRindasSkats().stream()
                        .filter(e -> e != null)
                        .collect(Collectors.toList()));
    }

    default Rinda gūtJēluRindas(int indekss) {
        return jēlaRindasSkats().get(indekss);
    }

    default Rinda gūtRinda(int indekss) {
        return gūtRindas().get(indekss);
    }

    int izmērs();

    default boolean irTukšs() {
        return 0 == izmērs();
    }

    default boolean navTukšs() {
        return !irTukšs();
    }

    @Deprecated
    List<Rinda> jēlasRindas();

    default String uzCSV() {
        final var csv = new StringBuffer();
        final var header = nosaukumuSkats().stream()
                .map(atribūts -> atribūts.vārds())
                .collect(toList());
        try (final var printeris = new CSVPrinter
                (csv, CSVFormat.RFC4180.withHeader(header.toArray(new String[header.size()])))) {
            gūtRindas().stream()
                    .map(rinda -> rinda.toStringList())
                    .forEach(rinda -> {
                        try {
                            printeris.printRecord(rinda);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return csv.toString();
    }

    /**
     * PĀRSAUKT
     */
    Rinda uzmeklēVienādus(Atribūts<Rinda> atribūts, Rinda other);

    default Stream<Rinda> uzmeklēVienādus(List<Object> vertības) {
        return gūtRindas().stream()
                .filter(rinda ->
                        IntStream.range(0, nosaukumuSkats().size())
                                .mapToObj(i -> Objects.equals(vertības.get(i), rinda.vērtība(nosaukumuSkats().get(i))))
                                .reduce(true, (a, b) -> a && b));
    }

    default Element uzFods() {
        final var fods = rElement(FODS_OFFICE, "document");
        final var ķermenis = element(FODS_OFFICE, "body");
        fods.setAttributeNode
                (attribute(FODS_OFFICE, "mimetype", "application/vnd.oasis.opendocument.spreadsheet"));
        fods.appendChild(ķermenis);
        {
            final var izklājlapu = element(FODS_OFFICE, "spreadsheet");
            ķermenis.appendChild(izklājlapu);
            final var tabula = rElement(FODS_TABLE, "table");
            izklājlapu.appendChild(tabula);
            tabula.setAttributeNode(attribute(FODS_TABLE, "name", "values"));
            {
                final var nosaukums = element(FODS_TABLE, "table-row");
                tabula.appendChild(nosaukums);
                nosaukumuSkats().stream()
                        .map(att -> att.vārds())
                        .map(attName -> {
                            final var tabulasElements = element(FODS_TABLE, "table-cell");
                            final var tabulasVertība = rElement(FODS_TEXT, "p");
                            tabulasElements.appendChild(tabulasVertība);
                            tabulasVertība.appendChild(textNode(attName));
                            return tabulasElements;
                        }).forEach(attDesc -> nosaukums.appendChild(attDesc));
                gūtRindas().forEach(line -> {
                    final var tabulasRinda = element(FODS_TABLE, "table-row");
                    tabula.appendChild(tabulasRinda);
                    nosaukumuSkats().stream()
                            .map(atribūts -> line.vērtība(atribūts))
                            .map(vertība -> {
                                final var tabulasElements = element(FODS_TABLE, "table-cell");
                                final var tabulasVertības = rElement(FODS_TEXT, "p");
                                tabulasElements.appendChild(tabulasVertības);
                                if (vertība instanceof Domable) {
                                    final var vertībasDom = ((Domable) vertība).toDom();
                                    tabulasVertības.appendChild
                                            (textNode(toPrettyString(vertībasDom)));
                                } else {
                                    tabulasVertības.appendChild(textNode(vertība.toString()));
                                }
                                return tabulasElements;
                            }).forEach(tabulasElements -> tabulasRinda.appendChild(tabulasElements));
                });
            }
        }
        return fods;
    }
}
