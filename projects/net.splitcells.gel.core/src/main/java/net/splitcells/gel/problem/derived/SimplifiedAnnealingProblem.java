package net.splitcells.gel.problem.derived;

import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.framework.MetaRating;
import net.splitcells.gel.rating.type.Optimality;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.constraint.type.Derivation.derivation;

public class SimplifiedAnnealingProblem extends DerivedSolution {

    public static Solution simplifiedAnnealingProblem(Solution solution) {
        return simplifiedAnnealingProblem(solution, i ->
                1f / (i.floatValue() + 1f));
    }

    public static Solution simplifiedAnnealingProblem(Solution solution, Function<Integer, Float> temperatureFunction) {
        return new SimplifiedAnnealingProblem(solution.allocations(), solution.constraint(), temperatureFunction);
    }

    public static Solution simplifiedAnnealingProblem(Solution solution, Function<Integer, Float> temperatureFunction
            , Randomness randomness) {
        return new SimplifiedAnnealingProblem(solution.allocations(), solution.constraint(), temperatureFunction
                , randomness);
    }

    protected SimplifiedAnnealingProblem(Allocations allocations, Constraint originalConstraint
            , Function<Integer, Float> temperatureFunction) {
        this(allocations, originalConstraint, temperatureFunction, randomness());
    }

    protected SimplifiedAnnealingProblem(Allocations allocations, Constraint originalConstraint
            , Function<Integer, Float> temperatureFunction
            , Randomness randomness) {
        super(() -> list(), allocations);
        constraint = derivation(originalConstraint,
                new Function<>() {
                    @Override
                    public MetaRating apply(MetaRating rating) {
                        if (randomness.truthValue(temperatureFunction.apply(SimplifiedAnnealingProblem.this.history().size()))) {
                            return Optimality.optimality(1).asMetaRating();
                        }
                        return rating;
                    }
                });
    }
}
