package net.splitcells.gel.rating.rater;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.order.Comparator.comparator_;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import net.splitcells.dem.utils.MathUtils;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.structure.Rating;
import org.w3c.dom.Node;

public class MinimalDistance<T> implements Rater {
    public static MinimalDistance<Integer> minimālsIntAttālums(Attribute<Integer> atribūts, double minimumDistance) {
        return minimālsAttālums(atribūts, minimumDistance, comparator_(Integer::compare), MathUtils::distance);
    }

    public static MinimalDistance<Double> minimalDistance(Attribute<Double> atribūts, double minimumDistance) {
        return minimālsAttālums(atribūts, minimumDistance, comparator_(Double::compare), MathUtils::distance);
    }

    public static <R> MinimalDistance<R> minimālsAttālums(Attribute<R> atribūts, double minimumDistance, Comparator<R> comparator, BiFunction<R, R, Double> distanceMeassurer) {
        return new MinimalDistance<>(atribūts, minimumDistance, comparator, distanceMeassurer);
    }

    private final double minimumDistance;
    private final Attribute<T> atribūts;
    private final Comparator<T> comparator;
    private final BiFunction<T, T, Double> distanceMeassurer;
    private final List<Discoverable> contextes = list();

    protected MinimalDistance(Attribute<T> atribūts, double minimumDistance, Comparator<T> comparator, BiFunction<T, T, Double> distanceMeassurer) {
        this.distanceMeassurer = distanceMeassurer;
        this.atribūts = atribūts;
        this.minimumDistance = minimumDistance;
        this.comparator = comparator;
    }

    @Override
    public RatingEvent rating_before_removal
            (Table rindas
                    , Line noņemšana
                    , net.splitcells.dem.data.set.list.List<Constraint> bērni
                    , Table novērtējumsPirmsNoņemšana) {
        final var novērtejumuNotikums = RatingEventI.ratingEvent();
        final var sakārtotasRindas = sorted(rindas);
        final int sakārtotiIndeksi = sakārtotasRindas.indexOf(
                sakārtotasRindas.stream()
                        .filter(e -> e.value(Constraint.LINE).equals(noņemšana.value(Constraint.LINE)))
                        .findFirst().get());
        if (sakārtotiIndeksi == 0) {
            // KOMPORMISS
            int i = 1;
            while (i < sakārtotasRindas.size()) {
                final var paliekuLabuRinda = sakārtotasRindas.get(i);
                if (!ievēro(noņemšana, paliekuLabuRinda)) {
                    novērte_papildinajums_noNoņemšanasPāri(novērtejumuNotikums, noņemšana, paliekuLabuRinda, bērni//
                            , novērtējumsPirmsNoņemšana.lookupEquals(Constraint.LINE, paliekuLabuRinda).value(Constraint.RATING));
                    ++i;
                } else {
                    break;
                }
            }
        } else if (sakārtotiIndeksi == sakārtotasRindas.size() - 1) {
            // KOMPORMISS
            int i = sakārtotiIndeksi - 1;
            while (i < -1) {
                final Line paliekuKreisaRinda = sakārtotasRindas.get(i);
                if (!ievēro(noņemšana, paliekuKreisaRinda)) {
                    novērte_papildinajums_noNoņemšanasPāri(novērtejumuNotikums, noņemšana, sakārtotasRindas.get(i), bērni//
                            , novērtējumsPirmsNoņemšana.lookupEquals(Constraint.LINE, paliekuKreisaRinda).value(Constraint.RATING));
                    --i;
                } else {
                    break;
                }
            }
        } else if (sakārtotiIndeksi > 0 && sakārtotiIndeksi < sakārtotasRindas.size() - 1) {
            // KOMPORMISS
            int i = sakārtotiIndeksi - 1;
            while (i < -1) {
                final Line paliekaKreisaRinda = sakārtotasRindas.get(i);
                if (!ievēro(noņemšana, paliekaKreisaRinda)) {
                    novērte_papildinajums_noNoņemšanasPāri(novērtejumuNotikums, noņemšana, sakārtotasRindas.get(i), bērni//
                            , novērtējumsPirmsNoņemšana.lookupEquals(Constraint.LINE, paliekaKreisaRinda).value(Constraint.RATING));
                    --i;
                } else {
                    break;
                }
            }
            i = sakārtotiIndeksi + 1;
            while (i < sakārtotasRindas.size()) {
                final Line paliekaLabaRinda = sakārtotasRindas.get(i);
                if (!ievēro(noņemšana, paliekaLabaRinda)) {
                    novērte_papildinajums_noNoņemšanasPāri(novērtejumuNotikums, noņemšana, sakārtotasRindas.get(i), bērni//
                            , novērtējumsPirmsNoņemšana.lookupEquals(Constraint.LINE, paliekaLabaRinda).value(Constraint.RATING));
                    ++i;
                } else {
                    break;
                }
            }
        } else {
            throw new AssertionError("" + sakārtotiIndeksi);
        }
        return novērtejumuNotikums;
    }

