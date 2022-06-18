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
package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import java.util.Collection;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;
import org.w3c.dom.Node;

public class GroupMultiplier implements Rater {
    public static GroupMultiplier groupMultiplier(Rater... groupers) {
        return new GroupMultiplier(groupers);
    }

    private final List<Rater> classifiers;
    protected final Map<List<GroupId>, GroupId> groupMultiplier = map();
    private final List<Discoverable> contexts = list();

    protected GroupMultiplier(Rater... classifiers) {
        this.classifiers = list(classifiers);
    }

    @Override
    public List<Domable> arguments() {
        return classifiers.mapped(classifier -> (Domable) classifier);
    }

    @Override
    public void addContext(Discoverable contexts) {
        this.contexts.add(contexts);
    }

    @Override
    public Set<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toSetOfUniques());
    }

    @Override
    public RatingEvent ratingAfterAddition
            (Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        final var ratingEvent = ratingEvent();

        List<GroupId> groupingOfAddition = listWithValuesOf(
                classifiers.stream()
                        .map(classifier -> classifier
                                .ratingAfterAddition(lines, addition, children, ratingsBeforeAddition))
                        .flatMap(classification -> classification.allAdditions().stream())
                        .map(additionRating -> additionRating.resultingConstraintGroupId())
                        .collect(toList())
        );
        groupMultiplier.computeIfAbsent(
                groupingOfAddition
                , key -> key
                        .reduced((a, b) -> GroupId.multiply(a, b))
                        .orElseGet(() -> GroupId.group()));
        ratingEvent.additions().put(
                addition
                , localRating()
                        .withPropagationTo(children)
                        .withRating(noCost())
                        .withResultingGroupId(groupMultiplier.get(groupingOfAddition)));
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal
            (Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        return Xml.textNode(getClass().getSimpleName());
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return classifiers.stream()
                .map(classifier -> classifier.toString())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
}
