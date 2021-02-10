package net.splitcells.gel.solution.optimization.meta.hill.climber;

import java.util.Optional;
import java.util.function.Supplier;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.history.meta.type.CompleteRating;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

@Deprecated
public class BranchingHillClimber implements Optimization {

    public static BranchingHillClimber branchingHillClimber() {
        return new BranchingHillClimber();
    }

    private final Supplier<Boolean> planner = () -> true;

    private BranchingHillClimber() {

    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final var nextBranch = nextBranch(solution);
        return nextOperation(nextBranch.get());
    }

    private List<OptimizationEvent> nextOperation(Solution branch) {
        throw not_implemented_yet();
    }

    private Optional<Solution> nextBranch(SolutionView solution) {
        final var rootRating = solution.constraint().rating();
        var bestNeighbour = Optional.<Solution>empty();
        while (planner.get()) {
            final var currentNeighbour = solution.branch();
            final var currentRating = solution
                    .history()
                    .getLines()
                    .lastValue()
                    .get()
                    .value(History.META_DATA)
                    .value(CompleteRating.class)
                    .get()
                    .value();
            if (currentRating.betterThan(rootRating)) {
                bestNeighbour = Optional.of(currentNeighbour);
            }
        }
        return bestNeighbour;
    }
}
