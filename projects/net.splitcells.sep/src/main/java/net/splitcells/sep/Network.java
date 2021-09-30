package net.splitcells.sep;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.Optimization;

import java.util.function.BiPredicate;
import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;

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
    public Network withOptimization(String argumentKey, Optimization execution) {
        return withExecution(argumentKey, s -> s.optimize(execution.optimize(s)));
    }
}
