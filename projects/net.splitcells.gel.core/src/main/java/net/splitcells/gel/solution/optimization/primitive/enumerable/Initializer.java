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
package net.splitcells.gel.solution.optimization.primitive.enumerable;

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.EnumerableOnlineOptimization;

import static net.splitcells.dem.utils.ExecutionException.executionException;

/**
 * This
 */
public class Initializer implements EnumerableOnlineOptimization {

    public static Initializer initializer() {
        return new Initializer();
    }

    private Initializer() {

    }

    @Override
    public int numberOfBranches(Solution solution) {
        return upperLimit(solution) + 1;
    }

    private int upperLimit(Solution solution) {
        return (solution.demandsFree().size()
                * solution.suppliesFree().size()
        ) - 1;
    }

    @Override
    public void optimize(Solution solution, Integer parameter) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            if (parameter < 0) {
                throw executionException("Parameter must not be negative: " + parameter);
            }
            final var upperLimit = upperLimit(solution);
            if (upperLimit < parameter) {
                throw executionException("Invalid parameter: upper limit = "
                        + upperLimit
                        + ", parameter = "
                        + parameter);
            }
        }
        final var demandIndex = MathUtils.floorToInt((double) parameter / solution.suppliesFree().size());
        final var demand = solution.demandsFree().orderedLine(demandIndex);
        final var supplyIndex = parameter - (demandIndex * solution.suppliesFree().size());
        final var supply = solution.suppliesFree().orderedLine(supplyIndex);
        solution.allocate(demand, supply);
    }
}
