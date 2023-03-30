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
package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.common.Language;
import net.splitcells.gel.constraint.type.framework.ConstraintAI;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.framework.Rating;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.DiscoverableFromMultiplePathsSetter;
import net.splitcells.dem.utils.reflection.PubliclyConstructed;
import net.splitcells.dem.utils.reflection.PubliclyTyped;

/**
 * <p>When a {@link Rater} is implemented, one often has a choice,
 * to implement a simple {@link Rater} or a performant {@link Rater}.
 * A simple instance will calculate the {@link Rating} of all {@link Line} in a given group.
 * The performant one, will only calculate the changes in {@link Rating} of a given group and
 * the added or removed {@link Line}.</p>
 * <p>
 * <p>{@link Rater} implementations should always calculate the {@link Constraint#RESULTING_CONSTRAINT_GROUP}
 * based on the {@link Constraint#INCOMING_CONSTRAINT_GROUP}.</p>
 * TODO RENAME Rater seems to be an incorrect name, because it produces more than a rating.
 */
public interface Rater extends PubliclyTyped<Rater>
        , PubliclyConstructed<Domable>
        , DiscoverableFromMultiplePathsSetter
        , Domable {

    /**
     * Calculates the {@link Rating} updates of the given {@code linesOfGroup}.
     *
     * @param lines          The already present lines of the group after the addition.
     *                       The {@link Table#headerView()} of this is the same as of {@link Constraint#lines()}.
     *                       In order to check, whether {@code addition} is equal to an element of {@code lines},
     *                       one should not use {@link Line#equalsTo(Line)}, as both have different {@link Line#context()}.
     *                       Compare their {@link Line#index()} instead.
     * @param addition       The new {@link Line} of the {@code linesOfGroup} and {@link Constraint#lines()}.
     * @param children       These are all sub {@link Constraint}s, to which the {@code linesOfGroup} can be propagated to.
     *                       A classic implementation to propagate all complying lines to all {@code children}.
     *                       See {@link Constraint#childrenView()}.
     * @param lineProcessing This is the {@link Constraint#lineProcessing()} of the incoming group before the addition.
     * @return
     */
    RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table lineProcessing);

    /**
     * Nothing needs to be done here, if the {@link Rating} of one {@link Line} is not dependent on the rating of another line.
     * {@link RaterBasedOnLineValue} can be used for constructing such {@link Rater}s.
     *
     * @param lines          These are all {@link Constraint#lines()} of the incoming group present before the removal.
     * @param removal        This is the line of {@link Constraint#lines()} and {@code linesOfGroup} that will be removed.
     * @param children       These are the children {@link Constraint}s of the current {@link Constraint} node.
     * @param lineProcessing This is the {@link Constraint#lineProcessing()} of the incoming group before the removal.
     * @return The events needed to update the {@link Rating} of all lines.
     * A {@link Rating} update for the {@code removal} argument is not required,
     * because its {@link Rating} will be automatically removed from the {@link Constraint} during the actual removal.
     */
    default RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table lineProcessing) {
        return ratingEvent();
    }

    @Override
    default Class<? extends Rater> type() {
        return Rater.class;
    }

    @Deprecated
    default Node argumentation(GroupId group, Table allocations) {
        throw notImplementedYet(getClass().getName());
    }

    @Override
    default Node toDom() {
        final var dom = Xml.elementWithChildren(getClass().getSimpleName());
        if (!arguments().isEmpty()) {
            dom.appendChild(Xml.element2(Language.ARGUMENTATION.value(), arguments().stream().map(arg -> arg.toDom())));
        }
        return dom;
    }

    /**
     * Describes in a natural way, how this is rating given {@link Line}.
     * Prefer using one complete sentence for the description.
     *
     * @param line
     * @param groupsLineProcessing
     * @param incomingGroup
     * @return
     */
    String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup);

    default Set<List<String>> paths() {
        return setOfUniques();
    }

    @Override
    default void addContext(Discoverable context) {

    }

    default String name() {
        return getClass().getSimpleName();
    }
}
