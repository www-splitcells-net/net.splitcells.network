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
package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.rating.framework.Rating;
import org.w3c.dom.Node;

public class AllocationRating implements MetaData<Rating> {

    public static AllocationRating allocationRating(Rating rating) {
        return new AllocationRating(rating);
    }

    private final Rating rating;

    private AllocationRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    public Rating value() {
        return rating;
    }

    @Override
    public Node toDom() {
        final var dom = Xml.elementWithChildren(getClass().getSimpleName());
        dom.appendChild(rating.toDom());
        return dom;
    }
}
