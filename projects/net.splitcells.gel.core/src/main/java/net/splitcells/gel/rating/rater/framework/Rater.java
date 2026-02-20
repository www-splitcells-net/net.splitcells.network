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
package net.splitcells.gel.rating.rater.framework;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue;
import net.splitcells.gel.solution.Solution;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.DiscoverableFromMultiplePathsSetter;
import net.splitcells.dem.utils.reflection.PubliclyConstructed;
import net.splitcells.dem.utils.reflection.PubliclyTyped;

/**
 * <p>When a {@link Rater} is implemented, one often has a choice,
 * to implement a simple {@link Rater} or a performant {@link Rater}.
 * A simple instance will calculate the {@link Rating} of all {@link Line} in a given group.
 * The performant one, will only calculate the changes in {@link Rating} of a given group and
 * the added or removed {@link Line}.</p>
 * <p>{@link Rater} implementations should always calculate the {@link Constraint#RESULTING_CONSTRAINT_GROUP}
 * based on the {@link Constraint#INCOMING_CONSTRAINT_GROUP}.
 * Avoid using caches or to store state in the {@link Rater} implementation if possible,
 * as this prone to errors.
 * For instance, caching {@link Constraint#INCOMING_CONSTRAINT_GROUP} and {@link Constraint#RESULTING_CONSTRAINT_GROUP}
 * can lead to accidental incorrect mappings between the 2.
 * Avoiding such a cache also avoids such mismatches as {@link #ratingAfterAddition(View, Line, List, View)}
 * provides info only for one {@link Constraint#INCOMING_CONSTRAINT_GROUP} at a time.</p>
 * <p>TODO RENAME Rater seems to be an incorrect name, because it produces more than a rating.</p>
 * <p>TODO For methods like {@link #ratingAfterAddition(View, Line, List, View)} use one dedicated type,
 * in order to make the method easy to extend.</p>
 */
