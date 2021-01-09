package net.splitcells.gel.rating.rater;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.utils.IncorrectImplementation.incorrectImplementation;
import static net.splitcells.gel.rating.type.Cost.cena;
import static net.splitcells.gel.rating.type.Cost.bezMaksas;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;
import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.constraint.Ierobežojums;

public class AllDifferent<T> implements Rater {
    private final Atribūts<T> atribūts;
    private final Map<T, Predicate<Rinda>> predikāts = map();
    private final Function<T, Predicate<Rinda>> predikātaRažotājs;
    private final List<Discoverable> kontekts = list();

    public static <R> AllDifferent<R> visiemAtšķirība(Atribūts<R> arg) {
        return new AllDifferent<>(arg);
    }

    private AllDifferent(Atribūts<T> arg) {
        atribūts = arg;
        predikātaRažotājs = value -> {
            return line -> line.vērtība(atribūts).equals(value);
        };
    }

    private Predicate<Rinda> predikāts(T value) {
        if (!predikāts.containsKey(value)) {
            predikāts.put(value, predikātaRažotājs.apply(value));
        }
        return predikāts.get(value);
    }

    @Override
    public RatingEvent vērtē_pēc_papildinājumu(Tabula rindas, Rinda papildinājums, net.splitcells.dem.data.set.list.List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final T vertība = papildinājums.vērtība(Ierobežojums.RINDA).vērtība(atribūts);
        final var grupa = rindas.kolonnaSkats(Ierobežojums.RINDA).uzmeklēšana(predikāts(vertība));
        final var novērtejumuNotikums = NovērtējumsNotikumsI.novērtejumuNotikums();
        if (1 == grupa.izmērs()) {
            novērtejumuNotikums.papildinājumi().put(
                    papildinājums
                    , lokalsNovērtejums()
                            .arIzdalīšanaUz(bērni)
                            .arNovērtējumu(bezMaksas())
                            .arRadītuGrupasId(papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
        } else if (2 == grupa.izmērs()) {
            if (ENFORCING_UNIT_CONSISTENCY) {
                assertThat(grupa.jēlaRindasSkats().stream().filter(e -> e != null)).hasSize(2);
            }
            // DARĪT Parametrizē pārbaudi.
            grupa.jēlaRindasSkats().stream()
                    .filter(e -> e != null)
                    .forEach(e -> novērtejumuNotikums.papildinājumi()
                            .put(e//
                                    , lokalsNovērtejums()
                                            .arIzdalīšanaUz(bērni)
                                            .arNovērtējumu(cena(1.0))
                                            .arRadītuGrupasId(papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID))));
            grupa.jēlaRindasSkats().stream()
                    .filter(e -> e != null)
                    .filter(e -> e.indekss() != papildinājums.indekss())
                    .forEach(e -> novērtejumuNotikums.noņemšana().add(e));
        } else if (2 < grupa.izmērs()) {
            // DARĪT Parametrizē pārbaudi.
            novērtejumuNotikums.papildinājumi().put
                    (papildinājums
                            , lokalsNovērtejums()
                                    .arIzdalīšanaUz(bērni)
                                    .arNovērtējumu(cena(1.0))
                                    .arRadītuGrupasId(papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
        } else {
            throw incorrectImplementation("" + grupa.izmērs());
        }
        return novērtejumuNotikums;
    }

    @Override
    public RatingEvent vērtē_pirms_noņemšana(Tabula rindas, Rinda noņemšana, net.splitcells.dem.data.set.list.List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        final T vērtība = noņemšana.vērtība(Ierobežojums.RINDA).vērtība(atribūts);
        final var grupa = rindas.kolonnaSkats(Ierobežojums.RINDA).uzmeklēšana(predikāts(vērtība));
        final var novērtejumuNotikums = NovērtējumsNotikumsI.novērtejumuNotikums();
        if (1 == grupa.izmērs()) {
            // Before removal there was 1 duplication and now there is now duplicate lines
            // for this value present anymore.
            if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
                assertThat(grupa.jēlaRindasSkats().stream().filter(e -> e != null)).hasSize(1);
            }
            grupa.jēlaRindasSkats().stream()
                    .filter(e -> e != null)
                    .forEach(e -> {
                        novērtejumuNotikums.noņemšana().add(e);
                        novērtejumuNotikums.papildinājumi()
                                .put(e
                                        , lokalsNovērtejums()
                                                .arIzdalīšanaUz(bērni)
                                                .arNovērtējumu(bezMaksas())
                                                .arRadītuGrupasId(noņemšana.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
                    });
        }
        return novērtejumuNotikums;
    }

    @Override
    public Class<? extends Rater> type() {
        return AllDifferent.class;
    }

    @Override
    public net.splitcells.dem.data.set.list.List<Domable> arguments() {
        return list(atribūts);
    }

    @Override
    public void addContext(Discoverable context) {
        kontekts.add(context);
    }

    @Override
    public Collection<net.splitcells.dem.data.set.list.List<String>> paths() {
        return kontekts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + atribūts.vārds();
    }
}
