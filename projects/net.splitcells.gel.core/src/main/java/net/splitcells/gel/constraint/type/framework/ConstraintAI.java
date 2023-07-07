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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.namespace.NameSpaces.GEL;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.resource.communication.log.LogLevel.DEBUG;
import static net.splitcells.gel.common.Language.ARGUMENTATION;
import static net.splitcells.gel.common.Language.EMPTY_STRING;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.constraint.intermediate.data.AllocationRating.lineRating;
import static net.splitcells.gel.constraint.Report.report;
import static net.splitcells.gel.constraint.intermediate.data.RoutingResult.routingResult;
import static net.splitcells.gel.data.table.attribute.IndexedAttribute.indexedAttribute;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.intermediate.data.AllocationSelector;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.constraint.intermediate.data.RoutingRating;
import net.splitcells.gel.data.table.attribute.IndexedAttribute;
import org.assertj.core.api.Assertions;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.QueryI;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;

/**
 * TODO TOFIX {@link #path()} was hacked together, without quality control.
 * Create test case for {@link #path()} and make it nice.
 */
@Deprecated
public abstract class ConstraintAI implements Constraint {
    private final GroupId injectionGroup;
    private final net.splitcells.dem.data.set.list.List<Constraint> children = list();
    protected Optional<Discoverable> mainContext = Optional.empty();
    private final List<Discoverable> contexts = list();
    protected final Database lines;
    protected final Database results;
    protected final Assignments lineProcessing;
    protected final Map<GroupId, Rating> groupProcessing = map();
    private final Class<? extends Constraint> type;

    private final IndexedAttribute<Line> lineIndex;
    private final IndexedAttribute<List<Constraint>> propagationToIndex;
    private final IndexedAttribute<GroupId> incomingConstraintGroupIndex;
    private final IndexedAttribute<GroupId> resultingConstraintGroupIndex;
    private final IndexedAttribute<Rating> ratingIndex;


    @Deprecated
    protected ConstraintAI(GroupId injectionGroup, Optional<Discoverable> parent
            , Class<? extends Constraint> type) {
        this(injectionGroup, "", parent, type);
    }

