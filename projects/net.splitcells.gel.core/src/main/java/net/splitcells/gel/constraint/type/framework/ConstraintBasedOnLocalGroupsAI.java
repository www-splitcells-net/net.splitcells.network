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

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.DescribedBool.describedBool;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.resource.communication.log.LogLevel.DEBUG;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.common.Language.EMPTY_STRING;
import static net.splitcells.gel.constraint.Report.report;
import static net.splitcells.gel.constraint.intermediate.data.AllocationRating.lineRating;
import static net.splitcells.gel.constraint.intermediate.data.RoutingResult.routingResult;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.view.attribute.IndexedAttribute.indexedAttribute;
import static net.splitcells.gel.rating.type.Cost.noCost;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.QueryI;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.constraint.intermediate.data.AllocationSelector;
import net.splitcells.gel.constraint.intermediate.data.RoutingRating;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.IndexedAttribute;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.solution.Solution;

/**
 * TODO TOFIX {@link #path()} was hacked together, without quality control.
 * Create test case for {@link #path()} and make it nice.
 */
public class ConstraintBasedOnLocalGroupsAI implements Constraint {
    /**
     *
     * @param rater
     * @param name
     * @param parent
     * @param localNaturalArgumentation
     * @param type
     * @return
     * @deprecated The name is not actually used.
     */
    @Deprecated public static ConstraintBasedOnLocalGroupsAI constraintBasedOnLocalGroupsAI(Rater rater, String name
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

    /**
     * @param standardGroup
     * @param rater
     * @param name
     * @param parent
     * @param localNaturalArgumentation
     * @param type
     * @return
     * @deprecated The name is not actually used.
     */
    @Deprecated public static ConstraintBasedOnLocalGroupsAI constraintBasedOnLocalGroupsAI(GroupId standardGroup, Rater rater
            , String name
            , Optional<Discoverable> parent
            , BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation
            , Class<? extends Constraint> type) {
        return new ConstraintBasedOnLocalGroupsAI(standardGroup, rater, name, parent, localNaturalArgumentation, type);
    }

    private final Rater rater;
    private final BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentor;
    private final GroupId injectionGroup;
    private final net.splitcells.dem.data.set.list.List<Constraint> children = list();
    private Optional<Discoverable> mainContext = Optional.empty();
    private final List<Discoverable> contexts = list();
    /**
     * TODO Rename this to incomingLines, in order clarify its meaning.
     */
    private final Table lines;
    private final Table results;
    private final Assignments lineProcessing;
    private final Map<GroupId, Rating> groupProcessing = map();
    private final Class<? extends Constraint> type;
    final Discoverable parentPath;

    private final IndexedAttribute<Line> lineIndex;
    private final IndexedAttribute<List<Constraint>> propagationToIndex;
    private final IndexedAttribute<GroupId> incomingConstraintGroupIndex;
    private final IndexedAttribute<GroupId> resultingConstraintGroupIndex;
    private final IndexedAttribute<Rating> ratingIndex;


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

    private String descriptiveName() {
        return type.getSimpleName() + "-" + rater.descriptivePathName();
    }

    private ConstraintBasedOnLocalGroupsAI(GroupId standardGroup, Rater rater, String name
            , Optional<Discoverable> parent
            , BiFunction<ConstraintBasedOnLocalGroupsAI, Report, String> localNaturalArgumentation
            , Class<? extends Constraint> type) {
        this.type = type;
        this.injectionGroup = standardGroup;
        this.rater = rater;
        this.localNaturalArgumentor = localNaturalArgumentation;
        if (parent.isPresent()) {
            parentPath = () -> parent.get().path().shallowCopy().withAppended(descriptiveName());
            mainContext = parent;
        } else {
            parentPath = () -> list(descriptiveName());
        }
        results = Tables.table("results", parentPath, list(RESULTING_CONSTRAINT_GROUP, RATING, PROPAGATION_TO));
        lines = Tables.table("lines", parentPath, list(LINE, INCOMING_CONSTRAINT_GROUP));
        lineProcessing = assignments("linesProcessing", lines, results);
        lineProcessing.subscribeToAfterAdditions(this::propagateAddition);
        lineProcessing.subscribeToBeforeRemoval(this::propagateRemoval);
        lines.subscribeToAfterAdditions(this::processLineAddition);
        lineIndex = indexedAttribute(LINE, lineProcessing);
        propagationToIndex = indexedAttribute(PROPAGATION_TO, lineProcessing);
        incomingConstraintGroupIndex = indexedAttribute(INCOMING_CONSTRAINT_GROUP, lineProcessing);
        resultingConstraintGroupIndex = indexedAttribute(RESULTING_CONSTRAINT_GROUP, lineProcessing);
        ratingIndex = indexedAttribute(RATING, lineProcessing);
    }

    @Override
    public void init(Solution solution) {
        rater.init(solution);
        childrenView().forEach(c -> c.init(solution));
    }

    public void processLineAddition(Line addition) {
        final var incomingGroup = addition.value(INCOMING_CONSTRAINT_GROUP);
        final var ratingEvent = rater.ratingAfterAddition(
                lines.columnView(INCOMING_CONSTRAINT_GROUP).lookup(incomingGroup)
                , addition
                , childrenView()
                , lineProcessing
                        .columnView(INCOMING_CONSTRAINT_GROUP)
                        .lookup(incomingGroup));
        processRatingEvent(ratingEvent);
        if (ENFORCING_UNIT_CONSISTENCY && lineProcessing.demandsUsed().misses(addition)) {
            throw execException(tree("The rater did not provide a rating to the added line.")
                    .withProperty("constraint", this.toTree())
                    .withProperty("rater", rater.toSimpleDescription(addition, lineProcessing, incomingGroup)));
        }
    }

    private void processRatingEvent(RatingEvent ratingEvent) {
        ratingEvent.removal().forEach(removal ->
                lineProcessing.assignmentsOfDemand(removal).forEach(lineProcessing::remove));
        ratingEvent.additions().forEach((line, resultUpdate) -> {
            final var r = addResult(resultUpdate);
            lineProcessing.assign(line, r);
        });
        ratingEvent.complexAdditions().forEach((line, updates) -> {
            updates.stream().forEach(update -> {
                final var r = addResult(update);
                lineProcessing.assign(line, r);
            });
        });
    }

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
        final var suppliesToRemove = lineProcessing.suppliesFree().unorderedLinesStream().collect(toList());
        suppliesToRemove.forEach(freeSupply -> results.remove(freeSupply));
    }

