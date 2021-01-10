package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.solution.optimization.StepType;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;

public class TemplateInitializer implements Optimization {
    public static TemplateInitializer veidnesIzpildītājs(Table veidne) {
        return new TemplateInitializer(veidne);
    }

    private final Table veidne;

    private TemplateInitializer(Table veidne) {
        this.veidne = veidne;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView atrisinājums) {
        final List<OptimizationEvent> optimicaija = list();
        final Set<Line> lietotasPrasības = setOfUniques();
        final Set<Line> lietotasPiedāvājumi = setOfUniques();
        veidne.getLines().forEach(rinda -> {
            final var prasībasVertība = atrisinājums.demands_unused()
                    .headerView()
                    .stream()
                    .map(nosaukums -> rinda.vērtība(nosaukums))
                    .collect(toList());
            final var piedāvājumuVertība = atrisinājums.supplies_unused()
                    .headerView()
                    .stream()
                    .map(nosaukums -> rinda.vērtība(nosaukums))
                    .collect(toList());
            final var atlasītaPrasība = atrisinājums.demands_unused()
                    .uzmeklēVienādus(prasībasVertība)
                    .filter(e -> !lietotasPrasības.contains(e))
                    .findFirst();
            final var atlasītsPiedāvājums = atrisinājums.supplies_unused()
                    .uzmeklēVienādus(piedāvājumuVertība)
                    .filter(e -> !lietotasPiedāvājumi.contains(e))
                    .findFirst();
            if (atlasītaPrasība.isPresent() && atlasītsPiedāvājums.isPresent()) {
                lietotasPrasības.ensureContains(atlasītaPrasība.get());
                lietotasPiedāvājumi.ensureContains(atlasītsPiedāvājums.get());
                optimicaija.add(OptimizationEvent.optimizacijasNotikums
                        (StepType.ADDITION
                                , atlasītaPrasība.get().uzRindaRādītājs()
                                , atlasītsPiedāvājums.get().uzRindaRādītājs()));
            }
        });
        return optimicaija;
    }
}
