package net.splitcells.gel.kodols.dati.uzmeklēšana;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.gel.kodols.dati.uzmeklēšana.UzmeklēšanasTabula.uzmeklēšanasTabula;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

public class UzmeklēšanaI<T> implements Uzmeklēšana<T> {
    private final UzmeklēšanasTabula tukšaUzmeklēšanasTabula;

    public static <R> Uzmeklēšana<R> uzmeklē(Tabula tabula, Atribūts<R> atribūts) {
        return new UzmeklēšanaI<>(tabula, atribūts);
    }

    protected final Tabula tabula;
    protected final Map<T, UzmeklēšanasTabula> saturs = map();
    protected final Atribūts<T> atribūts;
    protected final Map<Predicate<T>, UzmeklēšanasTabula> agregātsSaturs = map();

    protected UzmeklēšanaI(Tabula tabula, Atribūts<T> atribūts) {
        this.tabula = tabula;
        this.tukšaUzmeklēšanasTabula = uzmeklēšanasTabula(tabula, atribūts);
        this.atribūts = atribūts;
        tabula.jēlaRindasSkats().stream()
                .filter(e -> e != null)
                .forEach(e -> reģistrē_papildinājums(e.vērtība(atribūts), e.indekss()));
    }

    @Override
    public void reģistrē_papildinājums(T papildinājums, int indekss) {
        {
            final UzmeklēšanasTabula uzmeklēšanasTabula;
            if (saturs.containsKey(papildinājums)) {
                uzmeklēšanasTabula = saturs.get(papildinājums);
            } else {
                uzmeklēšanasTabula = uzmeklēšanasTabula(tabula, atribūts);
                saturs.put(papildinājums, uzmeklēšanasTabula);
            }
            uzmeklēšanasTabula.reģistrē(tabula.jēlaRindasSkats().get(indekss));
        }
        reģistrē_papildinājums_priekš_agregātuSaturs(papildinājums, indekss);
    }

    private void reģistrē_papildinājums_priekš_agregātuSaturs(T papildinājums, int indekss) {
        agregātsSaturs.forEach((predikāts, uzmeklēšanasTabula) -> {
            if (predikāts.test(papildinājums)) {
                uzmeklēšanasTabula.reģistrē(tabula.jēlaRindasSkats().get(indekss));
            }
        });
    }

    @Override
    public void reģistē_noņemšana(T noņemšana, int indekss) {
        final var rinda = tabula.jēlaRindasSkats().get(indekss);
        saturs.get(noņemšana).noņemt_reģistrāciju(rinda);
        // atkritumu kolekcija
        if (saturs.get(noņemšana).irTukšs()) {
            saturs.remove(noņemšana);
        }
        agregātsSaturs.forEach((predikāts, uzmeklēšanasTabula) -> {
            if (predikāts.test(noņemšana)) {
                uzmeklēšanasTabula.noņemt_reģistrāciju(tabula.jēlaRindasSkats().get(indekss));
            }
        });
    }

    @Override
    public Tabula uzmeklēšana(T vertība) {
        if (saturs.containsKey(vertība)) {
            return saturs.get(vertība);
        }
        return tukšaUzmeklēšanasTabula;
    }

    @Override
    public Tabula uzmeklēšana(Predicate<T> predikāts) {
        if (!agregātsSaturs.containsKey(predikāts)) {
            final var uzmeklēšana = uzmeklēšanasTabula(tabula, predikāts.toString());
            agregātsSaturs.put(predikāts, uzmeklēšana);
            tabula.jēlaRindasSkats().stream()
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