    public void processLinesAfterRemoval(GroupId incomingGroup) {
        processRatingEvent(rater.ratingAfterRemoval(
                lines.columnView(INCOMING_CONSTRAINT_GROUP).lookup(incomingGroup)
                , childrenView()
                , lineProcessing.columnView(INCOMING_CONSTRAINT_GROUP).lookup(incomingGroup)));
        final var suppliesToRemove = lineProcessing.suppliesFree().unorderedLinesStream().collect(toList());
        suppliesToRemove.forEach(freeSupply -> results.remove(freeSupply));
    }

    public String localNaturalArgumentation(Report report) {
        return localNaturalArgumentor.apply(this, report);
    }

    @Override
    public List<String> path() {
        return this.parentPath.path();
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

    @Override
    public GroupId injectionGroup() {
        return injectionGroup;
    }

    @Override
    public void registerAdditions(GroupId injectionGroup, Line addition) {
        // TODO Move this to a different project.
        if (TRACING) {
            logs().append
                    (tree("register-additions." + Constraint.class.getSimpleName())
                                    .withChild(tree("additions").withChild(addition.toTree()))
                                    .withProperty("injectionGroup", injectionGroup.toString())
                            , this
                            , DEBUG);
        }
        // TODO Move this to a different project.
        if (ENFORCING_UNIT_CONSISTENCY) {
            // TODO The runtime performance of this check is bad, when many lines are present (i.e. > 10_000).
            lines.columnView(INCOMING_CONSTRAINT_GROUP)
                    .lookup(injectionGroup)
                    .columnView(LINE)
                    .lookup(addition)
                    .unorderedLines()
                    .requireEmpty();
        }
        lines.addTranslated(list(addition, injectionGroup));
    }

    @Override
    public void registerBeforeRemoval(GroupId injectionGroup, Line removal) {
        // TODO Move this to a different project.
        if (ENFORCING_UNIT_CONSISTENCY) {
            require(removal.isValid());
        }
        processLinesBeforeRemoval(injectionGroup, removal);
        lineProcessing.columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(injectionGroup)
                .columnView(LINE)
                .lookup(removal)
                .unorderedLines() // This avoids `java.util.ConcurrentModificationException`.
                .forEach(lineProcessing::remove);
        lines.lookup(LINE, removal)
                .lookup(INCOMING_CONSTRAINT_GROUP, injectionGroup)
                .unorderedLines() // This avoids `java.util.ConcurrentModificationException`.
                .forEach(lines::remove);
        processLinesAfterRemoval(injectionGroup);
    }

    private void propagateAddition(Line addition) {
        addition.value(propagationToIndex).forEach(child ->
                child.registerAdditions
                        (addition.value(resultingConstraintGroupIndex)
                                , addition.value(lineIndex)));
    }

    private void propagateRemoval(Line removal) {
        removal.value(propagationToIndex).forEach(child ->
                child.registerBeforeRemoval
                        (removal.value(resultingConstraintGroupIndex)
                                , removal.value(lineIndex)));
    }

    @Override
    public Constraint withChildren(Constraint... children) {
        list(children).forEach(child -> {
            this.children.add(child);
            child.addContext(this);
        });
        return this;
    }

    public Set<Line> complying(GroupId group, Set<Line> allocations) {
        final Set<Line> complying = setOfUniques();
        allocations.forEach(allocation -> {
            if (rating(group, allocation.value(lineIndex)).equalz(noCost())) {
                complying.add(lineProcessing.demandOfAssignment(allocation).value(lineIndex));
            }
        });
        return complying;
    }

    public net.splitcells.dem.data.set.Set<Line> defying(GroupId group, Set<Line> allocations) {
        final net.splitcells.dem.data.set.Set<Line> defying = setOfUniques();
        allocations.forEach(allocation -> {
            if (!rating(group, allocation.value(lineIndex)).equalz(noCost())) {
                defying.add(lineProcessing.demandOfAssignment(allocation).value(lineIndex));
            }
        });
        return defying;
    }

    @Override
    public Rating rating(GroupId group, Line line) {
        final var routingRating = routingRating(lineProcessing
                .lookup(LINE, line)
                .lookup(INCOMING_CONSTRAINT_GROUP, group)
                .unorderedLinesStream());
        routingRating.children_to_groups().forEach((child, groups) ->
                groups.forEach(group2 -> routingRating.ratingComponents().add(child.rating(group2, line)))
        );
        if (routingRating.ratingComponents().isEmpty()) {
            return noCost();
        }
        return routingRating.ratingComponents().stream()
                .reduce((a, b) -> a.combine(b))
                .orElseThrow();
    }

    private RoutingRating routingRating(GroupId group, Predicate<Line> lineSelector) {
        final var routingRating = RoutingRating.create();
        lineProcessing.rawLinesView().forEach(line -> {
            if (line != null
                    && group.equals(line.value(incomingConstraintGroupIndex))
                    && lineSelector.test(line)) {
                routingRating.ratingComponents().add(line.value(ratingIndex));
                line.value(propagationToIndex).forEach(child -> {
                    final Set<GroupId> groupsOfChild;
                    if (!routingRating.children_to_groups().containsKey(child)) {
                        groupsOfChild = setOfUniques();
                        routingRating.children_to_groups().put(child, groupsOfChild);
                    } else {
                        groupsOfChild = routingRating.children_to_groups().get(child);
                    }
                    groupsOfChild.add(line.value(resultingConstraintGroupIndex));
                });
            }
        });
        return routingRating;
    }

    private RoutingRating routingRating(Stream<Line> lines) {
        final var routingRating = RoutingRating.create();
        lines.forEach(line -> {
            if (line != null) {
                routingRating.ratingComponents().add(line.value(ratingIndex));
                line.value(propagationToIndex).forEach(child -> {
                    final Set<GroupId> groupsOfChild;
                    if (!routingRating.children_to_groups().containsKey(child)) {
                        groupsOfChild = setOfUniques();
                        routingRating.children_to_groups().put(child, groupsOfChild);
                    } else {
                        groupsOfChild = routingRating.children_to_groups().get(child);
                    }
                    groupsOfChild.add(line.value(resultingConstraintGroupIndex));
                });
            }
        });
        return routingRating;
    }

    @Override
    public Rating rating(GroupId group) {
        final var routingRating
                = routingRating(group, lineSelector -> true);
        routingRating.children_to_groups().forEach((routingRhildren, groups) ->
                groups.forEach(group2 -> routingRating.ratingComponents().add(routingRhildren.rating(group2))));
        if (routingRating.ratingComponents().isEmpty()) {
            return noCost();
        }
        return routingRating.ratingComponents().stream()
                .reduce((a, b) -> a.combine(b))
                .orElseThrow();
    }

    /**
     * TODO PERFORMANCE
     */
    @Override
    public List<Constraint> childrenView() {
        return listWithValuesOf(children);
    }

    public Set<Line> allocationsOf(GroupId group) {
        final Set<Line> allocations = setOfUniques();
        lineProcessing.rawLinesView().forEach(line -> {
            if (line != null && line.value(incomingConstraintGroupIndex).equals(group)) {
                allocations.add(line);
            }
        });
        return allocations;
    }

    public Set<Line> complying(GroupId group) {
        return complying(group, allocationsOf(group));
    }

    public net.splitcells.dem.data.set.Set<Line> defying(GroupId group) {
        return defying(group, allocationsOf(group));
    }

    @Override
    public View lineProcessing() {
        return lineProcessing;
    }

    @Override
    public View lines() {
        return lines;
    }

    public GroupId groupOf(Line line) {
        return lineProcessing()
                .rawLinesView()
                .get(line.index())
                .value(resultingConstraintGroupIndex);
    }

    @Override
    public Line addResult(LocalRating localRating) {
        return results.addTranslated
                (list
                        (localRating.resultingConstraintGroupId()
                                , localRating.rating()
                                , localRating.propagateTo()));
    }

    @Override
    public void addContext(Discoverable context) {
        if (mainContext.isEmpty()) {
            mainContext = Optional.of(context);
        }
        contexts.add(context);
    }

    @Override
    public net.splitcells.dem.data.set.Set<net.splitcells.dem.data.set.list.List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toSetOfUniques());
    }

    @Override
    public Constraint withChildren(Function<Query, Query> builder) {
        builder.apply(QueryI.query(this, Optional.empty()));
        return this;
    }

    @Override
    public Tree toTree() {
        final var dom = tree(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.withChild(arg.toTree()));
        }
        dom.withProperty("rating", rating().toTree());
        {
            final var ratings = tree("ratings");
            dom.withChild(ratings);
            lineProcessing.columnView(INCOMING_CONSTRAINT_GROUP)
                    .lookup(injectionGroup())
                    .unorderedLines()
                    .forEach(line -> ratings.withChild(line.toTree()));
        }
        childrenView().forEach(child ->
                dom.withChild(
                        child.toPerspective(
                                setOfUniques(results
                                        .columnView(RESULTING_CONSTRAINT_GROUP)
                                        .values()))));
        return dom;
    }

    @Override
    public Tree toPerspective(Set<GroupId> groups) {
        final var dom = tree(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.withChild(arg.toTree()));
        }
        dom.withProperty("rating", rating(groups).toTree());
        {
            final var ratings = tree("ratings");
            dom.withChild(ratings);
            groups.forEach(group ->
                    lineProcessing
                            .columnView(INCOMING_CONSTRAINT_GROUP)
                            .lookup(group)
                            .unorderedLines().
                            forEach(line -> ratings.withChild(line.toTree())));
        }
        childrenView().forEach(child ->
                dom.withChild(
                        child.toPerspective(
                                groups.stream().
                                        map(group -> lineProcessing
                                                .columnView(INCOMING_CONSTRAINT_GROUP)
                                                .lookup(group)
                                                .unorderedLines()
                                                .stream()
                                                .map(groupLines -> groupLines.value(RESULTING_CONSTRAINT_GROUP))
                                                .collect(toSetOfUniques()))
                                        .flatMap(resultingGroupings -> resultingGroupings.stream())
                                        .collect(toSetOfUniques()))));
        return dom;
    }

    private Optional<String> localNaturalArgumentation
            (Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        return lineProcessing
                .columnView(LINE)
                .lookup(line)
                .columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(group)
                .unorderedLines()
                .stream()
                .filter(allocation -> allocationSelector
                        .test(lineRating
                                (allocation
                                        , rating(allocation.value(INCOMING_CONSTRAINT_GROUP), line))))
                .map(allocation -> report
                        (allocation.value(LINE)
                                , allocation.value(INCOMING_CONSTRAINT_GROUP)
                                , allocation.value(RATING)))
                .map(report -> localNaturalArgumentation(report))
                .findFirst();
    }

    @Override
    public Optional<Tree> naturalArgumentation(GroupId group) {
        final var naturalArgumentation = lineProcessing
                .columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(group)
                .unorderedLines()
                .stream()
                .map(allocation -> allocation.value(LINE))
                .map(line -> naturalArgumentation(line, group, AllocationSelector::selectLinesWithCost))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
        if (naturalArgumentation.isEmpty()) {
            return Optional.empty();
        }
        if (naturalArgumentation.size() == 1) {
            return Optional.of(naturalArgumentation.get(0));
        }
        final var localArgumentation = tree(EMPTY_STRING.value(), STRING);
        naturalArgumentation.forEach(localArgumentation::withMerged);
        if (localArgumentation.children().size() == 1) {
            return Optional.of(localArgumentation.child(0));
        }
        return Optional.of(localArgumentation);
    }

    @Override
    public Optional<Tree> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        final var localArgumentation = localNaturalArgumentation(line, group, allocationSelector);
        final var childrenArgumentation = childrenArgumentation(line, group, allocationSelector);
        if (localArgumentation.isEmpty() && childrenArgumentation.isEmpty()) {
            return Optional.empty();
        } else if (!localArgumentation.isEmpty()) {
            return Optional.of(tree(localArgumentation.get(), NameSpaces.STRING).withMerged(childrenArgumentation));
        } else if (childrenArgumentation.size() == 1) {
            return Optional.of(childrenArgumentation.get(0));
        } else {
            return Optional.of(tree(EMPTY_STRING.value(), STRING)
                    .withMerged(childrenArgumentation));
        }
    }

    private List<Tree> childrenArgumentation
            (Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        return lineProcessing
                .columnView(LINE)
                .lookup(line)
                .columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(group)
                .unorderedLines()
                .stream()
                .filter(allocation
                        -> allocationSelector
                        .test(lineRating
                                (allocation
                                        , rating(allocation.value(INCOMING_CONSTRAINT_GROUP), line))))
                .map(allocation -> allocation.value(PROPAGATION_TO)
                        .stream()
                        .map(propagatedTo ->
                                routingResult
                                        (allocation.value(RESULTING_CONSTRAINT_GROUP)
                                                , propagatedTo)))
                .flatMap(e -> e)
                .map(routingResult
                        -> routingResult
                        .propagation()
                        .naturalArgumentation(line, routingResult.group()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    public Optional<Discoverable> mainContext() {
        return mainContext;
    }

    @Override
    public Class<? extends Constraint> type() {
        return type;
    }
}