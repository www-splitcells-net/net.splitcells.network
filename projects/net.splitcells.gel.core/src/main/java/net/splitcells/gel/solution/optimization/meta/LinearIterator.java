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
package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.Arrays;

import static net.splitcells.dem.data.set.list.Lists.list;
import static org.assertj.core.api.Assertions.assertThat;

public class LinearIterator implements OfflineOptimization {

    private final List<OfflineOptimization> optimizations;
    private int counter = -1;

    public static LinearIterator linearIterator(OfflineOptimization... optimization) {
        return new LinearIterator(list(optimization));
    }

    public static LinearIterator linearIterator(List<OfflineOptimization> optimization) {
        return new LinearIterator(optimization);
    }

    private LinearIterator(List<OfflineOptimization> optimizations) {
        this.optimizations = optimizations;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        List<OptimizationEvent> optimization = list();
        int tryCounter = 0;
        while (optimization.isEmpty() && tryCounter < this.optimizations.size()) {
            optimization = selectNextOptimization().optimize(solution);
            ++tryCounter;
        }
        return optimization;
    }

    private OfflineOptimization selectNextOptimization() {
        counter += 1;
        if (counter >= optimizations.size()) {
            counter = 0;
        }
        return optimizations.get(counter);
    }
}
