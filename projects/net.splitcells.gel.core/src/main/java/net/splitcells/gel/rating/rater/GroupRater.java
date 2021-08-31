/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.rating.rater;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.rating.framework.Rating;

import java.util.Optional;

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
}
