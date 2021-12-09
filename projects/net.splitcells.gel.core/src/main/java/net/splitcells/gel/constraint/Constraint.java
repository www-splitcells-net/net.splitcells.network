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
package net.splitcells.gel.constraint;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.namespace.NameSpaces.GEL;
import static net.splitcells.dem.resource.Files.createDirectory;
import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.gel.common.Language.ARGUMENTATION;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.data.table.attribute.ListAttribute.listAttribute;
import static net.splitcells.gel.rating.framework.MetaRating.neutral;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.data.database.DatabaseSynchronization;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.reflection.PubliclyConstructed;
import net.splitcells.dem.utils.reflection.PubliclyTyped;
import net.splitcells.gel.constraint.intermediate.data.AllocationSelector;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.MetaRating;
import net.splitcells.gel.rating.framework.Rating;

/**
 * TODO Use constraint system for database queries regularly.
 * <p>
 * TODO Create non incremental constraint system as an alternative and double check.
 * <p>
 * TODO Use  parallelization.
 * <p>
 * IDEA Render constraint tree to HTML.
 * <p>
 * IDEA Combine the constraint system with theorem proofing.
 * <p>
 * TODO Implement {@link Constraint} graph as a table for better external compatibility and as a basis for side
 * effect free constraint tree (this is only required for side effect freedom in case of loops).
 */
public interface Constraint extends DatabaseSynchronization, ConstraintWriter, Discoverable, PubliclyTyped<Constraint>, PubliclyConstructed<Domable>, Domable {
    Attribute<Line> LINE = attribute(Line.class, "line");
    Attribute<java.util.List<Constraint>> PROPAGATION_TO = listAttribute(Constraint.class, "propagation to");
    Attribute<GroupId> INCOMING_CONSTRAINT_GROUP = attribute(GroupId.class, "incoming constraint group");
    Attribute<GroupId> RESULTING_CONSTRAINT_GROUP = attribute(GroupId.class, "resulting constraint group");
    Attribute<Rating> RATING = attribute(Rating.class, "rating");

    static List<List<Constraint>> allocationGroups(List<Constraint> currentPath) {
        final var constraint = currentPath.lastValue().get();
        final List<List<Constraint>> allocationGroups = list();
        allocationGroups.add(currentPath);
        allocationGroups.addAll(
                constraint.childrenView().stream()
                        .map(child -> allocationGroups(listWithValuesOf(currentPath).withAppended(child)))
                        .reduce((a, b) -> a.withAppended(b))
                        .orElseGet(() -> list()));
        return allocationGroups;
    }

    static List<List<Constraint>> allocationGroups(Constraint constraint) {
        return allocationGroups(list(constraint));
    }

    default int longestConstraintPathLength() {
        final var longestChildPath = this.childrenView().stream()
                .map(Constraint::longestConstraintPathLength)
                .max(Integer::compareTo);
        return longestChildPath.map(i -> i + 1).orElse(1);
    }
    
    GroupId injectionGroup();

    default MetaRating rating() {
        return rating(injectionGroup());
    }

    static GroupId standardGroup() {
        return GroupId.group("for-all");
    }

    default MetaRating rating(Line line) {
        return event(injectionGroup(), line);
    }

    MetaRating event(GroupId group, Line line);

    MetaRating rating(GroupId group);

    default Perspective naturalArgumentation() {
        return naturalArgumentation(injectionGroup());
    }

    Perspective naturalArgumentation(GroupId group);

    Optional<Discoverable> mainContext();

    default Optional<Perspective> naturalArgumentation(Line subject, GroupId group) {
        return naturalArgumentation(subject, group, AllocationSelector::selectLinesWithCost);
    }

    Optional<Perspective> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector);

    default MetaRating rating(Set<GroupId> groups) {
        return groups.stream().
                map(group -> rating(group)).
                reduce((a, b) -> a.combine(b)).
                orElseGet(() -> neutral());
    }

    default GroupId register(Line line) {
        final var injectionGroup = injectionGroup();
        registerAdditions(injectionGroup, line);
        return injectionGroup;
    }

    GroupId groupOf(Line line);

    void registerAdditions(GroupId group, Line line);

    default void registerAddition(Line line) {
        registerAdditions(injectionGroup(), line);
    }

    void registerBeforeRemoval(GroupId group, Line line);

    default void registerBeforeRemoval(Line line) {
        registerBeforeRemoval(injectionGroup(), line);
    }

    List<Constraint> childrenView();

    Set<Line> complying(GroupId group);

    default Set<Line> complying() {
        return complying(injectionGroup());
    }

    Set<Line> defying(GroupId group);

    default Set<Line> defying() {
        return defying(injectionGroup());
    }

    Line addResult(LocalRating localRating);

    default Query query() {
        return QueryI.query(this);
    }

    Table lineProcessing();

    Element toDom();

    Element toDom(Set<GroupId> groups);

    default Set<Constraint> parentOf(Constraint constraint) {
        if (equals(constraint)) {
            return setOfUniques(this);
        }
        return childrenView().stream()
                .map(child -> child.parentOf(constraint))
                .reduce((a, b) -> Sets.merge(a, b))
                .orElseGet(() -> setOfUniques());
    }

    @Deprecated
    default Set<GroupId> childGroups(Line lines, Constraint subject) {
        final Set<GroupId> childGroups = setOfUniques();
        if (equals(subject)) {
            childGroups.add(groupOf(lines));
        } else {
            childrenView().forEach(child -> childGroups.addAll(child.childGroups(lines, subject)));
        }
        return childGroups;
    }

    static Set<GroupId> incomingGroupsOfConstraintPath(List<Constraint> constraintPath) {
        return incomingGroupsOfConstraintPath(constraintPath, setOfUniques(constraintPath.get(0).injectionGroup()));
    }

    static Set<GroupId> incomingGroupsOfConstraintPath(List<Constraint> constraintPath, Set<GroupId> injectionGroups) {
        if (constraintPath.isEmpty()) {
            throw new IllegalArgumentException("No path present.");
        }
        if (constraintPath.size() == 1) {
            return injectionGroups;
        }
        final var pathHead = constraintPath.remove(0);
        return incomingGroupsOfConstraintPath
                (constraintPath
                        , injectionGroups.stream()
                                .map(pathHead::childGroups)
                                .flatMap(Collection::stream)
                                .collect(toSetOfUniques()));
    }

    default Set<GroupId> childGroups(GroupId incomingGroup) {
        return lineProcessing().columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(incomingGroup)
                .getLines()
                .stream()
                .map(lineResult -> lineResult.value(RESULTING_CONSTRAINT_GROUP))
                .collect(toSetOfUniques());
    }

    default Element graph() {
        final var graph = Xml.rElement(GEL, type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> graph.appendChild(Xml.elementWithChildren(ARGUMENTATION.value(), arg.toDom())));
        }
        childrenView().forEach(child -> {
            graph.appendChild(child.graph());
        });
        return graph;
    }

    default void persistGraphState() {
        createDirectory(environment().config().configValue(ProcessPath.class));
        writeToFile(environment().config().configValue(ProcessPath.class).resolve(path().toString() + ".fods"), lineProcessing().toFods());
        childrenView().forEach(Constraint::persistGraphState);
    }

    @Override
    default Constraint withChildren(List<Function<Query, Query>> builder) {
        builder.forEach(this::withChildren);
        return this;
    }
}
