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

import java.util.Optional;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.Rater;

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

    Query forAll(Attribute<?> args);

    Query forAll(Rater classifier);

    Query forAll();

    Query then(Rater rater);

    Query then(Rating rating);

    Query forAllCombinationsOf(Attribute<?>... args);

    /**
     * @return This {@link Rating} states the value of all {@link #currentInjectionGroups}.
     */
    Rating rating();

    /**
     * 
     * @return The currently selected {@link Constraint}.
     */
    Constraint currentConstraint();

    /**
     * @return The {@link Constraint} instance, where the first {@link Query} of the path was created.
     */
    Optional<Constraint> root();

    List<Constraint> constraintPath();

    /**
     *
     * @return Set of all {@link #currentConstraint()}'s {@link GroupId}s,
     * that where indirectly created via {@link Constraint#injectionGroup()} of the {@link #root()}.
     */
    Set<GroupId> currentInjectionGroups();
}
