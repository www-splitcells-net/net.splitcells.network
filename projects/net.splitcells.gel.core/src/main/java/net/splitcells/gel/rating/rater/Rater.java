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
package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import net.splitcells.gel.common.Language;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
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
     *
     * @param lines TODO Why is this needed? Is this not contained in other variables?
     * @param addition
     * @param children
     * @param ratingsBeforeAddition
     * @return
     */
    RatingEvent ratingAfterAddition(Table  lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition);

    /**
     *
     * @param lines TODO Why is this needed. Is this not contained in other variables?
     * @param removal
     * @param children
     * @param ratingsBeforeRemoval
     * @return
     */
    RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval);

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
}
