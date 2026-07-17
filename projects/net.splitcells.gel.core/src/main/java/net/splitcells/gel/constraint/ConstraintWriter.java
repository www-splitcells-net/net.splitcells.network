/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.constraint;

import java.util.function.Function;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.object.DiscoverableFromMultiplePathsSetter;

public interface ConstraintWriter extends DiscoverableFromMultiplePathsSetter {
    @ReturnsThis
    Constraint withChildren(Constraint... constraints);

    @ReturnsThis
    Constraint withChildren(Function<Query, Query> builder);

    @SuppressWarnings("unchecked")
    @ReturnsThis
    Constraint withChildren(List<Function<Query, Query>> builder);
}
