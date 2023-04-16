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
package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * Allocates {@link Solution#demandsFree()} and {@link Solution#suppliesFree()} in their respective order.
 */
public class OnlineLinearInitialization implements OnlineOptimization {
    private static final boolean IMPROVE_RUNTIME_GET_NEXT_LINE_BY_STREAM = true;
    private static final boolean IMPROVE_RUNTIME_GET_NEXT_LINE_BY_CACHE = true;

    public static OnlineLinearInitialization onlineLinearInitialization() {
        return new OnlineLinearInitialization();
    }

    private OnlineLinearInitialization() {

    }

    @Override
    public void optimize(Solution solution) {
        if (IMPROVE_RUNTIME_GET_NEXT_LINE_BY_CACHE) {
            // This is fast, when free demand and supply set are of equal length.
            final var freeDemands = solution.demandsFree().orderedLines();
            final var freeSupplies = solution.suppliesFree().orderedLines();
            int i = 0;
            while (freeDemands.hasElements() && freeSupplies.hasElements()) {
                solution.allocate(freeDemands.get(i++), freeSupplies.get(i++));
            }
        } else {
            while (solution.demandsFree().hasContent() && solution.suppliesFree().hasContent()) {
                if (IMPROVE_RUNTIME_GET_NEXT_LINE_BY_STREAM) {
                    // TODO Create faster version of initialization, based on 2 unordered streams.
                    solution.allocate(solution.demandsFree().orderedLinesStream().findFirst().orElseThrow()
                            , solution.suppliesFree().orderedLinesStream().findFirst().orElseThrow());
                } else {
                    solution.allocate(solution.demandsFree().orderedLine(0)
                            , solution.suppliesFree().orderedLine(0));
                }
            }
        }
    }
}
