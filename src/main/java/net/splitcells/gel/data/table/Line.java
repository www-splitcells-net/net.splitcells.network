package net.splitcells.gel.data.table;

import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.gel.data.table.LinePointerI.linePointer;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface Line extends Domable {

    static List<?> concat(Line... lines) {
        final List<Object> concatination = list();
        for (var line : lines) {
            line.context().headerView()
                    .forEach(attribute -> concatination.add(line.value(attribute)));
        }
        return concatination;
    }

    <T> T value(Attribute<T> attribute);

    int index();

    default LinePointer toLinePointer() {
        return linePointer(context(), index());
    }

    Table context();

    default boolean equalsTo(Line other) {
        return index() == other.index() && context().equals(other.context());
    }

    default boolean isValid() {
        return null != context().rawLinesView().get(index());
    }

    default List<String> toStringList() {
        return listWithValuesOf
                (context().headerView().stream()
                        .map(attribute -> value(attribute).toString())
                        .collect(toList()));
    }

    default List<Object> values() {
        return context()
                .headerView()
                .stream()
                .map(attribute -> value(attribute))
                .collect(toList());
    }
}
