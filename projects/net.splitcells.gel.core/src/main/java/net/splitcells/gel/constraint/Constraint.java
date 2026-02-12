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
package net.splitcells.gel.constraint;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.CommonMarkUtils.joinDocuments;
import static net.splitcells.dem.lang.namespace.NameSpaces.HTML;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Files.createDirectory;
import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.data.view.attribute.ListAttribute.listAttribute;
import static net.splitcells.gel.rating.framework.MetaRating.neutral;
import static net.splitcells.gel.rating.type.Cost.noCost;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.TreeI;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.data.table.TableSynchronization;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.solution.Solution;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.reflection.PubliclyConstructed;
import net.splitcells.dem.utils.reflection.PubliclyTyped;
import net.splitcells.gel.constraint.intermediate.data.AllocationSelector;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;

/**
 * <p>TODO Use constraint system for database queries regularly.</p>
 * <p>TODO Create non incremental constraint system as an alternative and double check.</p>
 * <p>TODO Use  parallelization.</p>
 * <p>TODO IDEA Render constraint tree to HTML.</p>
 * <p>TODO IDEA Combine the constraint system with theorem proofing.</p>
 * <p>TODO Implement {@link Constraint} graph as a table for better external compatibility and as a basis for side
 * effect free constraint tree (this is only required for side effect freedom in case of loops).</p>
 * <p>TODO IDEA Create performance analyser for {@link Constraint} tree.
 * For instance, this could log and check, how many values are recalculated in the tree,
 * for a new allocation or a removal of such on average.
 * </p>
 */
public interface Constraint extends TableSynchronization, ConstraintWriter, Discoverable, PubliclyTyped<Constraint>, PubliclyConstructed<Domable>, Domable {
    Attribute<Line> LINE = attribute(Line.class, "line");
    Attribute<List<Constraint>> PROPAGATION_TO = listAttribute(Constraint.class, "propagation to");
    Attribute<GroupId> INCOMING_CONSTRAINT_GROUP = attribute(GroupId.class, "incoming constraint group");
    Attribute<GroupId> RESULTING_CONSTRAINT_GROUP = attribute(GroupId.class, "resulting constraint group");
    Attribute<Rating> RATING = attribute(Rating.class, "rating");

    static List<List<Constraint>> allocationGroups(List<Constraint> currentPath) {
        final var constraint = currentPath.lastValue().orElseThrow();
        final List<List<Constraint>> allocationGroups = list();
        allocationGroups.add(currentPath);
        allocationGroups.addAll(
                constraint.childrenView().stream()
                        .map(child -> allocationGroups(listWithValuesOf(currentPath).withAppended(child)))
                        .reduce((a, b) -> a.withAppended(b))
                        .orElseGet(() -> list()));
        return allocationGroups;
    }

    /**
     * TODO Make this a normal method, so its easier to find.
     *
     * @param constraint
     * @return
     */
    @Deprecated
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

    default Rating rating() {
        return rating(injectionGroup());
    }

    static GroupId standardGroup() {
        return GroupId.rootGroup("for-all");
    }

    default Rating rating(Line line) {
        return rating(injectionGroup(), line);
    }

    Rating rating(GroupId group, Line line);

    Rating rating(GroupId group);

    /**
     * <p>TODO Simplify resulting {@link Tree} by avoiding empty {@link Tree} nodes.</p>
     * <p>TODO Make the resulting text more natural.</p>
     *
     * @return
     */
    default Optional<Tree> naturalArgumentation() {
        return naturalArgumentation(injectionGroup());
    }

    default String commonMarkRatingReport() {
        final var description = StringUtils.stringBuilder();
        joinDocuments(description, "# Constraint Rating Report");
        joinDocuments(description, "## Description");
        joinDocuments(description, rating().descriptionForUser());
        joinDocuments(description, "## Argumentation");
        joinDocuments(description, naturalArgumentation()
                .map(Tree::toCommonMarkString)
                .orElseGet(() -> {
                    if (rating().equalz(noCost())) {
                        return "There is no argumentation, as there are no errors in the solution, that need reasoning.";
                    }
                    return "No Argumentation is available.";
                }));
        return description.toString();
    }

    Optional<Tree> naturalArgumentation(GroupId group);

    Optional<Discoverable> mainContext();

    default Optional<Tree> naturalArgumentation(Line subject, GroupId group) {
        return naturalArgumentation(subject, group, AllocationSelector::selectLinesWithCost);
    }

