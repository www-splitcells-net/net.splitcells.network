package net.splitcells.gel.kodols.atrisinājums.optimizācija.primitīvs;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.kodols.dati.tabula.RindasRādītājs;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;

import static java.util.Objects.requireNonNull;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums.optimizacijasNotikums;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.SoluTips.PIEŠĶIRŠANA;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.SoluTips.NOŅEMŠANA;

public class LietotuPiedāvājumuSlēdzis implements Optimizācija {
    public static LietotuPiedāvājumuSlēdzis lietotaPiedāvājumuSlēdzis() {
        return new LietotuPiedāvājumuSlēdzis(randomness(), 1);
    }

    public static LietotuPiedāvājumuSlēdzis lietotaPiedāvājumuSlēdzis(int soluSkaits) {
        return new LietotuPiedāvājumuSlēdzis(randomness(), soluSkaits);
    }

    public static LietotuPiedāvājumuSlēdzis lietotaPiedāvājumuSlēdzis(Randomness nejaušiba) {
        return new LietotuPiedāvājumuSlēdzis(nejaušiba, 1);
    }

    public static LietotuPiedāvājumuSlēdzis lietotaPiedāvājumuSlēdzis(Randomness nejaušiba, int soluSkaits) {
        return new LietotuPiedāvājumuSlēdzis(nejaušiba, soluSkaits);
    }

    private LietotuPiedāvājumuSlēdzis(Randomness nejaušiba, int soluSkaits) {
        this.nejaušiba = requireNonNull(nejaušiba);
        this.soluSkaits = soluSkaits;
    }

    private final Randomness nejaušiba;
    private final int soluSkaits;

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinajums) {
        final List<OptimizācijasNotikums> optimizācijas = list();
        final var apstrādatiPrasības = Sets.<RindasRādītājs>setOfUniques();
        final var apstrādatiPiedāvājumi = Sets.<RindasRādītājs>setOfUniques();
        rangeClosed(1, soluSkaits)
                .forEach(i -> optimizācijas.addAll
                        (optimizacijasSoli(atrisinajums, apstrādatiPrasības, apstrādatiPiedāvājumi)));
        return optimizācijas;
    }

    private List<OptimizācijasNotikums> optimizacijasSoli
            (AtrisinājumaSkats atrisinājums
                    , Set<RindasRādītājs> apstrādatiPrasības
                    , Set<RindasRādītājs> apstrādatiPiedāvājumi) {
        if (atrisinājums.prasība_lietots().izmērs() >= 2) {
            final int atlaseA = nejaušiba.integer(0, atrisinājums.prasība_lietots().izmērs() - 1);
            final int atlaseB = nejaušiba.integer(0, atrisinājums.prasība_lietots().izmērs() - 1);
            if (atlaseA == atlaseB) {
                return list();
            }
            final var lietotaPrasībaA = atrisinājums.prasība().gūtRinda(atlaseA);
            final var vecaPieškiršanaA = atrisinājums.piešķiršanas_no_prasības(lietotaPrasībaA).iterator().next();
            final var lietotaPiedāvājumsA = atrisinājums.piedāvājums_no_piešķiršana(vecaPieškiršanaA);

            final var lietotaPrasībaB = atrisinājums.prasība().gūtRinda(atlaseB);
            final var vecaPieſkirſanaB = atrisinājums.piešķiršanas_no_prasības(lietotaPrasībaB).iterator().next();
            final var lietotsPiedāvājumsB = atrisinājums.piedāvājums_no_piešķiršana(vecaPieſkirſanaB);

            final var lietotasParsībasARāditājs = lietotaPrasībaA.uzRindaRādītājs();
            final var lietotasPrasībasBRāditājs = lietotaPrasībaB.uzRindaRādītājs();
            if (apstrādatiPrasības.containsAny(lietotasParsībasARāditājs, lietotasPrasībasBRāditājs)) {
                return list();
            }
            final var lietotasPiedāvājumuARāditājs = lietotaPiedāvājumsA.uzRindaRādītājs();
            final var lietotasPiedāvājumuBRāditājs = lietotsPiedāvājumsB.uzRindaRādītājs();
            if (apstrādatiPiedāvājumi.containsAny(lietotasPiedāvājumuARāditājs, lietotasPiedāvājumuBRāditājs)) {
                return list();
            }
            apstrādatiPrasības.addAll(lietotasParsībasARāditājs, lietotasPrasībasBRāditājs);
            apstrādatiPiedāvājumi.addAll(lietotasPiedāvājumuARāditājs, lietotasPiedāvājumuBRāditājs);
            return
                    list(optimizacijasNotikums(NOŅEMŠANA, lietotasParsībasARāditājs, lietotasPiedāvājumuARāditājs)
                            , optimizacijasNotikums(NOŅEMŠANA, lietotasPrasībasBRāditājs, lietotasPiedāvājumuBRāditājs)
                            , optimizacijasNotikums(PIEŠĶIRŠANA, lietotasParsībasARāditājs, lietotasPiedāvājumuBRāditājs)
                            , optimizacijasNotikums(PIEŠĶIRŠANA, lietotasPrasībasBRāditājs, lietotasPiedāvājumuARāditājs)
                    );
        }
        return list();
    }
}
