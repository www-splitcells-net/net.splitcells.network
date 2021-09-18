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
