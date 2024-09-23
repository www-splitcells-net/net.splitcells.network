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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.splitcells.dem.lang.namespace.NameSpaces.GEL;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public final class Derivation implements Constraint {

    public static Derivation derivation
            (Constraint derivationTarget, Function<Rating, Rating> derivationFunction) {
        return new Derivation(derivationTarget, derivationFunction);
    }

    private final Constraint derivationTarget;
    private final Function<Rating, Rating> derivationFunction;

    private Derivation(Constraint derivationTarget, Function<Rating, Rating> derivationFunction) {
        this.derivationTarget = derivationTarget;
        this.derivationFunction = derivationFunction;
    }

    @Override
    public GroupId injectionGroup() {
        return derivationTarget.injectionGroup();
    }

    @Override
    public Rating rating(GroupId group, Line line) {
        return derivationFunction.apply(derivationTarget.rating(group, line));
    }

    @Override
    public Rating rating(GroupId group) {
        return derivationFunction.apply(derivationTarget.rating(group));
    }

    @Override
    public Optional<Perspective> naturalArgumentation(GroupId group) {
        return derivationTarget.naturalArgumentation(group)
                .map(argumentation -> perspective("Derived via " + derivationFunction + ".", GEL)
                        .withChild(argumentation));
    }

    @Override
    public Optional<Discoverable> mainContext() {
        return derivationTarget.mainContext();
    }

    @Override
    public Optional<Perspective> naturalArgumentation
            (Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        return derivationTarget.naturalArgumentation(line, group, allocationSelector)
                .map(argumentation -> perspective("Derived via " + derivationFunction + ".", GEL)
                        .withChild(argumentation));
    }

    @Override
    public GroupId groupOf(Line line) {
        return derivationTarget.groupOf(line);
    }

    @Override
    public void registerAdditions(GroupId group, Line line) {
        throw notImplementedYet();
    }

    @Override
    public void registerBeforeRemoval(GroupId group, Line line) {
        throw notImplementedYet();
    }

    @Override
    public List<Constraint> childrenView() {
        throw notImplementedYet();
    }

    @Override
    public Set<Line> complying(GroupId group) {
        throw notImplementedYet();
    }

    @Override
    public net.splitcells.dem.data.set.Set<Line> defying(GroupId group) {
        throw notImplementedYet();
    }

    @Override
    public Line addResult(LocalRating localRating) {
        throw notImplementedYet();
    }

    @Override
    public Assignments lineProcessing() {
        throw notImplementedYet();
    }

    @Override
    public Perspective toPerspective(Set<GroupId> groups) {
        return derivationTarget.toPerspective(groups);
    }

    @Override
    public Table lines() {
        throw notImplementedYet();
    }

    @Override
    public void recalculatePropagation() {
        derivationTarget.recalculatePropagation();
    }

    @Override
    public void init(Solution solution) {
        derivationTarget.init(solution);
    }

    @Override
    public Proposal propose(Proposal proposal) {
        return derivationTarget.propose(proposal);
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        final var path = derivationTarget.path();
        path.add(getClass().getSimpleName());
        return path;
    }

    @Override
    public net.splitcells.dem.data.set.list.List<Domable> arguments() {
        throw notImplementedYet();
    }

    @Override
    public Class<? extends Constraint> type() {
        throw notImplementedYet();
    }

    @Override
    public Constraint withChildren(Constraint... constraints) {
        throw notImplementedYet();
    }

    @Override
    public Constraint withChildren(Function<Query, Query> builder) {
        throw notImplementedYet();
    }

    @Override
    public void addContext(Discoverable context) {
        throw notImplementedYet();
    }

    @Override
    public net.splitcells.dem.data.set.Set<net.splitcells.dem.data.set.list.List<String>> paths() {
        throw notImplementedYet();
    }

    @Override
    public Perspective toPerspective() {
        return derivationTarget.toPerspective();
    }
}
