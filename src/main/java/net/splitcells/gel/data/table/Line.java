package net.splitcells.gel.data.table;

import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.gel.data.table.LinePointerI.rindasRādītājs;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface Line extends Domable {

    static List<?> saķēdet(Line... rindas) {
        final List<Object> rVal = list();
        for (var rinda : rindas) {
            rinda.konteksts().headerView()
                    .forEach(attribute -> rVal.add(rinda.vērtība(attribute)));
        }
        return rVal;
    }

    <T> T vērtība(Attribute<T> atribūts);

    int index();

    default LinePointer uzRindaRādītājs() {
        return rindasRādītājs(konteksts(), index());
    }

    Table konteksts();

    default boolean vienāds(Line arg) {
        return index() == arg.index() && konteksts().equals(arg.konteksts());
    }

    default boolean irDerīgs() {
        return null != konteksts().rawLinesView().get(index());
    }

    default List<String> toStringList() {
        return listWithValuesOf
                (konteksts().headerView().stream()
                        .map(atribūts -> vērtība(atribūts).toString())
                        .collect(toList()));
    }

    default List<Object> vērtības() {
        return konteksts()
                .headerView()
                .stream()
                .map(nosaukums -> vērtība(nosaukums))
                .collect(toList());
    }
}
