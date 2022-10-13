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

import net.splitcells.dem.resource.AspectOrientedConstructorBase;
import net.splitcells.gel.problem.Problem;

public class SolutionFactoryI extends AspectOrientedConstructorBase<Solution> implements SolutionFactory {
    @Override
    public Solution solution(Problem problem) {
        return joinAspects(SolutionI.solution(problem));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
