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
package net.splitcells.gel.constraint.type;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.framework.ConstraintAspect.constraintAspect;
import static net.splitcells.gel.constraint.type.framework.ConstraintBasedOnLocalGroupsAI.constraintBasedOnLocalGroupsAI;
import static net.splitcells.gel.rating.rater.lib.classification.RaterBasedOnGrouping.raterBasedGrouping;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.constraint.type.framework.ConstraintBasedOnLocalGroupsAI;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.lib.classification.ForAllAttributeValues;
import net.splitcells.gel.rating.rater.lib.classification.Propagation;
import net.splitcells.gel.rating.rater.lib.classification.RaterBasedOnGrouping;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ForAll implements Constraint {

    public static final String FOR_ALL_NAME = "forAll";

    private static final BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> LOCAL_NATURAL_ARGUMENTATION = (constraint, report) -> {
        final var raterBasedOnGrouping = constraint.rater().casted(RaterBasedOnGrouping.class);
        if (raterBasedOnGrouping.isPresent()) {
            if (raterBasedOnGrouping.get().classifier().type().equals(Propagation.class)) {
                return raterBasedOnGrouping.get().classifier().toSimpleDescription(report.line()
                        , constraint.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(report.group())
                        , report.group());
            }
        }
        if (constraint.rater().type().equals(ForAllAttributeValues.class)) {
            return constraint.rater().toSimpleDescription(report.line()
                    , constraint.lineProcessing().columnView(Constraint.INCOMING_CONSTRAINT_GROUP).lookup(report.group())
                    , report.group());
        } else {
            return "For all " + constraint.rater().toSimpleDescription(report.line()
                    , constraint.lineProcessing().columnView(Constraint.INCOMING_CONSTRAINT_GROUP).lookup(report.group())
                    , report.group());
        }
    };

    public static Constraint forAll(Rater classifier) {
        return create(classifier);
    }

    public static Constraint forAll(Rater classifier, Optional<Discoverable> parent) {
        return constraintAspect(new ForAll(classifier
                , parent.map(d -> d.child(list(classifier.name())))
        ));
    }

    @Deprecated
    public static Constraint create(Rater classifier) {
        return constraintAspect(new ForAll(classifier));
    }

    private final ConstraintBasedOnLocalGroupsAI constraint;

    private ForAll(Rater classifier) {
        final var rater = raterBasedGrouping(classifier);
        constraint = constraintBasedOnLocalGroupsAI(rater, Optional.empty()
                , LOCAL_NATURAL_ARGUMENTATION
                , ForAll.class);
    }

    private ForAll(Rater classifier, Optional<Discoverable> parent) {
        final var rater = raterBasedGrouping(classifier);
        constraint = constraintBasedOnLocalGroupsAI(rater, parent, LOCAL_NATURAL_ARGUMENTATION, ForAll.class);
    }

    public Rater classification() {
        return constraint.rater();
    }

    @Override
    public Class<? extends Constraint> type() {
        return ForAll.class;
    }

    @Override
    public List<String> path() {
        return constraint.path();
    }

    @Override
    public Set<List<String>> paths() {
        return constraint.paths();
    }

    @Override
    public void addContext(Discoverable context) {
        constraint.addContext(context);
    }

    @Override
    public List<Domable> arguments() {
        return constraint.arguments();
    }

    @Override
    public GroupId injectionGroup() {
        return constraint.injectionGroup();
    }

    @Override
    public Rating rating(GroupId group, Line line) {
        return constraint.rating(group, line);
    }

    @Override
    public Rating rating(GroupId group) {
        return constraint.rating(group);
    }

    @Override
    public Optional<Perspective> naturalArgumentation(GroupId group) {
        return constraint.naturalArgumentation(group);
    }

    @Override
    public Optional<Discoverable> mainContext() {
        return constraint.mainContext();
    }

    @Override
    public Optional<Perspective> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        return constraint.naturalArgumentation(line, group, allocationSelector);
    }

    @Override
    public GroupId groupOf(Line line) {
        return constraint.groupOf(line);
    }

    @Override
    public void registerAdditions(GroupId group, Line line) {
        constraint.registerAdditions(group, line);
    }

    @Override
    public void registerBeforeRemoval(GroupId group, Line line) {
        constraint.registerBeforeRemoval(group, line);
    }

    @Override
    public List<Constraint> childrenView() {
        return constraint.childrenView();
    }

    @Override
    public Set<Line> complying(GroupId group) {
        return constraint.complying(group);
    }

    @Override
    public Set<Line> defying(GroupId group) {
        return constraint.defying(group);
    }

    @Override
    public Line addResult(LocalRating localRating) {
        return constraint.addResult(localRating);
    }

    @Override
    public Table lineProcessing() {
        return constraint.lineProcessing();
    }

    @Override
    public Perspective toPerspective() {
        return constraint.toPerspective();
    }

    @Override
    public Perspective toPerspective(Set<GroupId> groups) {
        return constraint.toPerspective(groups);
    }

    @Override
    public Table lines() {
        return constraint.lines();
    }

    @Override
    public void init(Solution solution) {
        constraint.init(solution);
    }

    @Override
    public Proposal propose(Proposal proposal) {
        return constraint.propose(proposal);
    }

    @Override
    public Constraint withChildren(Constraint... constraints) {
        return constraint.withChildren(constraints);
    }

    @Override
    public Constraint withChildren(Function<Query, Query> builder) {
        return constraint.withChildren(builder);
    }
}
