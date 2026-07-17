/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution;

import net.splitcells.dem.environment.resource.ResourceOptionImpl;
import net.splitcells.gel.problem.Problem;

import static net.splitcells.dem.Dem.environment;

public class Solutions extends ResourceOptionImpl<SolutionFactory> {
    public Solutions() {
        super(() -> new SolutionFactoryI());
    }

    public static Solution solution(Problem problem) {
        return environment().config().configValue(Solutions.class).solution(problem);
    }
}
