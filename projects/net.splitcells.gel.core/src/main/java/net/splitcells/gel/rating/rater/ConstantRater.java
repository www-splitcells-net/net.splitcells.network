package net.splitcells.gel.rating.rater;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.structure.Rating;
import org.w3c.dom.Node;

public class ConstantRater implements Rater {
    public static Rater constantRater(Rating rating) {
        return new ConstantRater(rating);
    }

    private final Rating rating;
    private final List<Discoverable> contexts = list();

    protected ConstantRater(Rating rating) {
        this.rating = rating;
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children
            , Table ratingsBeforeAddition) {
        final var ratingEvent = ratingEvent();
        ratingEvent.additions().put(
                addition
                , localRating()
                        .withPropagationTo(children)
                        .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP))
                        .withRating(rating));
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children
            , Table ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public Class<? extends Rater> type() {
        return ConstantRater.class;
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        final var argumentation = Xml.elementWithChildren("constant-ratings");
        argumentation.appendChild(rating.toDom());
        return argumentation;
    }

    @Override
    public List<Domable> arguments() {
        return list(rating);
    }

    @Override
    public void addContext(Discoverable context) {
        contexts.add(context);
    }

    @Override
    public Collection<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Element toDom() {
        final var dom = Xml.elementWithChildren(getClass().getSimpleName());
        dom.appendChild(rating.toDom());
        return dom;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
