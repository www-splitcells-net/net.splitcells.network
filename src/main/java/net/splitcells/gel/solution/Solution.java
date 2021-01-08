package net.splitcells.gel.solution;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.host.Files.createDirectory;
import static net.splitcells.dem.resource.host.Files.writeToFile;
import static net.splitcells.gel.solution.optimization.SoluTips.PIEŠĶIRŠANA;
import static net.splitcells.gel.solution.optimization.SoluTips.NOŅEMŠANA;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.rating.struktūra.Novērtējums;
import net.splitcells.gel.problem.Problēma;
import net.splitcells.gel.solution.optimization.Optimizācija;
import net.splitcells.gel.solution.optimization.OptimizācijasNotikums;

import java.util.function.Function;

public interface Solution extends Problēma, SolutionView {

    @Returns_this
    default Solution optimizē(Optimizācija optimizācija) {
        return optimizēArFunkciju(s -> optimizācija.optimizē(s));
    }

    @Returns_this
    default Solution optimizēArFunkciju(Function<Solution, List<OptimizācijasNotikums>> optimizācijaFunkcija) {
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
    default Solution optimizēVienreis(Optimizācija optimizācija) {
        return optimizeArFunkcijuVienreis(s -> optimizācija.optimizē(s));
    }

    @Returns_this
    default Solution optimizeArFunkcijuVienreis(Function<Solution, List<OptimizācijasNotikums>> optimizācija) {
        final var ieteikumi = optimizācija.apply(this);
        if (ieteikumi.isEmpty()) {
            return this;
        }
        optimizē(ieteikumi);
        return this;
    }

    @Returns_this
    default Solution optimizē(List<OptimizācijasNotikums> notikumi) {
        notikumi.forEach(this::optimizē);
        return this;
    }

    @Returns_this
    default Solution optimizē(List<OptimizācijasNotikums> notikumi, OptimizationParameters optimizationParameters) {
        notikumi.forEach(e -> optimizē(e, optimizationParameters));
        return this;
    }

    @Returns_this
    default Solution optimizē(OptimizācijasNotikums notikums) {
        return optimizē(notikums, OptimizationParameters.optimizācijasParametri());
    }

    @Returns_this
    default Solution optimizē(OptimizācijasNotikums notikums, OptimizationParameters optimizationParameters) {
        if (notikums.soluTips().equals(PIEŠĶIRŠANA)) {
            this.piešķirt(
                    prasības_nelietotas().gūtJēluRindas(notikums.prasība().interpretē().get().indekss()),
                    piedāvājums_nelietots().gūtJēluRindas(notikums.piedāvājums().interpretē().get().indekss()));
        } else if (notikums.soluTips().equals(NOŅEMŠANA)) {
            final var prasībaPriekšNoņemšanas = notikums.prasība().interpretē();
            final var piedāvājumuPriekšNoņemšanas = notikums.piedāvājums().interpretē();
            if (optimizationParameters.getDubultuNoņemšanaAtļauts()) {
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
