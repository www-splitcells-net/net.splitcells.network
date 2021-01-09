package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static java.util.Objects.requireNonNull;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizacijasNotikums;
import static net.splitcells.gel.solution.optimization.StepType.PIEŠĶIRŠANA;
import static net.splitcells.gel.solution.optimization.StepType.NOŅEMŠANA;

public class UsedSupplySwitcher implements Optimization {
    public static UsedSupplySwitcher lietotaPiedāvājumuSlēdzis() {
        return new UsedSupplySwitcher(randomness(), 1);
    }

    public static UsedSupplySwitcher lietotaPiedāvājumuSlēdzis(int soluSkaits) {
        return new UsedSupplySwitcher(randomness(), soluSkaits);
    }

    public static UsedSupplySwitcher lietotaPiedāvājumuSlēdzis(Randomness nejaušiba) {
        return new UsedSupplySwitcher(nejaušiba, 1);
    }

    public static UsedSupplySwitcher lietotaPiedāvājumuSlēdzis(Randomness nejaušiba, int soluSkaits) {
        return new UsedSupplySwitcher(nejaušiba, soluSkaits);
    }

    private UsedSupplySwitcher(Randomness nejaušiba, int soluSkaits) {
        this.nejaušiba = requireNonNull(nejaušiba);
        this.soluSkaits = soluSkaits;
    }

    private final Randomness nejaušiba;
    private final int soluSkaits;

    @Override
    public List<OptimizationEvent> optimizē(SolutionView atrisinajums) {
        final List<OptimizationEvent> optimizācijas = list();
        final var apstrādatiPrasības = Sets.<LinePointer>setOfUniques();
        final var apstrādatiPiedāvājumi = Sets.<LinePointer>setOfUniques();
        rangeClosed(1, soluSkaits)
                .forEach(i -> optimizācijas.addAll
                        (optimizacijasSoli(atrisinajums, apstrādatiPrasības, apstrādatiPiedāvājumi)));
        return optimizācijas;
    }

    private List<OptimizationEvent> optimizacijasSoli
            (SolutionView atrisinājums
                    , Set<LinePointer> apstrādatiPrasības
                    , Set<LinePointer> apstrādatiPiedāvājumi) {
        if (atrisinājums.prasība_lietots().size() >= 2) {
            final int atlaseA = nejaušiba.integer(0, atrisinājums.prasība_lietots().size() - 1);
            final int atlaseB = nejaušiba.integer(0, atrisinājums.prasība_lietots().size() - 1);
            if (atlaseA == atlaseB) {
                return list();
            }
            final var lietotaPrasībaA = atrisinājums.demands().gūtRinda(atlaseA);
            final var vecaPieškiršanaA = atrisinājums.piešķiršanas_no_prasības(lietotaPrasībaA).iterator().next();
            final var lietotaPiedāvājumsA = atrisinājums.piedāvājums_no_piešķiršana(vecaPieškiršanaA);

            final var lietotaPrasībaB = atrisinājums.demands().gūtRinda(atlaseB);
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
