package net.splitcells.gel.solution;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.host.Files.createDirectory;
import static net.splitcells.dem.resource.host.Files.writeToFile;
import static net.splitcells.gel.solution.OptimizationParameters.optimizationParameters;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.function.Function;

public interface Solution extends Problem, SolutionView {

    @Returns_this
    default Solution optimize(Optimization optimization) {
        return optimizeWithFunction(s -> optimization.optimize(s));
    }

    @Returns_this
    default Solution optimizeWithFunction(Function<Solution, List<OptimizationEvent>> optimizationFunction) {
        while (!isOptimal()) {
            final var recommendations = optimizationFunction.apply(this);
            if (recommendations.isEmpty()) {
                break;
            }
            optimize(recommendations);
        }
        return this;
    }

    @Returns_this
    default Solution optimizeOnce(Optimization optimization) {
        return optimizeWithFunctionOnce(s -> optimization.optimize(s));
    }

    @Returns_this
    default Solution optimizeWithFunctionOnce(Function<Solution, List<OptimizationEvent>> optimization) {
        final var recommendations = optimization.apply(this);
        if (recommendations.isEmpty()) {
            return this;
        }
        optimize(recommendations);
        return this;
    }

    @Returns_this
    default Solution optimize(List<OptimizationEvent> events) {
        events.forEach(this::optimize);
        return this;
    }

    @Returns_this
    default Solution optimize(List<OptimizationEvent> events, OptimizationParameters parameters) {
        events.forEach(e -> optimize(e, parameters));
        return this;
    }

    @Returns_this
    default Solution optimize(OptimizationEvent event) {
        return optimize(event, optimizationParameters());
    }

    @Returns_this
    default Solution optimize(OptimizationEvent event, OptimizationParameters parameters) {
        if (event.soluTips().equals(ADDITION)) {
            this.allocate(
                    demands_unused().getRawLines(event.demand().interpret().get().index()),
                    supplies_free().getRawLines(event.supply().interpret().get().index()));
        } else if (event.soluTips().equals(REMOVAL)) {
            final var demandBeforeRemoval = event.demand().interpret();
            final var supplyBeforeRemoval = event.supply().interpret();
            if (parameters.dublicateRemovalAllowed()) {
                if (demandBeforeRemoval.isEmpty() && supplyBeforeRemoval.isEmpty()) {
                    return this;
                }
            }
            remove(allocationsOf
                    (demandBeforeRemoval.get()
                            , supplyBeforeRemoval.get())
                    .iterator()
                    .next());
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    default void createAnalysis() {
        createDirectory(environment().config().configValue(ProcessPath.class));
        final var path = this.path().stream().reduce((left, right) -> left + "." + right);
        writeToFile(environment().config().configValue(ProcessPath.class).resolve(path + ".solution.constraint.toDom.xml"), constraint().toDom());
        writeToFile(environment().config().configValue(ProcessPath.class).resolve(path + ".solution.constraint.graph.xml"), constraint().graph());
    }

    default Rating rating(List<OptimizationEvent> events) {
        final var historyRootIndex = history().currentIndex();
        optimize(events);
        final var rating = constraint().rating();
        history().atiestatUz(historyRootIndex);
        return rating;
    }
}
