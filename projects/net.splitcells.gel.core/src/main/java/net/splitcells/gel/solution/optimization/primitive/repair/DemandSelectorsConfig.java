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
package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;

public class DemandSelectorsConfig {
    public static DemandSelectorsConfig demandSelectorsConfig() {
        return new DemandSelectorsConfig();
    }

    /**
     * If set to true, selects all {@link Line}s of a given {@link GroupId},
     * if it contains at least one defying {@link Line}.
     * Otherwise, only defying {@link Line}s are considered.
     */
    boolean repairCompliants = false;
    /**
     * If set to false,
     * only {@link Constraint#lineProcessing()} will be considered,
     * in order to determine a {@link Line}'s {@link Rating}.
     * Otherwise, {@link Constraint#rating(Line)} of the {@link Solution#constraint()} will be used.
     */
    boolean useCompleteRating = false;

    private DemandSelectorsConfig() {

    }

    public DemandSelectorsConfig withRepairCompliants(boolean arg) {
        repairCompliants = arg;
        return this;
    }

    public DemandSelectorsConfig withUseCompleteRating(boolean arg) {
        useCompleteRating = arg;
        return this;
    }

    public boolean repairCompliants() {
        return repairCompliants;
    }

    public boolean useCompleteRating() {
        return useCompleteRating;
    }
}
