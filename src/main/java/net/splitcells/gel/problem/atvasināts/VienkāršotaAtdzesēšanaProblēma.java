package net.splitcells.gel.problem.atvasināts;

import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.constraint.tips.Atvasināšana;
import net.splitcells.gel.rating.struktūra.RefleksijaNovērtējums;
import net.splitcells.gel.rating.tips.Optimālums;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;

public class VienkāršotaAtdzesēšanaProblēma extends AtvasinātsSolution {

    public static Solution vienkāršotsAtzesēšanasProblēma(Solution solution) {
        return vienkāršsotsAtdzesēšanasProblēma(solution, i ->
                1f / (i.floatValue() + 1f));
    }
    public static Solution vienkāršsotsAtdzesēšanasProblēma(Solution solution, Function<Integer, Float> temperatureFunction) {
        return new VienkāršotaAtdzesēšanaProblēma(solution.piešķiršanas(), solution.ierobežojums(), temperatureFunction);
    }

    protected VienkāršotaAtdzesēšanaProblēma(Piešķiršanas piešķiršanas, Ierobežojums originalIerobežojums, Function<Integer, Float> temperatureFunction) {
        super(() -> list(), piešķiršanas);
        ierobežojums = Atvasināšana.atvasināšana(originalIerobežojums,
                new Function<>() {
                    private final Randomness randomness = randomness();
                    @Override
                    public RefleksijaNovērtējums apply(RefleksijaNovērtējums rating) {
                        if (randomness.truthValue(temperatureFunction.apply(VienkāršotaAtdzesēšanaProblēma.this.vēsture().izmērs()))) {
                            return Optimālums.optimālums(1).kāReflektētsNovērtējums();
                        }
                        return rating;
                    }
                });
    }
}
