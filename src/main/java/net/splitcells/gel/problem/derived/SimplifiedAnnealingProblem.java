package net.splitcells.gel.problem.derived;

import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.type.Derivation;
import net.splitcells.gel.rating.structure.MetaRating;
import net.splitcells.gel.rating.type.Optimality;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;

public class SimplifiedAnnealingProblem extends DerivedSolution {

    public static Solution vienkāršotsAtzesēšanasProblēma(Solution solution) {
        return vienkāršsotsAtdzesēšanasProblēma(solution, i ->
                1f / (i.floatValue() + 1f));
    }
    public static Solution vienkāršsotsAtdzesēšanasProblēma(Solution solution, Function<Integer, Float> temperatureFunction) {
        return new SimplifiedAnnealingProblem(solution.allocations(), solution.constraint(), temperatureFunction);
    }

    protected SimplifiedAnnealingProblem(Allocations piešķiršanas, Constraint originalIerobežojums, Function<Integer, Float> temperatureFunction) {
        super(() -> list(), piešķiršanas);
        ierobežojums = Derivation.atvasināšana(originalIerobežojums,
                new Function<>() {
                    private final Randomness randomness = randomness();
                    @Override
                    public MetaRating apply(MetaRating rating) {
                        if (randomness.truthValue(temperatureFunction.apply(SimplifiedAnnealingProblem.this.history().size()))) {
                            return Optimality.optimālums(1).kāReflektētsNovērtējums();
                        }
                        return rating;
                    }
                });
    }
}
