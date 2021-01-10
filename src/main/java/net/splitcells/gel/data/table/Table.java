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
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface Table extends Discoverable, Domable {
    List<Attribute<Object>> headerView();

    <T> ColumnView<T> columnView(Attribute<T> atribūts);

    List<Column<Object>> columnsView();

    ListView<Line> rawLinesView();

    default boolean contains(Line rinda) {
        if (rinda.index() >= rawLinesView().size()) {
            return false;
        } else {
            return null != rawLinesView().get(rinda.index());
        }
    }

    default net.splitcells.dem.data.set.list.List<Line> getLines() {
        return listWithValuesOf
                (rawLinesView().stream()
                        .filter(e -> e != null)
                        .collect(Collectors.toList()));
    }

    default Line getRawLines(int indekss) {
        return rawLinesView().get(indekss);
    }

    default Line getLines(int indekss) {
        return getLines().get(indekss);
    }

    int size();

    default boolean isEmpty() {
        return 0 == size();
    }

    default boolean hasContent() {
        return !isEmpty();
    }

    @Deprecated
    List<Line> rawLines();

    default String toCSV() {
        final var csv = new StringBuffer();
        final var header = headerView().stream()
                .map(atribūts -> atribūts.name())
                .collect(toList());
        try (final var printeris = new CSVPrinter
                (csv, CSVFormat.RFC4180.withHeader(header.toArray(new String[header.size()])))) {
            getLines().stream()
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
    Line lookupEquals(Attribute<Line> atribūts, Line other);

    default Stream<Line> lookupEquals(List<Object> vertības) {
        return getLines().stream()
                .filter(rinda ->
                        IntStream.range(0, headerView().size())
                                .mapToObj(i -> Objects.equals(vertības.get(i), rinda.value(headerView().get(i))))
                                .reduce(true, (a, b) -> a && b));
    }

    default Element toFods() {
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
                headerView().stream()
                        .map(att -> att.name())
                        .map(attName -> {
                            final var tabulasElements = element(FODS_TABLE, "table-cell");
                            final var tabulasVertība = rElement(FODS_TEXT, "p");
                            tabulasElements.appendChild(tabulasVertība);
                            tabulasVertība.appendChild(textNode(attName));
                            return tabulasElements;
                        }).forEach(attDesc -> nosaukums.appendChild(attDesc));
                getLines().forEach(line -> {
                    final var tabulasRinda = element(FODS_TABLE, "table-row");
                    tabula.appendChild(tabulasRinda);
                    headerView().stream()
                            .map(atribūts -> line.value(atribūts))
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
