package net.splitcells.gel.rating.rater.classification;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;
import org.w3c.dom.Node;

import java.util.Collection;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.NotImplementedYet.not_implemented_yet;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class ForAllWithCondition<T> implements Rater {

    public static <T> ForAllWithCondition<T> forAllWithCondition(Predicate<Line> condition) {
        return new ForAllWithCondition<>(condition);
    }

    private final Predicate<Line> condition;
    private final List<Discoverable> contexts = list();

    private ForAllWithCondition(Predicate<Line> condition) {
        this.condition = condition;
    }

    @Override
    public RatingEvent ratingAfterAddition
            (Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        final List<Constraint> targetChildren;
        if (condition.test(addition.value(LINE))) {
            targetChildren = children;
        } else {
            targetChildren = list();
        }
        final var ratingEvent = ratingEvent();
        ratingEvent.additions().put
                (addition
                        , localRating()
                                .withPropagationTo(targetChildren)
                                .withRating(noCost())
                                .withResultingGroupId(addition.value(INCOMING_CONSTRAINT_GROUP)));
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        final var argumentation = Xml.elementWithChildren("for-all-with-condition");
        final var attributeDescription = Xml.elementWithChildren("condition");
        argumentation.appendChild(attributeDescription);
        attributeDescription.appendChild(Xml.textNode(condition.toString()));
        return argumentation;
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
    public List<Domable> arguments() {
        return list(
                new Domable() {
                    @Override
                    public Node toDom() {
                        return Xml.textNode(condition.toString());
                    }
                }
        );
    }
}
