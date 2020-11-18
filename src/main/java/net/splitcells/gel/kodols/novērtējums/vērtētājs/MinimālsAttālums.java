package net.splitcells.gel.kodols.novērtējums.vērtētājs;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.order.Comparator.comparator_;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.kodols.ierobežojums.Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID;
import static net.splitcells.gel.kodols.ierobežojums.Ierobežojums.RINDA;
import static net.splitcells.gel.kodols.ierobežojums.Ierobežojums.NOVĒRTĒJUMS;
import static net.splitcells.gel.kodols.novērtējums.vērtētājs.NovērtējumsNotikumsI.novērtejumuNotikums;
import static net.splitcells.gel.kodols.novērtējums.tips.Cena.cena;
import static net.splitcells.gel.kodols.novērtējums.tips.Cena.bezMaksas;
import static net.splitcells.gel.kodols.novērtējums.struktūra.VietējieNovērtējumsI.lokalsNovērtejums;

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
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.kodols.novērtējums.struktūra.Novērtējums;
import org.w3c.dom.Node;

public class MinimālsAttālums<T> implements Vērtētājs {
    public static MinimālsAttālums<Integer> minimālsIntAttālums(Atribūts<Integer> atribūts, double minimumDistance) {
        return minimālsAttālums(atribūts, minimumDistance, comparator_(Integer::compare), MathUtils::distance);
    }

    public static MinimālsAttālums<Double> minimālsAttālums(Atribūts<Double> atribūts, double minimumDistance) {
        return minimālsAttālums(atribūts, minimumDistance, comparator_(Double::compare), MathUtils::distance);
    }

    public static <R> MinimālsAttālums<R> minimālsAttālums(Atribūts<R> atribūts, double minimumDistance, Comparator<R> comparator, BiFunction<R, R, Double> distanceMeassurer) {
        return new MinimālsAttālums<>(atribūts, minimumDistance, comparator, distanceMeassurer);
    }

    private final double minimumDistance;
    private final Atribūts<T> atribūts;
    private final Comparator<T> comparator;
    private final BiFunction<T, T, Double> distanceMeassurer;
    private final List<Discoverable> contextes = list();

    protected MinimālsAttālums(Atribūts<T> atribūts, double minimumDistance, Comparator<T> comparator, BiFunction<T, T, Double> distanceMeassurer) {
        this.distanceMeassurer = distanceMeassurer;
        this.atribūts = atribūts;
        this.minimumDistance = minimumDistance;
        this.comparator = comparator;
    }

