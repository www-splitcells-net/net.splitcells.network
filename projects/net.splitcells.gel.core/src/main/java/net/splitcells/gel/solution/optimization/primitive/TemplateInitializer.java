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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.solution.optimization.StepType;
import net.splitcells.gel.data.view.Line;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;

public class TemplateInitializer implements OfflineOptimization {
    public static TemplateInitializer templateInitializer(View template) {
        return new TemplateInitializer(template);
    }

    private final View template;

    private TemplateInitializer(View template) {
        this.template = template;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final List<OptimizationEvent> optimization = list();
        final Set<Line> usedDemands = setOfUniques();
        final Set<Line> usedSupplies = setOfUniques();
        template.unorderedLines().forEach(line -> {
            final var demandValues = solution.demandsFree()
                    .headerView()
                    .stream()
                    .map(attribute -> line.value(attribute))
                    .collect(toList());
            final var supplyValues = solution.suppliesFree()
                    .headerView()
                    .stream()
                    .map(attribute -> line.value(attribute))
                    .collect(toList());
            final var selectedDemand = solution.demandsFree()
                    .lookupEquals(demandValues)
                    .filter(e -> !usedDemands.contains(e))
                    .findFirst();
            final var selectedSupply = solution.suppliesFree()
                    .lookupEquals(supplyValues)
                    .filter(e -> !usedSupplies.contains(e))
                    .findFirst();
            if (selectedDemand.isPresent() && selectedSupply.isPresent()) {
                usedDemands.ensureContains(selectedDemand.get());
                usedSupplies.ensureContains(selectedSupply.get());
                optimization.add(OptimizationEvent.optimizationEvent
                        (StepType.ADDITION
                                , selectedDemand.get().toLinePointer()
                                , selectedSupply.get().toLinePointer()));
            }
        });
        return optimization;
    }
}
