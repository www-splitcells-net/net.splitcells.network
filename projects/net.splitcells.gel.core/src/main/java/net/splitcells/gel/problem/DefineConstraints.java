/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.QueryI;
import net.splitcells.gel.solution.Solution;

import java.util.function.Function;

import static net.splitcells.gel.constraint.type.ForAlls.forAll;

public interface DefineConstraints {
    /**
     * We want to ensure, that the {@link Constraint} parent is the {@link Solution}.
     * 
     * @deprecated {@link #withConstraintBuilder(Function)}
     * @param constraint
     * @return
     */
    @Deprecated
    ProblemGenerator withConstraint(Constraint constraint);
    ProblemGenerator withConstraintBuilder(Function<Discoverable, Constraint> constraint);

    ProblemGenerator withConstraints(List<Function<Query, Query>> builders);

    ProblemGenerator withConstraint(Function<Query, Query> builder);
}
