/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.solution.Solution;

@FunctionalInterface
public interface DemandSelector {
    /**
     * 
     * @param constraint
     * @param solution
     * @return The mappings key should be from {@link Constraint#RESULTING_CONSTRAINT_GROUP}.
     */
    Map<GroupId, Set<Line>> demandGrouping(Constraint constraint, Solution solution);
}