    Optional<Tree> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector);

    default Rating rating(Set<GroupId> groups) {
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

    /**
     * TODO Should this not return `List&lt;ConstraintView&gt;`?
     *
     * @return List Of Child Constraints
     */
    List<Constraint> childrenView();

    default Constraint child(int index) {
        return childrenView().get(index);
    }

    Set<Line> complying(GroupId group);

    default Set<Line> complying() {
        return complying(injectionGroup());
    }

    net.splitcells.dem.data.set.Set<Line> defying(GroupId group);

    default Set<Line> defying() {
        return defying(injectionGroup());
    }

    Line addResult(LocalRating localRating);

    /**
     * @deprecated Write access should only be given in {@link ConstraintWriter}.
     */
    @Deprecated
    default Query query() {
        return QueryI.query(this);
    }

    /**
     * @return Provides a read-only {@link Query} interface.
     */
    default Query readQuery() {
        return QueryI.query(this, false);
    }

    View lineProcessing();

    Tree toPerspective(Set<GroupId> groups);

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
                .unorderedLines()
                .stream()
                .map(lineResult -> lineResult.value(RESULTING_CONSTRAINT_GROUP))
                .collect(toSetOfUniques());
    }

    default Tree graph() {
        final var graph = tree(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> graph.withChild(arg.toTree()));
        }
        childrenView().forEach(child -> {
            graph.withChild(child.graph());
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

    /**
     * Contains all processed {@link Line} and their corresponding {@link #INCOMING_CONSTRAINT_GROUP}.
     *
     * @return
     */
    View lines();

    /**
     * TODO Is this needed anymore?
     * <p>
     * Removes all {@link #lineProcessing()} from {@link #childrenView()} and gives them back again.
     * This in turn is the same, as recalculating the {@link Rating} of all {@link Line} in the sub tree of the {@link Constraint} tree.
     * Note, that no recalculation of {@link Rating} is done on {@code this}.
     * <p>
     * This is useful, if a new node is added to the {@link Constraint} tree,
     * while some {@link Line} are already processed in the {@link Constraint} tree.
     * See {@link Query}.
     */
    @Deprecated
    default void recalculatePropagation() {
        lineProcessing().unorderedLinesStream().forEach(l -> {
            l.value(PROPAGATION_TO).forEach(c -> c.registerBeforeRemoval(l.value(RESULTING_CONSTRAINT_GROUP), l.value(LINE)));
        });
        lineProcessing().unorderedLinesStream().forEach(l -> {
            l.value(PROPAGATION_TO).forEach(c -> c.registerAdditions(l.value(RESULTING_CONSTRAINT_GROUP), l.value(LINE)));
        });
    }

    /**
     * Removes all registered {@link Line} from {@link #lineProcessing()} and adds them back to this {@link Constraint}.
     * Thereby everything is recalculated.
     * The removals and additions made during the recalculation are also propagated to {@link #childrenView()}.
     * <p>
     * This is useful, if a new {@link Constraint} node is added to the {@link Constraint} tree,
     * while some {@link Line} are already processed in the {@link Constraint} tree.
     * See {@link Query} for a use case.
     */
    default void recalculateProcessing() {
        final Map<GroupId, List<Line>> registeredAdditions = map();
        lineProcessing().unorderedLinesStream().forEach(l -> {
            final var incomingConstraintGroup = l.value(INCOMING_CONSTRAINT_GROUP);
            registeredAdditions.computeIfAbsent(incomingConstraintGroup, i -> list()).add(l.value(LINE));
        });
        registeredAdditions.entrySet().forEach(e -> e.getValue().forEach(l -> registerBeforeRemoval(e.getKey(), l)));
        registeredAdditions.entrySet().forEach(e -> e.getValue().forEach(l -> registerAdditions(e.getKey(), l)));
    }

    default Tree renderToHtml() {
        final var html = TreeI.tree("ol", HTML);
        html.withChild(renderCurrentNodeToHtml());
        html.withChildren(childrenView().stream().map(Constraint::renderToHtml));
        return html;
    }

    default Tree renderCurrentNodeToHtml() {
        final var html = TreeI.tree("li", HTML);
        html.withChild(TreeI.tree(toString(), STRING));
        return html;
    }

    /**
     * <p>Initializes the {@link Constraint} and its {@link #childrenView()}, so it can be used,
     * which is not always the case before that,
     * depending on the {@link Constraint} instance in question.
     * This is called before the rated {@link Solution} is being optimized.</p>
     *
     * @param solution
     */
    void init(Solution solution);

    /**
     * By default, {@link Proposal}s are not processed.
     *
     * @param proposal Already present proposal.
     * @return Adjustment to the proposal, so that the given proposal is compliant with this {@link Constraint}.
     */
    Proposal propose(Proposal proposal);

}
