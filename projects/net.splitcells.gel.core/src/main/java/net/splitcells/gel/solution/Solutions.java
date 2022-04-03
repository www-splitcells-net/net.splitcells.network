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

import net.splitcells.dem.environment.resource.ResourceOptionI;
import net.splitcells.gel.problem.Problem;

import static net.splitcells.dem.Dem.environment;

public class Solutions extends ResourceOptionI<SolutionFactory> {
    public Solutions() {
        super(() -> new SolutionFactoryI());
    }

    public static Solution solution(Problem problem) {
        return environment().config().configValue(Solutions.class).solution(problem);
    }
}