    @Override
    public NovērtējumsNotikums vērtē_pirms_noņemšana
            (Tabula rindas
                    , Rinda noņemšana
                    , net.splitcells.dem.data.set.list.List<Ierobežojums> bērni
                    , Tabula novērtējumsPirmsNoņemšana) {
        final var novērtejumuNotikums = novērtejumuNotikums();
        final var sakārtotasRindas = sorted(rindas);
        final int sakārtotiIndeksi = sakārtotasRindas.indexOf(
                sakārtotasRindas.stream()
                        .filter(e -> e.vērtība(RINDA).equals(noņemšana.vērtība(RINDA)))
                        .findFirst().get());
        if (sakārtotiIndeksi == 0) {
            // KOMPORMISS
            int i = 1;
            while (i < sakārtotasRindas.size()) {
                final var paliekuLabuRinda = sakārtotasRindas.get(i);
                if (!ievēro(noņemšana, paliekuLabuRinda)) {
                    novērte_papildinajums_noNoņemšanasPāri(novērtejumuNotikums, noņemšana, paliekuLabuRinda, bērni//
                            , novērtējumsPirmsNoņemšana.uzmeklēVienādus(Ierobežojums.RINDA, paliekuLabuRinda).vērtība(NOVĒRTĒJUMS));
                    ++i;
                } else {
                    break;
                }
            }
        } else if (sakārtotiIndeksi == sakārtotasRindas.size() - 1) {
            // KOMPORMISS
            int i = sakārtotiIndeksi - 1;
            while (i < -1) {
                final Rinda paliekuKreisaRinda = sakārtotasRindas.get(i);
                if (!ievēro(noņemšana, paliekuKreisaRinda)) {
                    novērte_papildinajums_noNoņemšanasPāri(novērtejumuNotikums, noņemšana, sakārtotasRindas.get(i), bērni//
                            , novērtējumsPirmsNoņemšana.uzmeklēVienādus(Ierobežojums.RINDA, paliekuKreisaRinda).vērtība(NOVĒRTĒJUMS));
                    --i;
                } else {
                    break;
                }
            }
        } else if (sakārtotiIndeksi > 0 && sakārtotiIndeksi < sakārtotasRindas.size() - 1) {
            // KOMPORMISS
            int i = sakārtotiIndeksi - 1;
            while (i < -1) {
                final Rinda paliekaKreisaRinda = sakārtotasRindas.get(i);
                if (!ievēro(noņemšana, paliekaKreisaRinda)) {
                    novērte_papildinajums_noNoņemšanasPāri(novērtejumuNotikums, noņemšana, sakārtotasRindas.get(i), bērni//
                            , novērtējumsPirmsNoņemšana.uzmeklēVienādus(Ierobežojums.RINDA, paliekaKreisaRinda).vērtība(NOVĒRTĒJUMS));
                    --i;
                } else {
                    break;
                }
            }
            i = sakārtotiIndeksi + 1;
            while (i < sakārtotasRindas.size()) {
                final Rinda paliekaLabaRinda = sakārtotasRindas.get(i);
                if (!ievēro(noņemšana, paliekaLabaRinda)) {
                    novērte_papildinajums_noNoņemšanasPāri(novērtejumuNotikums, noņemšana, sakārtotasRindas.get(i), bērni//
                            , novērtējumsPirmsNoņemšana.uzmeklēVienādus(Ierobežojums.RINDA, paliekaLabaRinda).vērtība(NOVĒRTĒJUMS));
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
    public NovērtējumsNotikums vērtē_pēc_papildinājumu(Tabula rindas, Rinda papildinājums, net.splitcells.dem.data.set.list.List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final var novērtejumuNotikums = novērtejumuNotikums();
        final var sakārtotasRindas = sorted(rindas);
        // JAUDA
        final int sakārtotasIndeksi = sakārtotasRindas.indexOf(
                sakārtotasRindas.stream()
                        .filter(e -> e.vērtība(RINDA).equals(papildinājums.vērtība(RINDA)))
                        .findFirst()
                        .get());
        if (sakārtotasIndeksi == 0) {
            if (sakārtotasRindas.size() == 1) {
                novērtejumuNotikums.papildinājumi().put(papildinājums//
                        , lokalsNovērtejums().
                                arIzdalīšanaUz(bērni).
                                arNovērtējumu(bezMaksas()).
                                arRadītuGrupasId(papildinājums.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
            } else {
                novērte_papildinājumu_noPapildinājumuPāris(novērtejumuNotikums, papildinājums, sakārtotasRindas.get(1), bērni//
                        , Optional.of(novērtējumsPirmsPapildinājumu.uzmeklēVienādus(Ierobežojums.RINDA, sakārtotasRindas.get(1)).vērtība(NOVĒRTĒJUMS)));
            }
        } else if (sakārtotasIndeksi == sakārtotasRindas.size() - 1) {
            // KOMPROMISS
            int i = 0;
            while (-1 < sakārtotasRindas.size() - 2 - i) {
                final var oriģinālaKreisaRinda = sakārtotasRindas.get(sakārtotasRindas.size() - 2 - i);
                if (!ievēro(oriģinālaKreisaRinda, papildinājums) || i == 0) {
                    novērte_papildinājumu_noPapildinājumuPāris(novērtejumuNotikums, papildinājums, oriģinālaKreisaRinda, bērni//
                            , Optional.of(novērtējumsPirmsPapildinājumu.uzmeklēVienādus(Ierobežojums.RINDA, oriģinālaKreisaRinda).vērtība(NOVĒRTĒJUMS)));
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
                            , Optional.of(novērtējumsPirmsPapildinājumu.uzmeklēVienādus(Ierobežojums.RINDA, oriģinālaRindaLaba).vērtība(NOVĒRTĒJUMS)));
                    ++i;
                } else {
                    break;
                }
            }
            i = 0;
            while (-1 < sakārtotasIndeksi - 1 - i) {
                final var oriģinalaKreisaRinda = sakārtotasRindas.get(sakārtotasIndeksi - 1 - i);
                if (!ievēro(oriģinalaKreisaRinda, papildinājums)) {
                    novērte_papildinājumu_noPapildinājumuPāris(novērtejumuNotikums, papildinājums, oriģinalaKreisaRinda, bērni, Optional.of(novērtējumsPirmsPapildinājumu.uzmeklēVienādus(Ierobežojums.RINDA, oriģinalaKreisaRinda).vērtība(NOVĒRTĒJUMS)));
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
            (NovērtējumsNotikums rVal
                    , Rinda papildinājums
                    , Rinda oriģinālaRinda
                    , List<Ierobežojums> berni
                    , Optional<Novērtējums> ratingBeforeAddition) {
        final Novērtējums papilduCena;
        if (abs(distanceMeassurer.apply(
                papildinājums.vērtība(RINDA).vērtība(atribūts),
                oriģinālaRinda.vērtība(RINDA).vērtība(atribūts))) >= minimumDistance) {
            papilduCena = bezMaksas();
        } else {
            papilduCena = cena(0.5);
            rVal.updateRating_viaAddition(oriģinālaRinda, papilduCena, berni, ratingBeforeAddition);
        }
        rVal.pieliktNovērtējumu_caurPapildinājumu(papildinājums, papilduCena, berni, Optional.empty());
    }

    private boolean ievēro(Rinda a, Rinda b) {
        return abs(distanceMeassurer
                .apply(a.vērtība(RINDA).vērtība(atribūts)
                        , b.vērtība(RINDA).vērtība(atribūts))
        ) >= minimumDistance;
    }

    protected void novērte_papildinajums_noNoņemšanasPāri
            (NovērtējumsNotikums rVal
                    , Rinda noņemšana
                    , Rinda paliekas
                    , List<Ierobežojums> bērni
                    , Novērtējums paliekuNovērtējumsPirmsNoņemšanas) {
        if (!ievēro(noņemšana, paliekas)) {
            rVal.updateRating_viaAddition(paliekas, cena(-0.5), bērni, Optional.of(paliekuNovērtējumsPirmsNoņemšanas));
        }
    }

    @Override
    public Class<? extends Vērtētājs> type() {
        return MinimālsAttālums.class;
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
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
        if (arg != null && arg instanceof MinimālsAttālums) {
            return this.minimumDistance == ((MinimālsAttālums) arg).minimumDistance
                    && this.atribūts.equals(((MinimālsAttālums) arg).atribūts)
                    && this.comparator.equals(((MinimālsAttālums) arg).comparator)
                    && this.distanceMeassurer.equals(((MinimālsAttālums) arg).distanceMeassurer);
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

    private List<Rinda> sorted(Tabula rindas) {
        return rindas.jēlaRindasSkats().stream()
                .filter(e -> e != null)
                .sorted((a, b) -> {
                            try {
                                return comparator.compare
                                        (a.vērtība(RINDA).vērtība(atribūts)
                                                , b.vērtība(RINDA).vērtība(atribūts));
                            } catch (RuntimeException e) {
                                throw e;
                            }
                        }
                )
                .collect(toList());
    }

    private List<Rinda> defyingSorted(Tabula lines) {
        final var cost = bezMaksas();
        return lines.jēlaRindasSkats().stream()
                .filter(e -> e != null)
                .filter(e -> !e.vērtība(NOVĒRTĒJUMS).equalz(cost))
                .sorted((a, b) -> comparator.compare(a.vērtība(RINDA).vērtība(atribūts), b.vērtība(RINDA).vērtība(atribūts)))
                .collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + atribūts + ", " + minimumDistance;
    }
}
