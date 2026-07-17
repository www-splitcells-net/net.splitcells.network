/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.framework;

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;

import java.util.Optional;

import static net.splitcells.dem.resource.communication.log.Logs.logs;

/**
 * This is a helper interface, in order to create a rater based on one method.
 */
public interface GroupRater {

    static GroupRater describedGroupRater(GroupRater arg, String description, String descriptivePathName) {
        return new GroupRater() {
            @Override
            public Rating lineRating(View lines, Optional<Line> addition, Optional<Line> removal) {
                return arg.lineRating(lines, addition, removal);
            }

            @Override
            public String toString() {
                return description;
            }
            
            @Override public String descriptivePathName() {
                return descriptivePathName;
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
    Rating lineRating(View lines, Optional<Line> addition, Optional<Line> removal);

    /**
     * By default, {@link Proposal}s are not processed.
     *
     * @param proposal Already present proposal.
     * @return Adjustment to the proposal, so that the given proposal is compliant with this {@link Rater}.
     */
    default Proposal propose(Proposal proposal) {
        if (StaticFlags.WARNING) {
            logs().warnUnimplementedPart(getClass());
        }
        return proposal;
    }
    
    String descriptivePathName();
}
