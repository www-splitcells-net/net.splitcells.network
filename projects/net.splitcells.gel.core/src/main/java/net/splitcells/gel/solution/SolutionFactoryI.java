/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution;

import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.AspectOrientedConstructorBase;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.problem.Problem;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.resource.AspectOrientedConstructorBase.aspectOrientedConstructor;
import static net.splitcells.dem.resource.ConnectingConstructorI.connectingConstructor;

public class SolutionFactoryI implements SolutionFactory {

    private final AspectOrientedConstructor<Solution> aspects = aspectOrientedConstructor();
    private final ConnectingConstructor<Solution> connectors = connectingConstructor();

    @Override
    public Solution solution(Problem problem) {
        final var solution = joinAspects(SolutionI.solution(problem));
        connectors.connect(solution);
        return solution;
    }

    @Override
    public void close() {
        // Nothing needs to be done.
    }

    @Override
    public void flush() {
        // Nothing needs to be done.
    }

    @Override
    public ConnectingConstructor<Solution> withConnector(Consumer<Solution> connector) {
        connectors.withConnector(connector);
        return this;
    }

    @Override
    public Solution connect(Solution subject) {
        return connectors.connect(subject);
    }

    @Override
    public AspectOrientedConstructor<Solution> withAspect(Function<Solution, Solution> aspect) {
        return aspects.withAspect(aspect);
    }

    @Override
    public Solution joinAspects(Solution arg) {
        return aspects.joinAspects(arg);
    }
}
