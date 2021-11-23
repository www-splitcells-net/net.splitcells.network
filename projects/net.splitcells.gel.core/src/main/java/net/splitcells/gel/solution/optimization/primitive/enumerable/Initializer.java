package net.splitcells.gel.solution.optimization.primitive.enumerable;

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.EnumerableOnlineOptimization;

import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.MathUtils.modulus;

public class Initializer implements EnumerableOnlineOptimization {

    public static Initializer initializer() {
        return new Initializer();
    }

    private Initializer() {

    }

    @Override
    public int numberOfBranches(Solution solution) {
        return upperLimit(solution) + 1;
    }

    private int upperLimit(Solution solution) {
        return (solution.demandsFree().size()
                * solution.suppliesFree().size()
        ) - 1;
    }

    @Override
    public void optimize(Solution solution, Integer parameter) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            if (parameter < 0) {
                throw executionException("Parameter must not be negative: " + parameter);
            }
            final var upperLimit = upperLimit(solution);
            if (upperLimit <= parameter) {
                throw executionException("Invalid parameter: upper limit = "
                        + upperLimit
                        + ", parameter = "
                        + parameter);
            }
        }
        final var demandIndex = MathUtils.floorToInt((double) parameter / solution.suppliesFree().size());
        final var demand = solution.demandsFree().getLines(demandIndex);
        final var supplyIndex = parameter - (demandIndex * solution.suppliesFree().size());
        final var supply = solution.suppliesFree().getLines(supplyIndex);
        solution.allocate(demand, supply);
    }
}
