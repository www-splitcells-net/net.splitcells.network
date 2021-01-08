package net.splitcells.gel.atrisinājums;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.host.Files.createDirectory;
import static net.splitcells.dem.resource.host.Files.writeToFile;
import static net.splitcells.gel.atrisinājums.optimizācija.SoluTips.PIEŠĶIRŠANA;
import static net.splitcells.gel.atrisinājums.optimizācija.SoluTips.NOŅEMŠANA;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.novērtējums.struktūra.Novērtējums;
import net.splitcells.gel.problēma.Problēma;
import net.splitcells.gel.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.atrisinājums.optimizācija.OptimizācijasNotikums;

import java.util.function.Function;

public interface Atrisinājums extends Problēma, AtrisinājumaSkats {

    @Returns_this
    default Atrisinājums optimizē(Optimizācija optimizācija) {
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
    default Atrisinājums optimizē(List<OptimizācijasNotikums> notikumi, OptimizācijasParametri optimizācijasParametri) {
        notikumi.forEach(e -> optimizē(e, optimizācijasParametri));
        return this;
    }

    @Returns_this
    default Atrisinājums optimizē(OptimizācijasNotikums notikums) {
        return optimizē(notikums, OptimizācijasParametri.optimizācijasParametri());
    }

    @Returns_this
    default Atrisinājums optimizē(OptimizācijasNotikums notikums, OptimizācijasParametri optimizācijasParametri) {
        if (notikums.soluTips().equals(PIEŠĶIRŠANA)) {
            this.piešķirt(
                    prasības_nelietotas().gūtJēluRindas(notikums.prasība().interpretē().get().indekss()),
                    piedāvājums_nelietots().gūtJēluRindas(notikums.piedāvājums().interpretē().get().indekss()));
        } else if (notikums.soluTips().equals(NOŅEMŠANA)) {
            final var prasībaPriekšNoņemšanas = notikums.prasība().interpretē();
            final var piedāvājumuPriekšNoņemšanas = notikums.piedāvājums().interpretē();
            if (optimizācijasParametri.getDubultuNoņemšanaAtļauts()) {
                if (prasībaPriekšNoņemšanas.isEmpty() && piedāvājumuPriekšNoņemšanas.isEmpty()) {
                    return this;
                }
            }
            noņemt(piešķiršanasNo
                    (prasībaPriekšNoņemšanas.get()
                            , piedāvājumuPriekšNoņemšanas.get())
                    .iterator()
                    .next());
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    default void veidoAnalīzu() {
        createDirectory(environment().config().configValue(ProcessPath.class));
        final var path = this.path().stream().reduce((kreisi, labi) -> kreisi + "." + labi);
        writeToFile(environment().config().configValue(ProcessPath.class).resolve(path + ".atrisinājums.ierobežojums.toDom.xml"), ierobežojums().toDom());
        writeToFile(environment().config().configValue(ProcessPath.class).resolve(path + ".atrisinājums.ierobežojums.grafiks.xml"), ierobežojums().grafiks());
    }

    default Novērtējums novērtējums(List<OptimizācijasNotikums> notikumi) {
        final var sanknesVēsturesIndekss = vēsture().momentansIndekss();
        optimizē(notikumi);
        final var novērtējums = ierobežojums().novērtējums();
        vēsture().atiestatUz(sanknesVēsturesIndekss);
        return novērtējums;
    }
}
