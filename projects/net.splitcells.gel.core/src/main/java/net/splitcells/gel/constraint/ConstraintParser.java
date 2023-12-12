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
package net.splitcells.gel.constraint;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.antlr4.DenParser;

import java.util.Optional;

public interface ConstraintParser {
    /**
     * Parses a {@link Constraint} and add this to the {@link Query}.
     * Note, that the {@link Constraint} being added via {@link QueryEditor#nextQueryPathElement(Set, Constraint)},
     * can be a child of {@link Constraint#childrenView()}.
     * In other words, the added {@link Constraint} may not be a newly created {@link Constraint},
     * but an element of {@link Constraint#childrenView()} in {@link QueryEditor#currentConstraint()}.
     *
     * @param queryEditor This is the currently, already parsed part of the {@link Constraint} tree's branch.
     * @param content     This is the content parsed, in order to add a new {@link Constraint} to the query.
     * @return The next {@link Query} element in the {@link Constraint} tree branch.
     */
    Optional<QueryEditor> parseConstraint(QueryEditor queryEditor, DenParser.AccessContext content);

    /**
     * @param queryEditor
     * @param content
     * @return
     * @see #parseConstraint(QueryEditor, DenParser.AccessContext)
     */
    Optional<QueryEditor> parseConstraint(QueryEditor queryEditor, DenParser.Function_callContext content);
}
