package net.splitcells.sep.test.functionality;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.gel.solution.Solution;

import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;

public class Network {
    public static Network network() {
        return new Network();
    }

    private final Map<String, Solution> solutions = map();

    private Network() {

    }

    public Network withNode(String key, Solution solution) {
        if (solutions.containsKey(key)) {
            throw new IllegalArgumentException(key);
        }
        solutions.put(key, solution);
        return this;
    }

    public Network withNode(String key, Function<Solution, Solution> constructor, String dependencyKey) {
        if (solutions.containsKey(key)) {
            throw new IllegalArgumentException(key);
        }
        withNode(key, constructor.apply(solutions.get(dependencyKey)));
        return this;
    }
}
