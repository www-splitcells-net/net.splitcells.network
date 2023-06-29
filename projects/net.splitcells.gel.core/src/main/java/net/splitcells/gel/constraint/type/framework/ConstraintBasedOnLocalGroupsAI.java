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

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.solution.Solution;

public class ConstraintBasedOnLocalGroupsAI extends ConstraintAI {

    public static ConstraintBasedOnLocalGroupsAI constraintBasedOnLocalGroupsAI(Function<Constraint, Rater> raterFactory
            , Optional<Discoverable> parent
            , BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation
            , Class<? extends Constraint> type) {
        return new ConstraintBasedOnLocalGroupsAI(raterFactory, parent, localNaturalArgumentation, type);
    }

    public static ConstraintBasedOnLocalGroupsAI constraintBasedOnLocalGroupsAI(Rater rater, String name
            , Optional<Discoverable> parent
            , BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation
            , Class<? extends Constraint> type) {
        return new ConstraintBasedOnLocalGroupsAI(rater, name, parent, localNaturalArgumentation, type);
    }

    public static ConstraintBasedOnLocalGroupsAI constraintBasedOnLocalGroupsAI(Rater rater
            , Optional<Discoverable> parent
            , BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation
            , Class<? extends Constraint> type) {
        return new ConstraintBasedOnLocalGroupsAI(rater, parent, localNaturalArgumentation, type);
    }

    public static ConstraintBasedOnLocalGroupsAI constraintBasedOnLocalGroupsAI(GroupId standardGroup, Rater rater
            , String name
            , Optional<Discoverable> parent
            , BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation
            , Class<? extends Constraint> type) {
        return new ConstraintBasedOnLocalGroupsAI(standardGroup, rater, name, parent, localNaturalArgumentation, type);
    }

    private final Rater rater;
    private final BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation;

    private ConstraintBasedOnLocalGroupsAI(Function<Constraint, Rater> raterFactory, Optional<Discoverable> parent
            , BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation
            , Class<? extends Constraint> type) {
        super(Constraint.standardGroup(), parent, type);
        rater = raterFactory.apply(this);
        this.localNaturalArgumentation = localNaturalArgumentation;
    }

    private ConstraintBasedOnLocalGroupsAI(Rater rater, String name, Optional<Discoverable> parent
            , BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation
            , Class<? extends Constraint> type) {
        this(Constraint.standardGroup(), rater, name, parent, localNaturalArgumentation, type);
    }

    private ConstraintBasedOnLocalGroupsAI(Rater rater, Optional<Discoverable> parent
            , BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation
            , Class<? extends Constraint> type) {
        this(Constraint.standardGroup(), rater, "", parent, localNaturalArgumentation, type);
    }

    private ConstraintBasedOnLocalGroupsAI(GroupId standardGroup, Rater rater, String name
            , Optional<Discoverable> parent
            , BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation
            , Class<? extends Constraint> type) {
        super(standardGroup, name, parent, type);
        this.rater = rater;
        this.localNaturalArgumentation = localNaturalArgumentation;
    }

    @Override
    public void init(Solution solution) {
        rater.init(solution);
        childrenView().forEach(c -> c.init(solution));
    }

    @Override
    public void processLineAddition(Line addition) {
        final var incomingGroup = addition.value(INCOMING_CONSTRAINT_GROUP);
        processRatingEvent(
                rater.ratingAfterAddition(
                        lines.columnView(INCOMING_CONSTRAINT_GROUP)
                                .lookup(incomingGroup)
                        , addition
                        , childrenView()
                        , lineProcessing
                                .columnView(INCOMING_CONSTRAINT_GROUP)
                                .lookup(incomingGroup)));
    }

    private void processRatingEvent(RatingEvent ratingEvent) {
        ratingEvent.removal().forEach(removal ->
                lineProcessing.assignmentsOfDemand(removal).forEach(lineProcessing::remove));
        ratingEvent.additions().forEach((line, resultUpdate) -> {
            final var r = addResult(resultUpdate);
            int i = r.index();
            lineProcessing.assign(line, r);
        });
        ratingEvent.complexAdditions().forEach((line, updates) -> {
            updates.stream().forEach(update -> {
                final var r = addResult(update);
                int i = r.index();
                lineProcessing.assign(line, r);
            });
        });
    }

    @Override
    public void processLinesBeforeRemoval(GroupId incomingGroup, Line removal) {
        processRatingEvent(
                rater.rating_before_removal(
                        lines.columnView(INCOMING_CONSTRAINT_GROUP).lookup(incomingGroup)
                        , lines.columnView(INCOMING_CONSTRAINT_GROUP)
                                .lookup(incomingGroup)
                                .columnView(LINE)
                                .lookup(removal)
                                .unorderedLinesStream()
                                .findFirst()
                                .orElseThrow()
                        , childrenView()
                        , lineProcessing.columnView(INCOMING_CONSTRAINT_GROUP).lookup(incomingGroup)));
        super.processLinesBeforeRemoval(incomingGroup, removal);
    }

    @Override
    public String localNaturalArgumentation(Report report) {
        return null;
    }

    @Override
    public List<String> path() {
        return mainContext
                .map(context -> context.path())
                .orElseGet(() -> list())
                .withAppended(this.getClass().getSimpleName());
    }

    @Override
    public List<Domable> arguments() {
        return list(rater);
    }

    @Override
    public Proposal propose(Proposal proposal) {
        return rater.propose(proposal);
    }


    public Rater rater() {
        return rater;
    }
}