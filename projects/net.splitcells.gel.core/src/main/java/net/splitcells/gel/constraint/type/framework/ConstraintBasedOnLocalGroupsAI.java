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
package net.splitcells.gel.constraint.type.framework;

import static net.splitcells.dem.data.set.list.Lists.list;

import java.util.function.Function;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

@Deprecated
public abstract class ConstraintBasedOnLocalGroupsAI extends ConstraintAI {
    protected final Rater rater;

    @Deprecated
    protected ConstraintBasedOnLocalGroupsAI(Function<Constraint, Rater> raterFactory) {
        super(Constraint.standardGroup());
        rater = raterFactory.apply(this);
    }

    @Deprecated
    protected ConstraintBasedOnLocalGroupsAI(Rater rater, String name) {
        this(Constraint.standardGroup(), rater, name);
    }

    @Deprecated
    protected ConstraintBasedOnLocalGroupsAI(Rater rater) {
        this(Constraint.standardGroup(), rater, "");
    }

    @Deprecated
    protected ConstraintBasedOnLocalGroupsAI(GroupId standardGroup, Rater rater, String name) {
        super(standardGroup, name);
        this.rater = rater;
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

    protected void processRatingEvent(RatingEvent ratingEvent) {
        ratingEvent.removal().forEach(removal ->
                lineProcessing.allocationsOfDemand(removal).forEach(lineProcessing::remove));
        ratingEvent.additions().forEach((line, resultUpdate) -> {
            final var r = addResult(resultUpdate);
            int i = r.index();
            lineProcessing.allocate(line, r);
        });
        ratingEvent.complexAdditions().forEach((line, updates) -> {
            updates.stream().forEach(update -> {
                final var r = addResult(update);
                int i = r.index();
                lineProcessing.allocate(line, r);
            });
        });
    }

    @Override
    protected void processLinesBeforeRemoval(GroupId incomingGroup, Line removal) {
        processRatingEvent(
                rater.rating_before_removal(
                        lines.columnView(INCOMING_CONSTRAINT_GROUP).lookup(incomingGroup)
                        , lines.columnView(INCOMING_CONSTRAINT_GROUP)
                                .lookup(incomingGroup)
                                .columnView(LINE)
                                .lookup(removal)
                                .linesStream()
                                .findFirst()
                                .orElseThrow()
                        , childrenView()
                        , lineProcessing.columnView(INCOMING_CONSTRAINT_GROUP).lookup(incomingGroup)));
        super.processLinesBeforeRemoval(incomingGroup, removal);
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

}