package net.splitcells.gel.kodols.dati.tabula;

import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.gel.kodols.dati.tabula.RindaRādītājsI.rindasRādītājs;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

public interface Rinda extends Domable {

    static List<?> saķēdet(Rinda... rindas) {
        final List<Object> rVal = list();
        for (var rinda : rindas) {
            rinda.konteksts().nosaukumuSkats()
                    .forEach(attribute -> rVal.add(rinda.vērtība(attribute)));
        }
        return rVal;
    }

    <T> T vērtība(Atribūts<T> atribūts);

    int indekss();

    default RindaRādītājs uzRindaRādītājs() {
        return rindasRādītājs(konteksts(), indekss());
    }

    Tabula konteksts();

    default boolean vienāds(Rinda arg) {
        return indekss() == arg.indekss() && konteksts().equals(arg.konteksts());
    }

    default boolean irDerīgs() {
        return null != konteksts().jēlaRindasSkats().get(indekss());
    }

    default List<String> toStringList() {
        return listWithValuesOf
                (konteksts().nosaukumuSkats().stream()
                        .map(atribūts -> vērtība(atribūts).toString())
                        .collect(toList()));
    }

    default List<Object> vērtības() {
        return konteksts()
                .nosaukumuSkats()
                .stream()
                .map(nosaukums -> vērtība(nosaukums))
                .collect(toList());
    }
}
