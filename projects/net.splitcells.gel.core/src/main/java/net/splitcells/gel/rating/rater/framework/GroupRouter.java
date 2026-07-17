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
import net.splitcells.gel.rating.framework.Rating;

import static net.splitcells.dem.resource.communication.log.Logs.logs;

/**
 * This is a helper interface, in order to create a rater based on one method.
 */
public interface GroupRouter {

    /**
     * The {@link Rating} of an Event, has to be applied to all {@link Line}s of a group.
     *
     * @param lines    lines
     * @param children children
     * @return return
     */
    RatingEvent routing(View lines, List<Constraint> children);

    default Proposal propose(Proposal proposal) {
        if (StaticFlags.WARNING) {
            logs().warnUnimplementedPart(getClass());
        }
        return proposal;
    }

    String descriptivePathName();
}