public interface Rater extends PubliclyTyped<Rater>
        , PubliclyConstructed<Domable>
        , DiscoverableFromMultiplePathsSetter
        , Domable {

    /**
     * <p>Calculates the {@link Rating} updates of the given {@code linesOfGroup}.</p>
     * <p>TODO Is lines parameter not obsolete because of lineProcessing parameter?
     * Note, that the return value of this function requires elements of lines.
     * This could be provided via lineProcessing,
     * if it was a view on the allocations.
     * </p>
     *
     * @param linesOfGroup                 The already present lines of the group after the addition.
     *                                     The {@link View#headerView()} of this is the same as of {@link Constraint#lines()}.
     *                                     In order to check, whether {@code addition} is equal to an element of {@code lines},
     *                                     one should not use {@link Line#equalsTo(Line)}, as both have different {@link Line#context()}.
     *                                     Compare their {@link Line#index()} instead.
     * @param addition                     The new {@link Line} of the {@code linesOfGroup} and {@link Constraint#lines()}.
     * @param children                     These are all sub {@link Constraint}s, to which the {@code linesOfGroup} can be propagated to.
     *                                     A classic implementation to propagate all complying lines to all {@code children}.
     *                                     See {@link Constraint#childrenView()}.
     * @param lineProcessingBeforeAddition This is the {@link Constraint#lineProcessing()} of the incoming group before the addition.
     * @return
     */
    RatingEvent ratingAfterAddition(View linesOfGroup, Line addition, List<Constraint> children, View lineProcessingBeforeAddition);

    /**
     * <p>Calculate the required {@link Rating} update caused
     * by the removal of a {@link Line} from {@link Constraint#lineProcessing()}.
     * The {@link RatingEvent} is only complete,
     * if {@link #ratingAfterRemoval(View, List, View)} is considered as well.</p>
     * <p>Nothing needs to be done here, if the {@link Rating} of one {@link Line} is not dependent on the rating of another line.
     * {@link RaterBasedOnLineValue} can be used for constructing such {@link Rater}s.</p>
     *
     * @param lines                       These are all {@link Constraint#lines()} of the incoming group present before the removal.
     * @param removal                     This is the line of {@link Constraint#lines()} and {@code linesOfGroup} that will be removed.
     * @param children                    These are the children {@link Constraint}s of the current {@link Constraint} node.
     * @param lineProcessingBeforeRemoval This is the {@link Constraint#lineProcessing()} of the incoming group before the removal.
     * @return The events needed to update the {@link Rating} of all lines.
     * A {@link Rating} update for the {@code removal} argument is not required,
     * because its {@link Rating} will be automatically removed from the {@link Constraint} during the actual removal.
     * The result should never contradict {@link #ratingAfterRemoval(View, List, View)}.
     */
    RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View lineProcessingBeforeRemoval);

    /**
     * <p>This method combined with {@link #rating_before_removal(View, Line, List, View)} updates the {@link Rating} of all lines.
     * This is a helper method, that enables one to created simplified {@link Rater} implementations.</p>
     * <p>Nothing needs to be done here, if the {@link Rating} of one {@link Line} is not dependent on the rating of another line.
     * {@link RaterBasedOnLineValue} can be used for constructing such {@link Rater}s.</p>
     *
     * @param lines          These are all {@link Constraint#lines()} of the incoming group present after the removal.
     * @param children       These are the children {@link Constraint}s of the current {@link Constraint} node.
     * @param lineProcessing This is the {@link Constraint#lineProcessing()} of the incoming group after the removal.
     * @return The events needed to update the {@link Rating} of the remaining lines,
     * that were not updated by {@link #rating_before_removal(View, Line, List, View)}.
     * The result should never contradict {@link #rating_before_removal(View, Line, List, View)}.
     */
    default RatingEvent ratingAfterRemoval(View lines, List<Constraint> children, View lineProcessing) {
        return ratingEvent();
    }

    @Override
    default Class<? extends Rater> type() {
        return Rater.class;
    }

    @Override
    default Tree toTree() {
        final var perspective = tree(getClass().getSimpleName());
        if (!arguments().isEmpty()) {
            perspective.withChildren(arguments().stream().map(Domable::toTree));
        }
        return perspective;
    }

    /**
     * Describes to the user in a natural way, how this is rating given {@link Line}.
     *
     * @param line                 This is the {@link Line}, that needs to be described and is part of {@link Constraint#lines()}.
     * @param groupsLineProcessing This is part of the {@link Constraint#lineProcessing()}
     *                             for the incoming {@link GroupId} if the line.
     * @param incomingGroup        This is the incoming {@link GroupId} of the line.
     * @return Prefer using one complete sentence for the description,
     * that starts with lower case and does not end with a dot.
     */
    String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup);

    default Set<List<String>> paths() {
        return setOfUniques();
    }

    @Override
    default void addContext(Discoverable context) {

    }

    default String name() {
        return getClass().getSimpleName();
    }

    /**
     * 
     * @return Returns a description on the {@link Rater}, that is used for {@link Constraint#path()}.
     * Only use upper and lower case characters, numbers and hyphens.
     * Avoid whitespaces.
     * Never return a constraint type as the prefix of this name,
     * even though the {@link Rater} is only useful for 1 constraint type,
     * as other code assumes this in order to enable nice constraint tree renderings.
     */
    default String descriptivePathName() {
        return name();
    }

    /**
     * <p>Initializes the {@link Rater}, so it can be used,
     * which is not always the case before that,
     * depending on the {@link Rater} instance in question.
     * This is called before the rated {@link Solution} is being optimized.</p>
     * <p>By default most {@link Rater} do not need to do anything.
     * When writing a new {@link Rater},
     * one will know,
     * when an actual implementation is required.
     * If it's unknown,
     * if an actual implementation is required,
     * one is probably not required.</p>
     *
     * @param solution
     */
    default void init(Solution solution) {
    }

    /**
     * By default, {@link Proposal}s are not processed.
     *
     * @param proposal Already present proposal.
     * @return Adjustment to the proposal, so that the given proposal is compliant with this {@link Rater}.
     */
    default Proposal propose(Proposal proposal) {
        if (StaticFlags.WARNING) {
            logs().append(getClass() + " does not implement `Rater#propose(Proposal)`.", LogLevel.WARNING);
        }
        return proposal;
    }
}
