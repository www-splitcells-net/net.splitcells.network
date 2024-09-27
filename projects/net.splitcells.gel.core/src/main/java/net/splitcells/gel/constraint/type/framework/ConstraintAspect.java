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
package net.splitcells.gel.constraint.type.framework;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.constraint.type.framework.ConstraintThreadingAspect.constraintThreadingAspect;

/**
 * <p>TODO Make this aspect optional for {@link Constraint}s.</p>
 * <p>TODO Create dedicated aspect for rating caching.</p>
 * <p>TODO Currently, {@link ConstraintThreadingAspect} makes the performance worse.</p>
 */
public class ConstraintAspect implements Constraint {

    public static Constraint constraintAspect(Constraint constraint) {
        if (Dem.configValue(ConstraintMultiThreading.class)) {
            return constraintThreadingAspect(new ConstraintAspect(constraint));
        }
        return new ConstraintAspect(constraint);
    }

    private final Map<GroupId, Rating> ratingCache = map();
    private final Constraint constraint;

    private ConstraintAspect(Constraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public List<String> path() {
        return constraint.path();
    }

    @Override
    public net.splitcells.dem.data.set.Set<List<String>> paths() {
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
    public Class<? extends Constraint> type() {
        return constraint.type();
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
        return ratingCache.computeIfAbsent(group, g -> constraint.rating(group));
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
        ratingCache.remove(group);
        constraint.registerAdditions(group, line);
    }

    @Override
    public void registerBeforeRemoval(GroupId group, Line line) {
        ratingCache.remove(group);
        constraint.registerBeforeRemoval(group, line);
    }

    @Override
    public List<Constraint> childrenView() {
        ratingCache.clear();
        return constraint.childrenView();
    }

    @Override
    public Set<Line> complying(GroupId group) {
        return constraint.complying(group);
    }

    @Override
    public net.splitcells.dem.data.set.Set<Line> defying(GroupId group) {
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
    public Tree toPerspective(Set<GroupId> groups) {
        return constraint.toPerspective(groups);
    }

    @Override
    public Table lines() {
        return constraint.lines();
    }

    @Override
    public void recalculatePropagation() {
        constraint.recalculatePropagation();
    }

    @Override
    public void init(Solution solution) {
        constraint.init(solution);
    }

    @Override
    public Constraint withChildren(Constraint... constraints) {
        ratingCache.clear();
        constraint.withChildren(constraints);
        return this;
    }

    @Override
    public Constraint withChildren(Function<Query, Query> builder) {
        ratingCache.clear();
        constraint.withChildren(builder);
        return this;
    }

    @Override
    public Proposal propose(Proposal proposal) {
        return constraint.propose(proposal);
    }

    @Override
    public Tree toPerspective() {
        return constraint.toPerspective();
    }
}
