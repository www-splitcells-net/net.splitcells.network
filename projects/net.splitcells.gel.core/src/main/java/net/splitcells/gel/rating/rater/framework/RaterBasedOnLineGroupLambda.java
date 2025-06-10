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
package net.splitcells.gel.rating.rater.framework;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.proposal.Proposal;

import java.util.Optional;

import static net.splitcells.dem.resource.communication.log.Logs.logs;

@FunctionalInterface
public interface RaterBasedOnLineGroupLambda {
    RatingEvent rating(View lines, Optional<Line> addition, Optional<Line> removal, List<Constraint> children);

    default Proposal propose(Proposal proposal) {
        if (StaticFlags.WARNING) {
            logs().warnUnimplementedPart(getClass());
        }
        return proposal;
    }
}
