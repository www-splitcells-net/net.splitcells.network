/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
