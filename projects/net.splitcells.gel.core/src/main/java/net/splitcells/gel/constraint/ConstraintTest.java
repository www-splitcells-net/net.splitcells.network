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
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.gel.constraint.GroupId.group;
import static net.splitcells.gel.constraint.GroupId.rootGroup;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.constraint.type.ForAlls.forEach;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintTest {

    @Test
    public void testArgumentation() {
        final var attribute = attribute(Integer.class, "a");
        final int valueA = 1;
        final int valueB = 3;
        final var solution = defineProblem("testArgumentation")
                .withDemandAttributes(attribute)
                .withDemands(list(list(valueA)
                        , list(valueB)
                        , list(valueA)
                        , list(valueB)))
                .withSupplyAttributes()
                .withEmptySupplies(4)
                .withConstraint(query -> {
                    query.forAll(attribute).then(hasSize(4));
                    query.then(hasSize(2));
                    return query;
                })
                .toProblem()
                .asSolution();
        solution.optimize(offlineLinearInitialization());
        solution.constraint().naturalArgumentation().orElseThrow().requireEqualsTo(
                tree("For all allocations", STRING).withChildren(
                        tree("For all a", STRING)
                                .withChildren(tree("Then size should be 4, but is 2", STRING))
                        , tree("Then size should be 2, but is 4", STRING)))
        ;
    }

    @Test
    public void testMultipleGroupAssignment() {
        final var attribute = attribute(Integer.class);
        final var lineSupplier = Tables.table(attribute);
        final var group1 = rootGroup("1");
        final var group2 = rootGroup("2");
        final var testSubject = forEach(new Rater() {

            @Override
            public RatingEvent ratingAfterAddition(View lines, Line addition, List<Constraint> children, View ratingsBeforeAddition) {
                final var ratingEvent = ratingEvent();
                ratingEvent.complexAdditions().put(addition
                        , list(localRating().withRating(noCost()).withPropagationTo(children).withResultingGroupId(group1)
                                , localRating().withRating(noCost()).withPropagationTo(children).withResultingGroupId(group2)
                        ));
                return ratingEvent;
            }

            @Override
            public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
                return "no cost and 2 resulting groups for every line";
            }

            @Override
            public List<Domable> arguments() {
                return list();
            }
        });
        final var validator = forAll();
        testSubject.withChildren(validator);
        final List<Line> lines = list();
        {
            lines.withAppended(lineSupplier.addTranslated(list(1)), lineSupplier.addTranslated(list(2)));
            testSubject.register_addition(lines);
        }
        {
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(lines.size());
        }
        assertThat(validator.defying(group1)).isEmpty();
        assertThat(validator.defying(group2)).isEmpty();
        assertThat(validator.complying(group1)).hasSize(lines.size());
        assertThat(validator.complying(group2)).hasSize(lines.size());
    }
}
