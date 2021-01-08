package net.splitcells.gel.solution.optimization.primitīvs;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimizācija;
import net.splitcells.gel.solution.optimization.OptimizācijasNotikums;
import net.splitcells.gel.solution.optimization.SoluTips;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;

public class VeidnesIzpildītājs implements Optimizācija {
    public static VeidnesIzpildītājs veidnesIzpildītājs(Tabula veidne) {
        return new VeidnesIzpildītājs(veidne);
    }

    private final Tabula veidne;

    private VeidnesIzpildītājs(Tabula veidne) {
        this.veidne = veidne;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(SolutionView atrisinājums) {
        final List<OptimizācijasNotikums> optimicaija = list();
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
                optimicaija.add(OptimizācijasNotikums.optimizacijasNotikums
                        (SoluTips.PIEŠĶIRŠANA
                                , atlasītaPrasība.get().uzRindaRādītājs()
                                , atlasītsPiedāvājums.get().uzRindaRādītājs()));
            }
        });
        return optimicaija;
    }
}
