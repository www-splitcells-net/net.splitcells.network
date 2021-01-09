package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.solution.optimization.StepType;
import net.splitcells.gel.data.table.Rinda;
import net.splitcells.gel.data.table.Tabula;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;

public class TemplateInitializer implements Optimization {
    public static TemplateInitializer veidnesIzpildītājs(Tabula veidne) {
        return new TemplateInitializer(veidne);
    }

    private final Tabula veidne;

    private TemplateInitializer(Tabula veidne) {
        this.veidne = veidne;
    }

    @Override
    public List<OptimizationEvent> optimizē(SolutionView atrisinājums) {
        final List<OptimizationEvent> optimicaija = list();
        final Set<Rinda> lietotasPrasības = setOfUniques();
        final Set<Rinda> lietotasPiedāvājumi = setOfUniques();
        veidne.gūtRindas().forEach(rinda -> {
            final var prasībasVertība = atrisinājums.prasības_nelietotas()
                    .nosaukumuSkats()
                    .stream()
                    .map(nosaukums -> rinda.vērtība(nosaukums))
                    .collect(toList());
            final var piedāvājumuVertība = atrisinājums.piedāvājums_nelietots()
                    .nosaukumuSkats()
                    .stream()
                    .map(nosaukums -> rinda.vērtība(nosaukums))
                    .collect(toList());
            final var atlasītaPrasība = atrisinājums.prasības_nelietotas()
                    .uzmeklēVienādus(prasībasVertība)
                    .filter(e -> !lietotasPrasības.contains(e))
                    .findFirst();
            final var atlasītsPiedāvājums = atrisinājums.piedāvājums_nelietots()
                    .uzmeklēVienādus(piedāvājumuVertība)
                    .filter(e -> !lietotasPiedāvājumi.contains(e))
                    .findFirst();
            if (atlasītaPrasība.isPresent() && atlasītsPiedāvājums.isPresent()) {
                lietotasPrasības.ensureContains(atlasītaPrasība.get());
                lietotasPiedāvājumi.ensureContains(atlasītsPiedāvājums.get());
                optimicaija.add(OptimizationEvent.optimizacijasNotikums
                        (StepType.PIEŠĶIRŠANA
                                , atlasītaPrasība.get().uzRindaRādītājs()
                                , atlasītsPiedāvājums.get().uzRindaRādītājs()));
            }
        });
        return optimicaija;
    }
}
