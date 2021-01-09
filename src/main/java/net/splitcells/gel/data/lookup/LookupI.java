package net.splitcells.gel.data.lookup;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.data.lookup.LookupTable.uzmeklēšanasTabula;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

public class LookupI<T> implements Lookup<T> {
    private final LookupTable tukšaUzmeklēšanasTabula;

    protected final Table tabula;
    protected final Map<T, LookupTable> saturs = map();
    protected final Attribute<T> atribūts;
    protected final Map<Predicate<T>, LookupTable> agregātsSaturs = map();

    protected LookupI(Table tabula, Attribute<T> atribūts) {
        this.tabula = tabula;
        this.tukšaUzmeklēšanasTabula = uzmeklēšanasTabula(tabula, atribūts);
        this.atribūts = atribūts;
        tabula.rawLinesView().stream()
                .filter(e -> e != null)
                .forEach(e -> reģistrē_papildinājums(e.vērtība(atribūts), e.indekss()));
    }

    @Override
    public void reģistrē_papildinājums(T papildinājums, int indekss) {
        {
            final LookupTable uzmeklēšanasTabula;
            if (saturs.containsKey(papildinājums)) {
                uzmeklēšanasTabula = saturs.get(papildinājums);
            } else {
                uzmeklēšanasTabula = uzmeklēšanasTabula(tabula, atribūts);
                saturs.put(papildinājums, uzmeklēšanasTabula);
            }
            uzmeklēšanasTabula.reģistrē(tabula.rawLinesView().get(indekss));
        }
        reģistrē_papildinājums_priekš_agregātuSaturs(papildinājums, indekss);
    }

    private void reģistrē_papildinājums_priekš_agregātuSaturs(T papildinājums, int indekss) {
        agregātsSaturs.forEach((predikāts, uzmeklēšanasTabula) -> {
            if (predikāts.test(papildinājums)) {
                uzmeklēšanasTabula.reģistrē(tabula.rawLinesView().get(indekss));
            }
        });
    }

    @Override
    public void reģistē_noņemšana(T noņemšana, int indekss) {
        final var rinda = tabula.rawLinesView().get(indekss);
        saturs.get(noņemšana).noņemt_reģistrāciju(rinda);
        // atkritumu kolekcija
        if (saturs.get(noņemšana).isEmpty()) {
            saturs.remove(noņemšana);
        }
        agregātsSaturs.forEach((predikāts, uzmeklēšanasTabula) -> {
            if (predikāts.test(noņemšana)) {
                uzmeklēšanasTabula.noņemt_reģistrāciju(tabula.rawLinesView().get(indekss));
            }
        });
    }

    @Override
    public Table uzmeklēšana(T vertība) {
        if (saturs.containsKey(vertība)) {
            return saturs.get(vertība);
        }
        return tukšaUzmeklēšanasTabula;
    }

    @Override
    public Table uzmeklēšana(Predicate<T> predikāts) {
        if (!agregātsSaturs.containsKey(predikāts)) {
            final var uzmeklēšana = uzmeklēšanasTabula(tabula, predikāts.toString());
            agregātsSaturs.put(predikāts, uzmeklēšana);
            tabula
                    .rawLinesView()
                    .stream()
                    .filter(e -> e != null)
                    .forEach(e -> {
                        if (predikāts.test(e.vērtība(atribūts))) {
                            uzmeklēšana.reģistrē(e);
                        }
                    });
        }
        return agregātsSaturs.get(predikāts);
    }

    @Override
    public List<String> path() {
        final List<String> taka = tabula.path();
        taka.add(atribūts.vārds());
        return taka;
    }
}
