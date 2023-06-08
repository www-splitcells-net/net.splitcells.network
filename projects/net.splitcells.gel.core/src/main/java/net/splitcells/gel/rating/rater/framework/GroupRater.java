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

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;

import java.util.Optional;

import static net.splitcells.dem.resource.communication.log.Domsole.domsole;

/**
 * This is a helper interface, in order to create a rater based on one method.
 */
@FunctionalInterface
public interface GroupRater {

    static GroupRater describedGroupRater(GroupRater arg, String description) {
        return new GroupRater() {
            @Override
            public Rating lineRating(Table lines, Optional<Line> addition, Optional<Line> removal) {
                return arg.lineRating(lines, addition, removal);
            }

            @Override
            public String toString() {
                return description;
            }
        };
    }

    /**
     * The {@link Rating} of an Event, has to be applied to all {@link Line}s of a group.
     *
     * @param lines    lines
     * @param addition addition
     * @param removal  removal
     * @return return
     */
    Rating lineRating(Table lines, Optional<Line> addition, Optional<Line> removal);

    /**
     * By default, {@link Proposal}s are not processed.
     *
     * @param proposal Already present proposal.
     * @return Adjustment to the proposal, so that the given proposal is compliant with this {@link Rater}.
     */
    default Proposal propose(Proposal proposal) {
        if (StaticFlags.WARNING) {
            domsole().appendUnimplementedWarning(getClass());
        }
        return proposal;
    }
}
