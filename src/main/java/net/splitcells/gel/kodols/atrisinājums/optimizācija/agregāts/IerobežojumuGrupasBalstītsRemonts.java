package net.splitcells.gel.kodols.atrisinājums.optimizācija.agregāts;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.tips.PriekšVisiem;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators.PriekšVisiemAtribūtsVērtībam;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators.PriekšVisiemVērtībasKombinācija;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.*;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums.optimizacijasNotikums;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.SoluTips.NOŅEMŠANA;
import static net.splitcells.gel.kodols.ierobežojums.Ierobežojums.*;

public class IerobežojumuGrupasBalstītsRemonts implements Optimizācija {
    public static IerobežojumuGrupasBalstītsRemonts ierobežojumGrupaBalstītsRemonts() {
        return new IerobežojumuGrupasBalstītsRemonts();
    }

    protected IerobežojumuGrupasBalstītsRemonts() {
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums) {
        izbrīvoNeievērotajuGrupuNoMazākoIerobežojumuGrupu(atrisinājums);
        return pārdale(atrisinājums);
    }

    protected List<OptimizācijasNotikums> pārdale(AtrisinājumaSkats atrisinājums) {
        final var brīvasGrupas = atrisinājums.brīvasGrupas();

        final var atlasītasBrīvasGrupas = brīvasGrupas.stream()
                .collect(toList());
        {
            final var grupasAtribūti = atlasītasBrīvasGrupas.stream()
                    .map(selected -> {
                        final var forAllConstraint = (PriekšVisiem) selected;
                        if (forAllConstraint.grupešana() instanceof PriekšVisiemAtribūtsVērtībam) {
                            return list(((PriekšVisiemAtribūtsVērtībam) forAllConstraint.grupešana()).atribūti());
                        } else if (forAllConstraint.grupešana() instanceof PriekšVisiemVērtībasKombinācija) {
                            return ((PriekšVisiemVērtībasKombinācija) forAllConstraint.grupešana()).attributes();
                        } else {
                            throw new IllegalArgumentException(forAllConstraint.toString());
                        }
                    })
                    .flatMap(selected -> selected.stream())
                    .distinct()
                    .collect(toList());
        }
        {
            final var priekšVisiemIerobēzojums = (PriekšVisiem) atlasītasBrīvasGrupas.get(0);
            final Map<GrupaId, Set<Rinda>> atlasītasBrīvasRindas = Maps.map();
            atrisinājums.prasības_nelietotas().gūtRindas().stream()
                    // KOMPROMISS Noņem lietošanu no null.
                    .map(brīvasPrasības -> pair
                            (brīvasPrasības
                                    , priekšVisiemIerobēzojums.grupešana()
                                            .vērtē_pēc_padildinājumu(null, brīvasPrasības, list(), null)
                                            .papildinājumi()
                                            .get(brīvasPrasības)
                                            .radītsIerobežojumuGrupaId()))
                    .forEach(freeDemandGrouping
                            -> atlasītasBrīvasRindas
                            .computeIfAbsent(freeDemandGrouping.getValue(), (g) -> setOfUniques())
                            .add(freeDemandGrouping.getKey())
                    );
        }
        throw not_implemented_yet();
    }

    public List<OptimizācijasNotikums> izbrīvoNeievērotajuGrupuNoMazākoIerobežojumuGrupu(AtrisinājumaSkats atrisinājums) {
        final var pieškiršanasGrupas = piešķiršanasGruppas(atrisinājums.ierobežojums())
                .stream()
                .filter(e -> !e.isEmpty())
                .collect(toList());
        // DARĪT Ir tas sakārtots?
        return pieškiršanasGrupas
                .reverse()
                .stream()
                .map(pēdejaPieškiršanasGrupa -> izbrīvoNeievērotajuGrupuNoIerobežojumuGrupu(atrisinājums, pēdejaPieškiršanasGrupa))
                .filter(grupasIzbrīvošanu -> !grupasIzbrīvošanu.isEmpty())
                .findFirst()
                .orElseGet(() -> list());
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
