/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
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

    }

    @Override
    public void flush() {

    }

    @Override
    public ConnectingConstructor<Solution> withConnector(Consumer<Solution> connector) {
        connectors.withConnector(connector);
        return this;
    }

    @Override
    public void connect(Solution subject) {
        connectors.connect(subject);
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
