/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.problem.derived;

import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.framework.MetaRating;
import net.splitcells.gel.rating.type.Optimality;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.constraint.type.Derivation.derivation;

public class SimplifiedAnnealingProblem extends DerivedSolution {

    public static Solution simplifiedAnnealingProblem(Solution solution) {
        return simplifiedAnnealingProblem(solution, i ->
                1f / (i.floatValue() + 1f));
    }

    public static Solution simplifiedAnnealingProblem(Solution solution, Function<Integer, Float> temperatureFunction) {
        return new SimplifiedAnnealingProblem(solution.allocations(), solution.constraint(), temperatureFunction);
    }

    public static Solution simplifiedAnnealingProblem(Solution solution, Function<Integer, Float> temperatureFunction
            , Randomness randomness) {
        return new SimplifiedAnnealingProblem(solution.allocations(), solution.constraint(), temperatureFunction
                , randomness);
    }

    protected SimplifiedAnnealingProblem(Allocations allocations, Constraint originalConstraint
            , Function<Integer, Float> temperatureFunction) {
        this(allocations, originalConstraint, temperatureFunction, randomness());
    }

    protected SimplifiedAnnealingProblem(Allocations allocations, Constraint originalConstraint
            , Function<Integer, Float> temperatureFunction
            , Randomness randomness) {
        super(() -> list(), allocations);
        constraint = derivation(originalConstraint,
                new Function<>() {
                    @Override
                    public MetaRating apply(MetaRating rating) {
                        if (randomness.truthValue(temperatureFunction.apply(SimplifiedAnnealingProblem.this.history().size()))) {
                            return Optimality.optimality(1).asMetaRating();
                        }
                        return rating;
                    }
                });
    }
}
