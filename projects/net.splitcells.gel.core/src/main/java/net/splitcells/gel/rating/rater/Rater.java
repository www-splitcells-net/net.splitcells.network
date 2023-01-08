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
 * <p>Someone will be said to have spoken ill of you; think whether you did not first speak ill of him; think of how
 * many persons you have yourself spoken ill. - Lucius Annaeus Seneca
 * </p>
 *
 * TODO RENAME Rater seems to be an incorrect name, because it produces more than a rating.
 */
public interface Rater extends PubliclyTyped<Rater>
        , PubliclyConstructed<Domable>
        , DiscoverableFromMultiplePathsSetter
        , Domable {

    /**
     * Calculates the {@link Rating} updates of the given {@code linesOfGroup}.
     *
     * @param lines The already present lines of the group after the addition.
     *                     The {@link Table#headerView()} of this is the same as of {@link Constraint#lineProcessing()}.
     * @param addition The new {@link Line} of the {@code linesOfGroup} and {@link Constraint#lines()}.
     * @param children These are all sub {@link Constraint}s, to which the {@code linesOfGroup} can be propagated to.
     *                 A classic implementation to propagate all complying lines to all {@code children}.
     *                 See {@link Constraint#childrenView()}.
     * @param lineProcessing This is the {@link Constraint#lineProcessing()} of the incoming group before the addition.
     * @return
     */
    RatingEvent ratingAfterAddition(Table  lines, Line addition, List<Constraint> children, Table lineProcessing);

    /**
     * Nothing needs to be done here, if the {@link Rating} of one {@link Line} is not dependent on the rating of another line.
     * {@link RaterBasedOnLineValue} can be used for constructing such {@link Rater}s.
     *
     * @param lines These are all {@link Constraint#lines()} of the incoming group present before the removal.
     * @param removal This is the line of {@link Constraint#lines()} and {@code linesOfGroup} that will be removed.
     * @param children These are the children {@link Constraint}s of the current {@link Constraint} node.
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

    default String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        throw notImplementedYet(getClass().getName());
    }

    default Set<List<String>> paths() {
        return setOfUniques();
    }

    @Override
    default void addContext(Discoverable context) {

    }
}
