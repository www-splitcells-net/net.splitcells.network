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
package net.splitcells.gel.rating.rater.classification;

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.common.Language.FOR_ALL;
import static net.splitcells.gel.constraint.GroupId.group;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

@Deprecated
public class ForAllAttributeValues implements Rater {
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
            (Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
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
            (Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public List<Domable> arguments() {
        return list(attribute);
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        final var argumentation = Xml.elementWithChildren(FOR_ALL.value());
        argumentation.appendChild(
                Xml.elementWithChildren("words"
                        , Xml.textNode(getClass().getSimpleName())));
        argumentation.appendChild(attribute.toDom());
        return argumentation;
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return attribute.name();
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
}