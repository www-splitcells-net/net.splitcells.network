package net.splitcells.gel.kodols.atrisinājums.optimizācija.agregāts;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.dem.data.set.map.Pair;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.dem.utils.random.RandomnessSource;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.tips.PriekšVisiem;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators.PriekšVisiemAtribūtsVērtībam;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators.PriekšVisiemVērtībasKombinācija;

import java.util.Optional;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.Maps.toMap;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.*;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums.optimizacijasNotikums;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.SoluTips.NOŅEMŠANA;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.SoluTips.PIEŠĶIRŠANA;
import static net.splitcells.gel.kodols.ierobežojums.Ierobežojums.*;

public class IerobežojumuGrupasBalstītsRemonts implements Optimizācija {
    public static IerobežojumuGrupasBalstītsRemonts ierobežojumGrupaBalstītsRemonts() {
        return new IerobežojumuGrupasBalstītsRemonts();
    }

    private final Randomness randomness = RandomnessSource.randomness();

    protected IerobežojumuGrupasBalstītsRemonts() {
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums) {
        final var neievērotajuGrupuNoIerobežojumuGrupu = neievērotajuGrupuNoIerobežojumuGrupu(atrisinājums);
        final var prasībasGrupēšana = prāsībasGrupēšana(neievērotajuGrupuNoIerobežojumuGrupu, atrisinājums);
        final var optimizāija =
                neievērotajuGrupuNoIerobežojumuGrupu.map(e -> izbrīvoNeievērotajuGrupuNoIerobežojumuGrupu(atrisinājums, e))
                        .orElseGet(() -> list());
        optimizāija.withAppended(pārdale(atrisinājums, prasībasGrupēšana));
        return optimizāija;
    }

    protected List<OptimizācijasNotikums> pārdale(AtrisinājumaSkats atrisinājums, Map<GrupaId, Set<Rinda>> brīvasPrasībasGrupas) {
        List<OptimizācijasNotikums> pārdale = list();
        final var nelietotiPiedāvājumi = atrisinājums.piedāvājums_nelietots().gūtRindas();
        brīvasPrasībasGrupas.entrySet().forEach(grupa -> {
            grupa.getValue().forEach(prāsiba -> {
                if (nelietotiPiedāvājumi.isEmpty()) {
                    return;
                }
                pārdale.add(
                        optimizacijasNotikums(
                                PIEŠĶIRŠANA
                                , prāsiba.uzRindaRādītājs()
                                , nelietotiPiedāvājumi.remove(randomness.integer(0, nelietotiPiedāvājumi.size() - 1)).uzRindaRādītājs()
                        ));
            });
        });
        return pārdale;
    }

    protected Map<GrupaId, Set<Rinda>> prāsībasGrupēšana(Optional<List<Ierobežojums>> ierobežojumuGrupēšāna, AtrisinājumaSkats atrisinājums) {
        return ierobežojumuGrupēšāna
                .map(grupēšanasTaka -> grupēšanasTaka
                        .lastValue()
                        .map(grupēšana -> {
                            final Map<GrupaId, Set<Rinda>> prāsībasGrupēšana = map();
                            grupēšana
                                    .rindasAbstrāde()
                                    .gūtRindas()
                                    .stream()
                                    .map(abstrāde -> pair(abstrāde.vērtība(RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID), abstrāde.vērtība(RINDA)))
                                    .forEach(abstrāde -> {
                                        final Set<Rinda> grupa;
                                        if (!prāsībasGrupēšana.containsKey(abstrāde.getKey())) {
                                            grupa = Sets.setOfUniques();
                                            prāsībasGrupēšana.put(abstrāde.getKey(), grupa);
                                        } else {
                                            grupa = prāsībasGrupēšana.get(abstrāde.getKey());
                                        }
                                        grupa.with(abstrāde.getValue());
                                    });
                            return prāsībasGrupēšana;
                        })
                        .orElseGet(() -> map()))
                .orElseGet(() -> map());
    }

    protected Optional<List<Ierobežojums>> neievērotajuGrupuNoIerobežojumuGrupu(AtrisinājumaSkats atrisinājums) {
        return piešķiršanasGruppas(atrisinājums.ierobežojums())
                .stream()
                .filter(e -> !e.isEmpty())
                .findFirst();
    }

    protected Optional<List<OptimizācijasNotikums>> izbrīvoNeievērotajuGrupuNoIerobežojumuGrupam
            (Optional<List<Ierobežojums>> neievērotajuGrupuNoIerobežojumuGrupu, AtrisinājumaSkats atrisinājums) {
        return neievērotajuGrupuNoIerobežojumuGrupu
                .stream()
                .map(pēdejaPieškiršanasGrupa
                        -> izbrīvoNeievērotajuGrupuNoIerobežojumuGrupu(atrisinājums, pēdejaPieškiršanasGrupa))
                .filter(grupasIzbrīvošanu -> !grupasIzbrīvošanu.isEmpty())
                .findFirst();
    }

    protected List<OptimizācijasNotikums> izbrīvoNeievērotajuGrupuNoIerobežojumuGrupu(AtrisinājumaSkats atrisinājums, List<Ierobežojums> ierobežojumuTaka) {
        return ierobežojumuTaka.lastValue()
                .map(ierobežojums ->
                        ierobežojums
                                .rindasAbstrāde()
                                .kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .vertības()
                                .stream()
                                // DARĪT Šis nestrāda ar IerobežojumuGrupasBalstītsRemontsTests.
                                .filter(grupa -> !ierobežojums.neievērotaji(grupa).isEmpty())
                                .map(grupa -> ierobežojums.rindasAbstrāde().kolonnaSkats(RINDA).vertības())
                                .collect(toSetOfUniques()))
                //.reduce((l, r) -> merge(l, r))
                .stream()
                .flatMap(straumeNoRindasSarakstiem -> straumeNoRindasSarakstiem.stream())
                .flatMap(rindas -> rindas.stream())
                .map(rinda -> optimizacijasNotikums
                        (NOŅEMŠANA
                                , atrisinājums.prasība_no_piešķiršana(rinda).uzRindaRādītājs()
                                , atrisinājums.piedāvājums_no_piešķiršana(rinda).uzRindaRādītājs()))
                .collect(toList());
    }
}
