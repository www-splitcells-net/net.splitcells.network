/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.problem;

import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.assignment.AssignmentsLiveView;
import net.splitcells.gel.problem.derived.DerivedSolution;
import net.splitcells.gel.rating.framework.Rating;

import java.util.function.Function;

public interface ProblemView extends AssignmentsLiveView {

    Constraint constraint();

    Assignments allocations();

    DerivedSolution derived(Function<Rating, Rating> derivation);
}