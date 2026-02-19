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

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.constraint.GroupId.group;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

@Deprecated
public class ForAllAttributeValues implements Rater {
    public static final String FOR_ALL_ATTRIBUTE_VALUES = "for-all-attribute-values";

    public static ForAllAttributeValues forAllAttributeValues(final Attribute<?> attribute) {
        return new ForAllAttributeValues(attribute);
    }

    private final Attribute<?> attribute;

    private ForAllAttributeValues(final Attribute<?> attribute) {
        this.attribute = attribute;
    }

    private final Map<GroupId, Map<Object, GroupId>> group = map();
    private final List<Discoverable> contexts = list();

    public Attribute<?> attribute() {
        return attribute;
    }

    @Override
    public RatingEvent ratingAfterAddition
            (View lines, Line addition, List<Constraint> children, View ratingsBeforeAddition) {
        final var groupingValue = addition.value(Constraint.LINE).value(attribute);
        final var incomingGroup = addition.value(Constraint.INCOMING_CONSTRAINT_GROUP);
        if (!group.containsKey(incomingGroup)) {
            group.put(incomingGroup, map());
        }
        if (!group.get(incomingGroup).containsKey(groupingValue)) {
            group.get(incomingGroup).put(groupingValue, group(incomingGroup, groupingValue.toString()));
        }
        final var ratingEvent = ratingEvent();
        ratingEvent.additions().put(addition
                , localRating()
                        .withPropagationTo(children)
                        .withRating(noCost())
                        .withResultingGroupId(group.get(incomingGroup).get(groupingValue))
        );
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal
            (View lines, Line removal, List<Constraint> children, View ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public List<Domable> arguments() {
        return list(attribute);
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return "groups of " + attribute.name() + " attribute values: where value is `" + line.value(attribute) + "`";
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
    public Class<? extends Rater> type() {
        return ForAllAttributeValues.class;
    }

    @Override
    public String toString() {
        return ForAllAttributeValues.class.getSimpleName() + "-" + attribute.name();
    }

    @Override
    public Tree toTree() {
        return tree("For all attribute values").withChild(attribute.toTree());
    }

    @Override public String descriptivePathName() {
        return "for-all-values-of-" + attribute.name();
    }
}