    @Deprecated
    protected ConstraintAI(GroupId injectionGroup, String name, Optional<Discoverable> parent
            , Class<? extends Constraint> type) {
        this.type = type;
        this.injectionGroup = injectionGroup;
        final Discoverable parentPath;
        if (parent.isPresent()) {
            parentPath = () -> parent.get().path().withAppended(type.getSimpleName());
            mainContext = parent;
        } else {
            parentPath = this;
        }
        results = Databases.database("results", parentPath, list(RESULTING_CONSTRAINT_GROUP, RATING, PROPAGATION_TO));
        lines = Databases.database(name + ".lines", parentPath, list(LINE, INCOMING_CONSTRAINT_GROUP));
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

    @Deprecated
    protected ConstraintAI(Optional<Discoverable> parent, Class<? extends Constraint> type) {
        this(Constraint.standardGroup(), parent, type);
    }

    @Override
    public GroupId injectionGroup() {
        return injectionGroup;
    }

    @Override
    public void registerAdditions(GroupId injectionGroup, Line addition) {
        // TODO Move this to a different project.
        if (TRACING) {
            domsole().append
                    (perspective("register-additions." + Constraint.class.getSimpleName())
                                    .withChild(perspective("additions").withChild(addition.toPerspective()))
                                    .withProperty("injectionGroup", injectionGroup.toString())
                            , this
                            , DEBUG);
        }
        // TODO Move this to a different project.
        if (ENFORCING_UNIT_CONSISTENCY) {
            // TODO The runtime performance of this check is bad, when many lines are present (i.e. > 10_000).
            assertThat(
                    lines
                            .columnView(INCOMING_CONSTRAINT_GROUP)
                            .lookup(injectionGroup)
                            .columnView(LINE)
                            .lookup(addition)
                            .unorderedLines()
            ).isEmpty();
        }
        lines.addTranslated(list(addition, injectionGroup));
    }

    @Override
    public void registerBeforeRemoval(GroupId injectionGroup, Line removal) {
        // TODO Move this to a different project.
        if (ENFORCING_UNIT_CONSISTENCY) {
            Assertions.assertThat(removal.isValid()).isTrue();
        }
        processLinesBeforeRemoval(injectionGroup, removal);
        lineProcessing.columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(injectionGroup)
                .columnView(LINE)
                .lookup(removal)
                .unorderedLinesStream()
                .forEach(lineProcessing::remove);
        lines.lookup(LINE, removal)
                .lookup(INCOMING_CONSTRAINT_GROUP, injectionGroup)
                .unorderedLinesStream()
                .forEach(lines::remove);
    }

    protected abstract void processLineAddition(Line addition);

    protected void processLinesBeforeRemoval(GroupId injectionGroup, Line removal) {
        final var suppliesToRemove = lineProcessing.suppliesFree().unorderedLinesStream().collect(toList());
        suppliesToRemove.forEach(freeSupply -> results.remove(freeSupply));
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
        asList(children).forEach(child -> {
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

    protected RoutingRating routingRating
            (GroupId group, Predicate<Line> lineSelector) {
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

    protected RoutingRating routingRating(Stream<Line> lines) {
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
    public Table lineProcessing() {
        return lineProcessing;
    }

    @Override
    public Table lines() {
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
    public Element toDom() {
        final var dom = Xml.elementWithChildren(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.appendChild(Xml.elementWithChildren(ARGUMENTATION.value(), arg.toDom())));
        }
        dom.appendChild(Xml.elementWithChildren("rating", rating().toDom()));
        {
            final var ratings = Xml.elementWithChildren("ratings");
            dom.appendChild(ratings);
            lineProcessing.columnView(INCOMING_CONSTRAINT_GROUP)
                    .lookup(injectionGroup())
                    .unorderedLines()
                    .forEach(line -> ratings.appendChild(line.toDom()));
        }
        childrenView().forEach(child ->
                dom.appendChild(
                        child.toDom(
                                setOfUniques(results
                                        .columnView(RESULTING_CONSTRAINT_GROUP)
                                        .values()))));
        return dom;
    }

    @Override
    public Perspective toPerspective() {
        final var dom = perspective(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.withProperty(ARGUMENTATION.value(), arg.toPerspective()));
        }
        dom.withProperty("rating", rating().toPerspective());
        {
            final var ratings = perspective("ratings");
            dom.withChild(ratings);
            lineProcessing.columnView(INCOMING_CONSTRAINT_GROUP)
                    .lookup(injectionGroup())
                    .unorderedLines()
                    .forEach(line -> ratings.withChild(line.toPerspective()));
        }
        childrenView().forEach(child ->
                dom.withChild(
                        child.toPerspective(
                                setOfUniques(results
                                        .columnView(RESULTING_CONSTRAINT_GROUP)
                                        .values()))));
        return dom;
    }

    public Perspective toPerspective(Set<GroupId> groups) {
        final var dom = perspective(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.withProperty(ARGUMENTATION.value(), arg.toPerspective()));
        }
        dom.withProperty("rating", rating(groups).toPerspective());
        {
            final var ratings = perspective("ratings");
            dom.withChild(ratings);
            groups.forEach(group ->
                    lineProcessing
                            .columnView(INCOMING_CONSTRAINT_GROUP)
                            .lookup(group)
                            .unorderedLines().
                            forEach(line -> ratings.withChild(line.toPerspective())));
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
                                                .collect(toSet()))
                                        .flatMap(resultingGroupings -> resultingGroupings.stream())
                                        .collect(toSetOfUniques()))));
        return dom;
    }

    @Override
    public Element toDom(Set<GroupId> groups) {
        final var dom = Xml.elementWithChildren(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.appendChild(Xml.elementWithChildren(ARGUMENTATION.value(), arg.toDom())));
        }
        dom.appendChild(Xml.elementWithChildren("rating", rating(groups).toDom()));
        {
            final var ratings = Xml.elementWithChildren("ratings");
            dom.appendChild(ratings);
            groups.forEach(group ->
                    lineProcessing
                            .columnView(INCOMING_CONSTRAINT_GROUP)
                            .lookup(group)
                            .unorderedLines().
                            forEach(line -> ratings.appendChild(line.toDom())));
        }
        childrenView().forEach(child ->
                dom.appendChild(
                        child.toDom(
                                groups.stream().
                                        map(group -> lineProcessing
                                                .columnView(INCOMING_CONSTRAINT_GROUP)
                                                .lookup(group)
                                                .unorderedLines()
                                                .stream()
                                                .map(groupLines -> groupLines.value(RESULTING_CONSTRAINT_GROUP))
                                                .collect(toSet()))
                                        .flatMap(resultingGroupings -> resultingGroupings.stream())
                                        .collect(toSetOfUniques()))));
        return dom;
    }

    protected abstract String localNaturalArgumentation(Report report);

    protected Optional<String> localNaturalArgumentation
            (Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        final var localNaturalArgumentation
                = lineProcessing
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
        return localNaturalArgumentation;
    }

    @Override
    public Optional<Perspective> naturalArgumentation(GroupId group) {
        final var naturalArgumentation = lineProcessing
                .columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(group)
                .unorderedLines()
                .stream()
                .map(allocation -> allocation.value(LINE))
                .map(line -> naturalArgumentation(line, group, AllocationSelector::selectLinesWithCost))
                .filter(Optional::isPresent)
                .collect(toList());
        if (naturalArgumentation.isEmpty()) {
            return Optional.empty();
        }
        final var localArgumentation = perspective(EMPTY_STRING.value(), GEL);
        naturalArgumentation
                .forEach(naturalReasoning -> naturalReasoning.ifPresent(localArgumentation::withChild));
        return Optional.of(localArgumentation);
    }

    @Override
    public Optional<Perspective> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        final var localArgumentation = localNaturalArgumentation(line, group, allocationSelector);
        final var childrenArgumentation = childrenArgumentation(line, group, allocationSelector);
        if (localArgumentation.isEmpty() && childrenArgumentation.isEmpty()) {
            return Optional.empty();
        } else if (!localArgumentation.isEmpty()) {
            return Optional.of(perspective(EMPTY_STRING.value(), GEL)
                    .withChild(perspective(localArgumentation.get(), NameSpaces.STRING))
                    .withChildren(childrenArgumentation));
        } else {
            return Optional.of(perspective(EMPTY_STRING.value(), GEL)
                    .withChildren(childrenArgumentation));
        }
    }

    protected List<Perspective> childrenArgumentation
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
                .filter(e -> e.isPresent())
                .map(e -> e.get())
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
