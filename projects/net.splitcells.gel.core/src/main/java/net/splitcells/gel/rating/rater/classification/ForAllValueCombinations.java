package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;

import java.util.Collection;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;
import org.w3c.dom.Node;

public class ForAllValueCombinations implements Rater {
    public static ForAllValueCombinations forAllValueCombinations(final Attribute<?>... attributes) {
        return new ForAllValueCombinations(attributes);
    }

    /**
     * incoming group -> value of attribute -> resulting group
     */
    private final Map<GroupId, Map<List<Object>, GroupId>> grouping = map();
    private final List<Attribute<?>> attributes = list();
    private final List<Discoverable> contexts = list();

    private ForAllValueCombinations(final Attribute<?>... attributes) {
        for (final var atribūti : attributes) {
            this.attributes.add(atribūti);
        }
    }

    public List<Attribute<?>> attributes() {
        return Lists.listWithValuesOf(attributes);
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
                    , GroupId.group(
                            groupValues.stream()
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
    public Collection<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        final var reasoning = Xml.elementWithChildren(getClass().getSimpleName());
        {
            final var attributeDescription = Xml.elementWithChildren("attribute");
            reasoning.appendChild(attributeDescription);
            attributes.forEach(att -> attributeDescription.appendChild(att.toDom()));
        }
        return reasoning;
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
