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

import java.util.Optional;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.PerspectiveView;
import net.splitcells.dem.lang.perspective.antlr4.DenParser;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

/**
 * <p>This interface allows one to construct {@link Constraint} trees via paths of {@link Constraint}s.
 * It also allows one to select a specific {@link Constraint} in the tree.
 * The path is created by calling methods, that are returning a new {@link Query} object.
 * {@link #currentConstraint} can be used in order to retrieve the currently selected {@link Constraint}.
 * </p>
 * <p>TODO Split this up in read only query and builder based on query.
 * Somehow illegal queries need to be marked via interface.</p>
 */
public interface Query {

    /**
     * @return The {@link Assignments} on which the {@link Query} is applied to.
     */
    Optional<Assignments> subject();

    Query forAll(Attribute<?> args);

    Query forAll(Rater classifier);

    Query forAll();

    Query then(Rater rater);

    Query then(Rating rating);

    @SuppressWarnings("unchecked")
    default Query forAllCombinationsOf(Attribute<? extends Object>... args) {
        return forAllCombinationsOf(listWithValuesOf(args));
    }

    Query forAllCombinationsOf(List<Attribute<? extends Object>> args);


    /**
     * @return This {@link Rating} states the value of all {@link #currentInjectionGroups}.
     */
    Rating rating();

    /**
     * @return The currently selected {@link Constraint}.
     */
    Constraint currentConstraint();

    /**
     * @return The {@link Constraint} instance, where the first {@link Query} of the path was created.
     */
    Optional<Constraint> root();

    List<Constraint> constraintPath();

    /**
     * @return Set of all {@link #currentConstraint()}'s {@link GroupId}s,
     * that where indirectly created via {@link Constraint#injectionGroup()} of the {@link #root()}.
     */
    Set<GroupId> currentInjectionGroups();

    Query forAll(List<Rater> classifiers);

    Query parse(DenParser.AccessContext arg);

    Query parse(DenParser.Function_callContext arg);
}
