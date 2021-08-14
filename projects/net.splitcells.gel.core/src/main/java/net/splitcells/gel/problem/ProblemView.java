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
package net.splitcells.gel.problem;

import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.allocation.AllocationsLiveView;
import net.splitcells.gel.problem.derived.DerivedSolution;
import net.splitcells.gel.rating.framework.MetaRating;

import java.util.function.Function;

public interface ProblemView extends AllocationsLiveView {

    Constraint constraint();

    Allocations allocations();

    DerivedSolution derived(Function<MetaRating, MetaRating> derivation);
}