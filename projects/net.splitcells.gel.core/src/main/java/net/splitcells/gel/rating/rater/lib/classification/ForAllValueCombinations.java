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
package net.splitcells.gel.rating.rater.lib.classification;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.TreeI.perspective;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.perspective.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

public class ForAllValueCombinations implements Rater {

    public static final String FOR_ALL_VALUE_COMBINATIONS_NAME = "forAllCombinationsOf";

    public static ForAllValueCombinations forAllValueCombinations(final Attribute<?>... attributes) {
        return new ForAllValueCombinations(listWithValuesOf(attributes));
    }

    public static ForAllValueCombinations forAllValueCombinations(final List<Attribute<?>> attributes) {
        return new ForAllValueCombinations(attributes);
    }

    /**
     * incoming group -> value of attribute -> resulting group
     */
    private final Map<GroupId, Map<List<Object>, GroupId>> grouping = map();
    private final List<Attribute<?>> attributes = list();
    private final List<Discoverable> contexts = list();

    private ForAllValueCombinations(final List<Attribute<?>> attributes) {
        this.attributes.addAll(attributes);
    }

    public List<Attribute<?>> attributes() {
        return listWithValuesOf(attributes);
    }

    @Override
    public RatingEvent ratingAfterAddition
            (Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        final List<Object> groupValues = list();
        final var lineValue = addition.value(Constraint.LINE);
        attributes.forEach(e -> groupValues.add(lineValue.value(e)));
        final var incomingGroup = addition.value(Constraint.INCOMING_CONSTRAINT_GROUP);
        if (!grouping.containsKey(incomingGroup)) {
            grouping.put(incomingGroup, map());
        }
        if (!grouping.get(incomingGroup).containsKey(groupValues)) {
            grouping.get(incomingGroup).put(groupValues
                    , GroupId.group(incomingGroup
                            , groupValues.stream()
                                    .map(value -> value.toString()).reduce((a, b) -> a + "," + b)
                                    .orElse("empty")));
        }
        final var ratingEvent = ratingEvent();
        ratingEvent.additions().put(
                addition
                , localRating()
                        .withPropagationTo(children)
                        .withRating(noCost())
                        .withResultingGroupId
                                (grouping.get(incomingGroup).get(groupValues)));
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public Class<? extends Rater> type() {
        return ForAllValueCombinations.class;
    }

    @Override
    public List<Domable> arguments() {
        return listWithValuesOf(attributes.mapped(a -> (Domable) a));
    }

    @Override
    public void addContext(Discoverable contexts) {
        this.contexts.add(contexts);
    }

    @Override
    public Set<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(Sets.toSetOfUniques());
    }

    @Override
    public Tree toPerspective() {
        return perspective(FOR_ALL_VALUE_COMBINATIONS_NAME)
                .withChild(perspective("attributes").withChildren(listWithValuesOf(attributes.mapped(Domable::toPerspective))));
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return "combinations of "
                + attributes
                .stream()
                .map(a -> a.name())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
}
