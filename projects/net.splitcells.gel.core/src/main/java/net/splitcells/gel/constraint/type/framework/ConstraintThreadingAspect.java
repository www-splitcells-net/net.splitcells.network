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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.execution.Effect;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
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
import org.w3c.dom.Node;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.execution.EffectWorker.effectWorker;
import static net.splitcells.dem.execution.EffectWorkerPool.effectWorkerPool;

public class ConstraintThreadingAspect implements Constraint {
    public static Constraint constraintThreadingAspect(Constraint constraint) {
        return new ConstraintThreadingAspect(constraint);
    }

    private final Effect<Constraint> constraintEffect;


    private ConstraintThreadingAspect(Constraint constraint) {
        constraintEffect = effectWorker(constraint);
    }

    @Override
    public GroupId injectionGroup() {
        return constraintEffect.affectSynchronously(c -> c.injectionGroup());
    }

    @Override
    public Rating rating(GroupId group, Line line) {
        return constraintEffect.affectSynchronously(c -> c.rating(group, line));
    }

    @Override
    public Rating rating(GroupId group) {
        return constraintEffect.affectSynchronously(c -> c.rating(group));
    }

    @Override
    public Optional<Perspective> naturalArgumentation(GroupId group) {
        return constraintEffect.affectSynchronously(c -> c.naturalArgumentation(group));
    }

    @Override
    public Optional<Discoverable> mainContext() {
        return constraintEffect.affectSynchronously(c -> c.mainContext());
    }

    @Override
    public Optional<Perspective> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        return constraintEffect.affectSynchronously(c -> c.naturalArgumentation(line, group, allocationSelector));
    }

    @Override
    public GroupId groupOf(Line line) {
        return constraintEffect.affectSynchronously(c -> c.groupOf(line));
    }

    @Override
    public void registerAdditions(GroupId group, Line line) {
        constraintEffect.affect(c -> c.registerAdditions(group, line));
    }

    @Override
    public void registerBeforeRemoval(GroupId group, Line line) {
        constraintEffect.affect(c -> c.registerBeforeRemoval(group, line));
    }

    @Override
    public void recalculateProcessing() {
        constraintEffect.affectSynchronously(c -> {
            final Map<GroupId, List<Line>> registeredAdditions = map();
            c.lineProcessing().unorderedLinesStream().forEach(l -> {
                final var incomingConstraintGroup = l.value(INCOMING_CONSTRAINT_GROUP);
                registeredAdditions.computeIfAbsent(incomingConstraintGroup, i -> list()).add(l.value(LINE));
            });
            registeredAdditions.entrySet().forEach(e -> e.getValue().forEach(l -> c.registerBeforeRemoval(e.getKey(), l)));
            registeredAdditions.entrySet().forEach(e -> e.getValue().forEach(l -> c.registerAdditions(e.getKey(), l)));
            return null;
        });
    }

    @Override
    public List<Constraint> childrenView() {
        return constraintEffect.affectSynchronously(c -> c.childrenView());
    }

    @Override
    public Set<Line> complying(GroupId group) {
        return constraintEffect.affectSynchronously(c -> c.complying(group));
    }

    @Override
    public Set<Line> defying(GroupId group) {
        return constraintEffect.affectSynchronously(c -> c.defying(group));
    }

    @Override
    public Line addResult(LocalRating localRating) {
        return constraintEffect.affectSynchronously(c -> c.addResult(localRating));
    }

    @Override
    public Table lineProcessing() {
        return constraintEffect.affectSynchronously(c -> c.lineProcessing());
    }

    @Override
    public Perspective toPerspective(Set<GroupId> groups) {
        return constraintEffect.affectSynchronously(c -> c.toPerspective(groups));
    }

    @Override
    public Table lines() {
        return constraintEffect.affectSynchronously(c -> c.lines());
    }

    @Override
    public void init(Solution solution) {
        constraintEffect.affect(c -> c.init(solution));
    }

    @Override
    public Proposal propose(Proposal proposal) {
        return constraintEffect.affectSynchronously(c -> c.propose(proposal));
    }

    @Override
    public Node toDom() {
        return constraintEffect.affectSynchronously(c -> c.toDom());
    }

    @Override
    public List<String> path() {
        return constraintEffect.affectSynchronously(c -> c.path());
    }

    @Override
    public List<Domable> arguments() {
        return constraintEffect.affectSynchronously(c -> c.arguments());
    }

    @Override
    public Class<? extends Constraint> type() {
        return constraintEffect.affectSynchronously(c -> c.type());
    }

    @Override
    public Constraint withChildren(Constraint... constraints) {
        constraintEffect.affectSynchronously(c -> c.withChildren(constraints));
        return this;
    }

    @Override
    public Constraint withChildren(Function<Query, Query> builder) {
        constraintEffect.affectSynchronously(c -> c.withChildren(builder));
        return this;
    }

    @Override
    public void addContext(Discoverable context) {
        constraintEffect.affect(c -> c.addContext(context));
    }

    @Override
    public Set<List<String>> paths() {
        return constraintEffect.affectSynchronously(c -> c.paths());
    }
}
