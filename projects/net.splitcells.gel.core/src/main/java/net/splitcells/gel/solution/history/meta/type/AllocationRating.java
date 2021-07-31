/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
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
