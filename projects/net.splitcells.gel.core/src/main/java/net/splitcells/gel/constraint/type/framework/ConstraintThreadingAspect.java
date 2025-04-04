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
import net.splitcells.dem.execution.ExplicitEffect;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.execution.EffectWorker.effectWorker;
import static net.splitcells.dem.execution.EffectWorkerPool.effectWorkerPool;

/**
 * <p>TODO This aspect does not work,
 * when one reads data from {@link Constraint} asynchronously via not kind of main thread.
 * Read access of the {@link Constraint} does not work yet thread safely,
 * because one does not know which version in history of the stored {@link Constraint} is being accessed and
 * one does not know how far the synchronization of {@link Constraint} is gone while being accessed.
 * Therefore, this is a race condition also such read access does not make the {@link Constraint} inconsistent.
 * The reason for this is the fact,
 * that {@link Constraint} cannot trigger a synchronization of its parent {@link Constraint}.
 * Currently, {@link ConstraintThreadingAspect} can only trigger a synchronization of itself.</p>
 */
public class ConstraintThreadingAspect implements Constraint {
    public static Constraint constraintThreadingAspect(Constraint constraint) {
        return new ConstraintThreadingAspect(constraint);
    }

    private final ExplicitEffect<Constraint> constraintEffect;


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
    public Optional<Tree> naturalArgumentation(GroupId group) {
        return constraintEffect.affectSynchronously(c -> c.naturalArgumentation(group));
    }

    @Override
    public Optional<Discoverable> mainContext() {
        return constraintEffect.affectSynchronously(c -> c.mainContext());
    }

    @Override
    public Optional<Tree> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
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
    public View lineProcessing() {
        return constraintEffect.affectSynchronously(c -> c.lineProcessing());
    }

    @Override
    public Tree toPerspective(Set<GroupId> groups) {
        return constraintEffect.affectSynchronously(c -> c.toPerspective(groups));
    }

    @Override
    public View lines() {
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

    @Override
    public Tree toTree() {
        return constraintEffect.affectSynchronously(c -> c.toTree());
    }
}