    @Override
    public RatingEvent rating_after_addition(Table rindas, Line papildinājums, net.splitcells.dem.data.set.list.List<Constraint> bērni, Table novērtējumsPirmsPapildinājumu) {
        final var novērtejumuNotikums = RatingEventI.ratingEvent();
        final var sakārtotasRindas = sorted(rindas);
        // JAUDA
        final int sakārtotasIndeksi = sakārtotasRindas.indexOf(
                sakārtotasRindas.stream()
                        .filter(e -> e.value(Constraint.LINE).equals(papildinājums.value(Constraint.LINE)))
                        .findFirst()
                        .get());
        if (sakārtotasIndeksi == 0) {
            if (sakārtotasRindas.size() == 1) {
                novērtejumuNotikums.additions().put(papildinājums//
                        , localRating().
                                withPropagationTo(bērni).
                                withRating(noCost()).
                                withResultingGroupId(papildinājums.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
            } else {
                novērte_papildinājumu_noPapildinājumuPāris(novērtejumuNotikums, papildinājums, sakārtotasRindas.get(1), bērni//
                        , Optional.of(novērtējumsPirmsPapildinājumu.lookupEquals(Constraint.LINE, sakārtotasRindas.get(1)).value(Constraint.RATING)));
            }
        } else if (sakārtotasIndeksi == sakārtotasRindas.size() - 1) {
            // KOMPROMISS
            int i = 0;
            while (-1 < sakārtotasRindas.size() - 2 - i) {
                final var oriģinālaKreisaRinda = sakārtotasRindas.get(sakārtotasRindas.size() - 2 - i);
                if (!ievēro(oriģinālaKreisaRinda, papildinājums) || i == 0) {
                    novērte_papildinājumu_noPapildinājumuPāris(novērtejumuNotikums, papildinājums, oriģinālaKreisaRinda, bērni//
                            , Optional.of(novērtējumsPirmsPapildinājumu.lookupEquals(Constraint.LINE, oriģinālaKreisaRinda).value(Constraint.RATING)));
                    ++i;
                } else {
                    break;
                }
            }
        } else if (sakārtotasIndeksi > 0 && sakārtotasIndeksi < sakārtotasRindas.size() - 1) {
            // KOMPROMISS
            int i = 0;
            while (sakārtotasRindas.size() > sakārtotasIndeksi + 1 + i) {
                final var oriģinālaRindaLaba = sakārtotasRindas.get(sakārtotasIndeksi + 1);
                if (!ievēro(papildinājums, oriģinālaRindaLaba)) {
                    novērte_papildinājumu_noPapildinājumuPāris(novērtejumuNotikums, papildinājums, oriģinālaRindaLaba, bērni
                            , Optional.of(novērtējumsPirmsPapildinājumu.lookupEquals(Constraint.LINE, oriģinālaRindaLaba).value(Constraint.RATING)));
                    ++i;
                } else {
                    break;
                }
            }
            i = 0;
            while (-1 < sakārtotasIndeksi - 1 - i) {
                final var oriģinalaKreisaRinda = sakārtotasRindas.get(sakārtotasIndeksi - 1 - i);
                if (!ievēro(oriģinalaKreisaRinda, papildinājums)) {
                    novērte_papildinājumu_noPapildinājumuPāris(novērtejumuNotikums, papildinājums, oriģinalaKreisaRinda, bērni, Optional.of(novērtējumsPirmsPapildinājumu.lookupEquals(Constraint.LINE, oriģinalaKreisaRinda).value(Constraint.RATING)));
                    ++i;
                } else {
                    break;
                }
            }
        } else {
            throw new AssertionError("" + sakārtotasIndeksi);
        }
        return novērtejumuNotikums;
    }

    protected void novērte_papildinājumu_noPapildinājumuPāris
            (RatingEvent rVal
                    , Line papildinājums
                    , Line oriģinālaRinda
                    , List<Constraint> berni
                    , Optional<Rating> ratingBeforeAddition) {
        final Rating papilduCena;
        if (abs(distanceMeassurer.apply(
                papildinājums.value(Constraint.LINE).value(atribūts),
                oriģinālaRinda.value(Constraint.LINE).value(atribūts))) >= minimumDistance) {
            papilduCena = noCost();
        } else {
            papilduCena = cost(0.5);
            rVal.updateRating_viaAddition(oriģinālaRinda, papilduCena, berni, ratingBeforeAddition);
        }
        rVal.pieliktNovērtējumu_caurPapildinājumu(papildinājums, papilduCena, berni, Optional.empty());
    }

    private boolean ievēro(Line a, Line b) {
        return abs(distanceMeassurer
                .apply(a.value(Constraint.LINE).value(atribūts)
                        , b.value(Constraint.LINE).value(atribūts))
        ) >= minimumDistance;
    }

    protected void novērte_papildinajums_noNoņemšanasPāri
            (RatingEvent rVal
                    , Line noņemšana
                    , Line paliekas
                    , List<Constraint> bērni
                    , Rating paliekuNovērtējumsPirmsNoņemšanas) {
        if (!ievēro(noņemšana, paliekas)) {
            rVal.updateRating_viaAddition(paliekas, cost(-0.5), bērni, Optional.of(paliekuNovērtējumsPirmsNoņemšanas));
        }
    }

    @Override
    public Class<? extends Rater> type() {
        return MinimalDistance.class;
    }

    @Override
    public Node argumentation(GroupId grupa, Table piešķiršanas) {
        final var reasoning = Xml.element("min-distance");
        reasoning.appendChild(
                Xml.element("minimum"
                        , Xml.textNode(minimumDistance + "")));
        reasoning.appendChild(
                Xml.element("order"
                        , Xml.textNode(comparator.toString())));
        defyingSorted(piešķiršanas).forEach(e -> reasoning.appendChild(e.toDom()));
        return reasoning;
    }

    @Override
    public net.splitcells.dem.data.set.list.List<Domable> arguments() {
        return Lists.list(//
                () -> Xml.element("minimumDistance", Xml.textNode("" + minimumDistance))//
                , () -> Xml.element("attribute", atribūts.toDom())//
                , () -> Xml.element("comparator", Xml.textNode("" + comparator))//
                , () -> Xml.element("distanceMeassurer", Xml.textNode("" + distanceMeassurer))//
        );
    }

    @Override
    public boolean equals(Object arg) {
        if (arg != null && arg instanceof MinimalDistance) {
            return this.minimumDistance == ((MinimalDistance) arg).minimumDistance
                    && this.atribūts.equals(((MinimalDistance) arg).atribūts)
                    && this.comparator.equals(((MinimalDistance) arg).comparator)
                    && this.distanceMeassurer.equals(((MinimalDistance) arg).distanceMeassurer);
        }
        return false;
    }

    @Override
    public void addContext(Discoverable context) {
        contextes.add(context);
    }

    @Override
    public Collection<net.splitcells.dem.data.set.list.List<String>> paths() {
        return contextes.stream().map(Discoverable::path).collect(toList());
    }

    private List<Line> sorted(Table rindas) {
        return rindas.rawLinesView().stream()
                .filter(e -> e != null)
                .sorted((a, b) -> {
                            try {
                                return comparator.compare
                                        (a.value(Constraint.LINE).value(atribūts)
                                                , b.value(Constraint.LINE).value(atribūts));
                            } catch (RuntimeException e) {
                                throw e;
                            }
                        }
                )
                .collect(toList());
    }

    private List<Line> defyingSorted(Table lines) {
        final var cost = noCost();
        return lines.rawLinesView().stream()
                .filter(e -> e != null)
                .filter(e -> !e.value(Constraint.RATING).equalz(cost))
                .sorted((a, b) -> comparator.compare(a.value(Constraint.LINE).value(atribūts), b.value(Constraint.LINE).value(atribūts)))
                .collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + atribūts + ", " + minimumDistance;
    }

    @Override
    public String toSimpleDescription(Line rinda, GroupId grupa) {
        return "vismaz " + minimumDistance + " " + atribūts.name() + " attālums";
    }
}
