package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.data.tabula.RindasRādītājs;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizacijasNotikums;
import static net.splitcells.gel.solution.optimization.StepType.PIEŠĶIRŠANA;
import static net.splitcells.gel.solution.optimization.StepType.NOŅEMŠANA;

public class FreeSupplySwitcher implements Optimization {
    public static FreeSupplySwitcher brīvuPiedāvājumuSlēdzis() {
        return new FreeSupplySwitcher();
    }

    public static FreeSupplySwitcher brīvuPiedāvājumuSlēdzis(Randomness nejaušiba, int soluSkaitlis) {
        return new FreeSupplySwitcher(nejaušiba, soluSkaitlis);
    }

    public static FreeSupplySwitcher brīvuPiedāvājumuSlēdzis(Randomness nejaušiba) {
        return new FreeSupplySwitcher(nejaušiba);
    }

    private final Randomness nejaušiba;
    private final int soluSkaititlis;

    public FreeSupplySwitcher(Randomness nejaušiba, int soluSkaititlis) {
        this.nejaušiba = nejaušiba;
        this.soluSkaititlis = soluSkaititlis;
    }

    public FreeSupplySwitcher(Randomness nejaušiba) {
        this.nejaušiba = nejaušiba;
        this.soluSkaititlis = 1;
    }

    public FreeSupplySwitcher() {
        nejaušiba = randomness();
        this.soluSkaititlis = 1;
    }

    @Override
    public List<OptimizationEvent> optimizē(SolutionView atrisinājums) {
        final List<OptimizationEvent> optimizācijas = list();
        final var apstrādataPrasība = Sets.<RindasRādītājs>setOfUniques();
        final var apstrādatsPiedāvājums = Sets.<RindasRādītājs>setOfUniques();
        rangeClosed(1, soluSkaititlis)
                .forEach(i -> optimizācijas.addAll
                        (optimizacijasSoli(atrisinājums, apstrādataPrasība, apstrādatsPiedāvājums)));
        return optimizācijas;
    }

    public List<OptimizationEvent> optimizacijasSoli
            (SolutionView atrisinājums
                    , Set<RindasRādītājs> apstrādatasPrasības
                    , Set<RindasRādītājs> apstrādatiPiedāvājumi) {
        if (atrisinājums.prasība_lietots().navTukšs() && atrisinājums.piedāvājums_nelietots().navTukšs()) {
            final int atlase = nejaušiba.integer(0, atrisinājums.prasības_nelietotas().izmērs() - 1);
            final var lietotaPrasība = atrisinājums.prasība_lietots().gūtRinda(atlase);
            final var lietotasPrasībasRādītājs = lietotaPrasība.uzRindaRādītājs();
            if (apstrādatasPrasības.contains(lietotasPrasībasRādītājs)) {
                return list();
            }
            final var pieškiršana = atrisinājums.piešķiršanas_no_prasības(lietotaPrasība).iterator().next();
            final var lietotsPiedāvājums = atrisinājums.piedāvājums_no_piešķiršana(pieškiršana);
            final var lietotsPiedāvājumuRādītājs = lietotsPiedāvājums.uzRindaRādītājs();
            if (apstrādatiPiedāvājumi.contains(lietotsPiedāvājumuRādītājs)) {
                return list();
            }
            apstrādatasPrasības.add(lietotasPrasībasRādītājs);
            apstrādatiPiedāvājumi.add(lietotsPiedāvājumuRādītājs);
            return
                    list(
                            optimizacijasNotikums(NOŅEMŠANA, lietotasPrasībasRādītājs, lietotsPiedāvājumuRādītājs)
                            , optimizacijasNotikums(
                                    PIEŠĶIRŠANA
                                    , atrisinājums.prasība()
                                            .gūtJēluRindas(lietotaPrasība.indekss())
                                            .uzRindaRādītājs()
                                    , atrisinājums
                                            .piedāvājums()
                                            .gūtJēluRindas
                                                    (nejaušiba.integer(0, atrisinājums.piedāvājums_nelietots().izmērs()))
                                            .uzRindaRādītājs()
                            ));
        }
        return list();
    }
}
