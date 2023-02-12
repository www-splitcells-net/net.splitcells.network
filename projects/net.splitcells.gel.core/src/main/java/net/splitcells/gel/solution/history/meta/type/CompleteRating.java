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
package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.rating.framework.Rating;
import org.w3c.dom.Node;

public final class CompleteRating implements MetaData<Rating>, Domable {

    public static CompleteRating completeRating(Rating rating) {
        return new CompleteRating(rating);
    }

    private final Rating rating;

    private CompleteRating(Rating rating) {
        this.rating = rating;

    }

    @Override
    public Rating value() {
        return rating;
    }

    @Override
	public String toString() {
    	return Xml.toPrettyString(toDom());
	}

	@Override
	public Node toDom() {
    	final var dom = Xml.elementWithChildren(getClass().getSimpleName());
    	dom.appendChild(rating.toDom());
		return dom;
	}
}
