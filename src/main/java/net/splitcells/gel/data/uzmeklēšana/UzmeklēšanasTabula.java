package net.splitcells.gel.data.uzmeklēšana;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Rinda;
import net.splitcells.gel.data.table.Tabula;
import net.splitcells.gel.data.table.atribūts.Atribūts;
import net.splitcells.gel.data.table.kolonna.Kolonna;
import org.w3c.dom.Element;
import net.splitcells.dem.data.set.list.Lists;

public class UzmeklēšanasTabula implements Tabula {
    // PĀRSAUKT Nosaukums nenorāda mainīgās nozīmu.
    protected final Tabula tabula;
    protected final String vārds;
    protected final Set<Integer> saturs = setOfUniques();
    protected final List<Kolonna<Object>> kolonnas;
    protected final List<Kolonna<Object>> kolonnasSkats;

    public static UzmeklēšanasTabula uzmeklēšanasTabula(Tabula tabula, String vārds) {
        return new UzmeklēšanasTabula(tabula, vārds);
    }

    public static UzmeklēšanasTabula uzmeklēšanasTabula(Tabula tabula, Atribūts<?> atribūts) {
        return new UzmeklēšanasTabula(tabula, atribūts.vārds());
    }

    protected UzmeklēšanasTabula(Tabula tabula, String vārds) {
        this.tabula = tabula;
        this.vārds = vārds;
        kolonnas = listWithValuesOf
                (tabula.nosaukumuSkats().stream()
                        .map(attribute -> UzmeklēšanasKolonna.lookupColumn(this, attribute))
                        .collect(toList()));
        kolonnasSkats = listWithValuesOf(kolonnas);
    }

    @Override
    public List<Atribūts<Object>> nosaukumuSkats() {
        return tabula.nosaukumuSkats();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Kolonna<T> kolonnaSkats(Atribūts<T> atribūts) {
        int index = 0;
        for (final var headerAttribute : tabula.nosaukumuSkats()) {
            if (headerAttribute.equals(atribūts)) {
                return (Kolonna<T>) kolonnas.get(index);
            }
            ++index;
        }
        throw new IllegalArgumentException(atribūts.toString());
    }

    @Override
    public List<Rinda> jēlaRindasSkats() {
        final var rVal = Lists.<Rinda>list();
        range(0, tabula.jēlaRindasSkats().size()).forEach(i -> {
            final Rinda rElement;
            if (saturs.contains(i)) {
                rElement = tabula.jēlaRindasSkats().get(i);
            } else {
                rElement = null;
            }
            rVal.add(rElement);
        });
        return rVal;
    }

    @Override
    public int izmērs() {
        return saturs.size();
    }

    public void reģistrē(Rinda rinda) {
        saturs.add(rinda.indekss());
        // DARĪT JAUD
        // SALABOT
        range(0, kolonnas.size()).forEach(i -> {
            // KOMPROMISS
            final var kolonna = (UzmeklēšanasKolonna<Object>) kolonnas.get(i);
            kolonna.set(rinda.indekss(), rinda.vērtība(tabula.nosaukumuSkats().get(i)));
        });
        kolonnas.forEach(kolonna -> kolonna.reģistrē_papildinājumi(rinda));
    }

    public void noņemt_reģistrāciju(Rinda rinda) {
        kolonnas.forEach(column -> column.rēgistrē_pirms_noņemšanas(rinda));
        saturs.remove(rinda.indekss());
        range(0, kolonnas.size()).forEach(i -> {
            // HACK
            final var column = (UzmeklēšanasKolonna<Object>) kolonnas.get(i);
            column.set(rinda.indekss(), null);
        });
    }

    @Override
    public List<Kolonna<Object>> kolonnaSkats() {
        return kolonnasSkats;
    }

    public Tabula base() {
        return tabula;
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        final var rVal = tabula.path();
        rVal.add(UzmeklēšanasTabula.class.getSimpleName() + "(" + vārds + ")");
        return rVal;
    }

    @Override
    public Element toDom() {
        final var rVal = element(UzmeklēšanasTabula.class.getSimpleName());
        // REMOVE
        rVal.appendChild(textNode("" + hashCode()));
        rVal.appendChild(element("subject", textNode(path().toString())));
        rVal.appendChild(element("content", textNode(saturs.toString())));
        saturs.forEach(i -> {
            rVal.appendChild(jēlaRindasSkats().get(i).toDom());
        });
        return rVal;
    }

    @Override
    public String toString() {
        return UzmeklēšanasTabula.class.getSimpleName() + path().toString();
    }

    @Override
    public List<Rinda> jēlasRindas() {
        // TASK PERFORMANCE
        final var rVal = Lists.<Rinda>list();
        saturs.forEach(index -> rVal.add(tabula.gūtJēluRindas(index)));
        return rVal;
    }

    @Override
    public Rinda uzmeklēVienādus(Atribūts<Rinda> atribūts, Rinda other) {
        final var rBase = tabula.uzmeklēVienādus(atribūts, other);
        if (saturs.contains(rBase.indekss())) {
            return rBase;
        }
        return null;
    }
}
