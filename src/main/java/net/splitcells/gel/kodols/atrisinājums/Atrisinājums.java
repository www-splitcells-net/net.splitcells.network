package net.splitcells.gel.kodols.atrisinājums;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.SoluTips.PIEŠĶIRŠANA;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.SoluTips.NOŅEMŠANA;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.gel.kodols.problēma.Problēma;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijaTiešsaistē;

import java.util.function.Function;

public interface Atrisinājums extends Problēma, AtrisinājumaSkats {

    @Returns_this
    default Atrisinājums optimizē(Optimizācija optimizācija) {
        return optimizēArFunkciju(s -> optimizācija.optimizē(s));
    }

    @Returns_this
    default Atrisinājums optimizē(OptimizācijaTiešsaistē optimizācija) {
        return optimizēArFunkciju(s -> optimizācija.optimizē(s));
    }

    @Returns_this
    default Atrisinājums optimizēArFunkciju(Function<Atrisinājums, List<OptimizācijasNotikums>> optimizācijaFunkcija) {
        while (!irOptimāls()) {
            final var ieteikumi = optimizācijaFunkcija.apply(this);
            if (ieteikumi.isEmpty()) {
                break;
            }
            optimizē(ieteikumi);
        }
        return this;
    }

    @Returns_this
    default Atrisinājums optimizēVienreis(Optimizācija optimizācija) {
        return optimizeArFunkcijuVienreis(s -> optimizācija.optimizē(s));
    }

    @Returns_this
    default Atrisinājums optimizēVienreis(OptimizācijaTiešsaistē optimizācija) {
        return optimizeArFunkcijuVienreis(s -> optimizācija.optimizē(s));
    }

    @Returns_this
    default Atrisinājums optimizeArFunkcijuVienreis(Function<Atrisinājums, List<OptimizācijasNotikums>> optimizācija) {
        final var ieteikumi = optimizācija.apply(this);
        if (ieteikumi.isEmpty()) {
            return this;
        }
        optimizē(ieteikumi);
        return this;
    }

    @Returns_this
    default Atrisinājums optimizē(List<OptimizācijasNotikums> notikumi) {
        notikumi.forEach(this::optimizē);
        return this;
    }

    @Returns_this
    default Atrisinājums optimizē(OptimizācijasNotikums notikums) {
        if (notikums.soluTips().equals(PIEŠĶIRŠANA)) {
            this.piešķirt(
                    notikums.prasība().interpretē(prasības_nelietotas()).get(),
                    notikums.piedāvājums().interpretē(piedāvājums_nelietots()).get());
        } else if (notikums.soluTips().equals(NOŅEMŠANA)) {
            noņemt(piešķiršanasNo(
                    notikums.prasība().interpretē(prasība_lietots()).get(),
                    notikums.piedāvājums().interpretē(piedāvājumi_lietoti()).get()
            ).iterator().next());
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }
}
