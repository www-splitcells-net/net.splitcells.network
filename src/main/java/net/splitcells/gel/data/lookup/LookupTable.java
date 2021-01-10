package net.splitcells.gel.data.lookup;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import org.w3c.dom.Element;
import net.splitcells.dem.data.set.list.Lists;

public class LookupTable implements Table {
    // PĀRSAUKT Nosaukums nenorāda mainīgās nozīmu.
    protected final Table tabula;
    protected final String vārds;
    protected final Set<Integer> saturs = setOfUniques();
    protected final List<Column<Object>> kolonnas;
    protected final List<Column<Object>> kolonnasSkats;

    public static LookupTable uzmeklēšanasTabula(Table tabula, String vārds) {
        return new LookupTable(tabula, vārds);
    }

    public static LookupTable uzmeklēšanasTabula(Table tabula, Attribute<?> atribūts) {
        return new LookupTable(tabula, atribūts.name());
    }

    protected LookupTable(Table tabula, String vārds) {
        this.tabula = tabula;
        this.vārds = vārds;
        kolonnas = listWithValuesOf
                (tabula.headerView().stream()
                        .map(attribute -> LookupColumn.lookupColumn(this, attribute))
                        .collect(toList()));
        kolonnasSkats = listWithValuesOf(kolonnas);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return tabula.headerView();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Column<T> columnView(Attribute<T> atribūts) {
        int index = 0;
        for (final var headerAttribute : tabula.headerView()) {
            if (headerAttribute.equals(atribūts)) {
                return (Column<T>) kolonnas.get(index);
            }
            ++index;
        }
        throw new IllegalArgumentException(atribūts.toString());
    }

    @Override
    public List<Line> rawLinesView() {
        final var rVal = Lists.<Line>list();
        range(0, tabula.rawLinesView().size()).forEach(i -> {
            final Line rElement;
            if (saturs.contains(i)) {
                rElement = tabula.rawLinesView().get(i);
            } else {
                rElement = null;
            }
            rVal.add(rElement);
        });
        return rVal;
    }

    @Override
    public int size() {
        return saturs.size();
    }

    public void reģistrē(Line rinda) {
        saturs.add(rinda.index());
        // DARĪT JAUD
        // SALABOT
        range(0, kolonnas.size()).forEach(i -> {
            // KOMPROMISS
            final var kolonna = (LookupColumn<Object>) kolonnas.get(i);
            kolonna.set(rinda.index(), rinda.value(tabula.headerView().get(i)));
        });
        kolonnas.forEach(kolonna -> kolonna.register_addition(rinda));
    }

    public void noņemt_reģistrāciju(Line rinda) {
        kolonnas.forEach(column -> column.register_before_removal(rinda));
        saturs.remove(rinda.index());
        range(0, kolonnas.size()).forEach(i -> {
            // HACK
            final var column = (LookupColumn<Object>) kolonnas.get(i);
            column.set(rinda.index(), null);
        });
    }

    @Override
    public List<Column<Object>> columnsView() {
        return kolonnasSkats;
    }

    public Table base() {
        return tabula;
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        final var rVal = tabula.path();
        rVal.add(LookupTable.class.getSimpleName() + "(" + vārds + ")");
        return rVal;
    }

    @Override
    public Element toDom() {
        final var rVal = element(LookupTable.class.getSimpleName());
        // REMOVE
        rVal.appendChild(textNode("" + hashCode()));
        rVal.appendChild(element("subject", textNode(path().toString())));
        rVal.appendChild(element("content", textNode(saturs.toString())));
        saturs.forEach(i -> {
            rVal.appendChild(rawLinesView().get(i).toDom());
        });
        return rVal;
    }

    @Override
    public String toString() {
        return LookupTable.class.getSimpleName() + path().toString();
    }

    @Override
    public List<Line> rawLines() {
        // TASK PERFORMANCE
        final var rVal = Lists.<Line>list();
        saturs.forEach(index -> rVal.add(tabula.getRawLine(index)));
        return rVal;
    }

    @Override
    public Line lookupEquals(Attribute<Line> atribūts, Line other) {
        final var rBase = tabula.lookupEquals(atribūts, other);
        if (saturs.contains(rBase.index())) {
            return rBase;
        }
        return null;
    }
}
