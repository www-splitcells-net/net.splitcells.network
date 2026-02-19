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
import static net.splitcells.gel.rating.rater.lib.ConstantRater.constantRater;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.constraint.type.framework.ConstraintBasedOnLocalGroupsAI;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Then implements Constraint {

    public static final String THEN_NAME = "then";

    private static final BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> LOCAL_NATURAL_ARGUMENTATION = (argConstraint, report) -> {
        return "Then " + argConstraint.rater().toSimpleDescription(report.line()
                , argConstraint.lineProcessing().columnView(Constraint.INCOMING_CONSTRAINT_GROUP).lookup(report.group())
                , report.group());
    };

    @Deprecated
    public static Constraint then(Rater rater) {
        return constraintAspect(new Then(rater));
    }

    public static Constraint then(Rater rater, Optional<Discoverable> parent) {
        return constraintAspect(new Then(rater, parent));
    }

    public static Constraint then(Rating rating) {
        return constraintAspect(new Then(constantRater(rating)));
    }

    private final ConstraintBasedOnLocalGroupsAI constraint;

    private Then(Rater rater, Optional<Discoverable> parent) {
        constraint = constraintBasedOnLocalGroupsAI(rater, rater.name()
                , parent
                , LOCAL_NATURAL_ARGUMENTATION
                , Then.class);
    }

    private Then(Rater rater) {
        constraint = constraintBasedOnLocalGroupsAI(rater, rater.name(), Optional.empty()
                , LOCAL_NATURAL_ARGUMENTATION
                , Then.class);
    }

    @Override
    public Class<? extends Constraint> type() {
        return Then.class;
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
    public Optional<Tree> naturalArgumentation(GroupId group) {
        return constraint.naturalArgumentation(group);
    }

    @Override
    public Optional<Discoverable> mainContext() {
        return constraint.mainContext();
    }

    @Override
    public Optional<Tree> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
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
    public View lineProcessing() {
        return constraint.lineProcessing();
    }

    @Override
    public Tree toPerspective(Set<GroupId> groups) {
        return constraint.toPerspective(groups);
    }

    @Override
    public View lines() {
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

    @Override
    public Tree toTree() {
        return constraint.toTree();
    }
}
