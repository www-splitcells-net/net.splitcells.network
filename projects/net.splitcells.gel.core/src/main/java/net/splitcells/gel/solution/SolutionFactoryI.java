/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
