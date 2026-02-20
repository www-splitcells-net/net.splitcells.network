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
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

public class ForAllValueCombinations implements Rater {

    public static final String FOR_ALL_VALUE_COMBINATIONS_NAME = "forAllCombinationsOf";

    public static ForAllValueCombinations forAllValueCombinations(final Attribute<?>... attributes) {
        return new ForAllValueCombinations(listWithValuesOf(attributes));
    }

    public static ForAllValueCombinations forAllValueCombinations(final List<? extends Attribute<?>> attributes) {
        return new ForAllValueCombinations(attributes);
    }

    /**
     * incoming group -> value of attribute -> resulting group
     */
    private final Map<GroupId, Map<List<Object>, GroupId>> grouping = map();
    private final List<Attribute<?>> attributes = list();
    private final List<Discoverable> contexts = list();

    private ForAllValueCombinations(final List<? extends Attribute<?>> attributes) {
        this.attributes.addAll(attributes);
    }

    public List<Attribute<?>> attributes() {
        return listWithValuesOf(attributes);
    }

    @Override
    public RatingEvent ratingAfterAddition
            (View lines, Line addition, List<Constraint> children, View ratingsBeforeAddition) {
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
    public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View ratingsBeforeRemoval) {
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
    public Tree toTree() {
        return tree(FOR_ALL_VALUE_COMBINATIONS_NAME).withChildren(listWithValuesOf(attributes.mapped(Domable::toTree)));
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return "combinations of "
                + attributes
                .stream()
                .map(a -> a.name() + " = " + line.value(a))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    @Override public String descriptivePathName() {
        return "value-combinations-"
                + attributes.stream()
                .map(Attribute::name)
                .reduce((a, b) -> a + "-" + b)
                .orElse("empty");
    }

}
