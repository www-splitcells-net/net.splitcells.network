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
package net.splitcells.gel.solution.optimization.space;

import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.EnumerableOnlineOptimization;

import java.util.Optional;

public class EnumerableOptimizationSpaceI implements EnumerableOptimizationSpace {

    public static EnumerableOptimizationSpace enumerableOptimizationSpace
            (Solution solution, EnumerableOnlineOptimization optimization) {
        return new EnumerableOptimizationSpaceI(solution, optimization, Optional.empty());
    }

    public static EnumerableOptimizationSpace enumerableOptimizationSpace
            (Solution solution, EnumerableOnlineOptimization optimization
                    , EnumerableOptimizationSpace parent) {
        return new EnumerableOptimizationSpaceI(solution, optimization
                , Optional.of(parent));
    }

    private final Solution solution;
    private final EnumerableOnlineOptimization optimization;
    private final int startIndex;
    private final Optional<EnumerableOptimizationSpace> parent;

    private EnumerableOptimizationSpaceI(Solution solution
            , EnumerableOnlineOptimization optimization
            , Optional<EnumerableOptimizationSpace> parent) {
        this.solution = solution;
        this.optimization = optimization;
        this.startIndex = solution.history().currentIndex();
        this.parent = parent;
    }

    @Override
    public EnumerableOptimizationSpace child(int index) {
        optimization.optimize(solution, index);
        return enumerableOptimizationSpace(solution, optimization, this);
    }

    @Override
    public int childrenCount() {
        return optimization.numberOfBranches(solution);
    }

    @Override
    public Optional<EnumerableOptimizationSpace> parent() {
        if (parent.isPresent()) {
            solution.history().resetTo(parent.get().historyIndex());
        }
        return parent;
    }

    @Override
    public SolutionView currentState() {
        return solution;
    }

    @Override
    public Solution endDiscovery() {
        return solution;
    }

    @Override
    public int historyIndex() {
        return startIndex;
    }
}
