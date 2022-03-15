package net.splitcells.sep;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OfflineOptimization;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;

/**
 * <p>TODO Ensure that all solutions have a name,
 * because otherwise it can get hard pretty easily to distinguish one {@link net.splitcells.gel.problem.Problem}
 * from another.</p>
 * <p>TODO Make this based on {@link net.splitcells.dem.execution.EffectSystem}.
 * Note that {@link net.splitcells.dem.execution.EffectSystem} is not in a good
 * state and needs to be fixed first.</p>
 */
public class Network {
    public static Network network() {
        return new Network();
    }

    private final Map<String, Solution> solutions = map();

    private Network() {

    }

    @ReturnsThis
    public Network withNode(String key, Solution solution) {
        if (solutions.containsKey(key)) {
            throw new IllegalArgumentException(key);
        }
        solutions.put(key, solution);
        return this;
    }

    @ReturnsThis
    public Network withNode(String key, Function<Solution, Solution> constructor, String dependencyKey) {
        if (solutions.containsKey(key)) {
            throw new IllegalArgumentException(key);
        }
        withNode(key, constructor.apply(solutions.get(dependencyKey)));
        return this;
    }

    @ReturnsThis
    public Network withExecution(String argumentKey, Function<Solution, Solution> execution) {
        solutions.put(argumentKey, execution.apply(solutions.get(argumentKey)));
        return this;
    }

    @ReturnsThis
    public <T> T extract(String argumentKey, Function<Solution, T> execution) {
        return execution.apply(solutions.get(argumentKey));
    }

    @ReturnsThis
    public void process(String argumentKey, Consumer<Solution> execution) {
        execution.accept(solutions.get(argumentKey));
    }

    @ReturnsThis
    public Network withOptimization(String argumentKey, OfflineOptimization execution) {
        return withExecution(argumentKey, s -> s.optimize(execution));
    }

    @ReturnsThis
    public Network withOptimization(String argumentKey, OfflineOptimization optimizationFunction, BiPredicate<Solution, Integer> continuationCondition) {
        final var solution = solutions.get(argumentKey);
        int i = 0;
        while (continuationCondition.test(solution, i)) {
            final var recommendations = optimizationFunction.optimize(solution);
            if (recommendations.isEmpty()) {
                break;
            }
            ++i;
            solution.optimize(recommendations);
        }
        return this;
    }
}
