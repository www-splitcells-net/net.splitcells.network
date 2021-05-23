package net.splitcells.gel.rating.rater;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.rating.structure.Rating;


public class HasSize implements Rater {
    public static HasSize hasSize(int targetSize) {
        return new HasSize(targetSize);
    }

    private final int targetSize;
    private final List<Discoverable> contexts = list();

    protected HasSize(int targetSize) {
        this.targetSize = targetSize;
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children
            , Table ratingsBeforeAddition) {
        final var individualRating = rating(lines, false);
        final var additionalRatings
                = rateLines(lines, addition, children, individualRating);
        additionalRatings.additions().put(addition
                , localRating()
                        .withPropagationTo(children)
                        .withRating(individualRating)
                        .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP))
        );
        return additionalRatings;
    }

    private RatingEvent rateLines(Table lines, Line changed, List<Constraint> children, Rating cost) {
        final RatingEvent linesRating = ratingEvent();
        lines.rawLinesView().stream()
                .filter(e -> e != null)
                .filter(e -> e.index() != changed.index())
                .forEach(e -> {
                    linesRating.updateRating_withReplacement(e,
                            localRating().
                                    withPropagationTo(children).
                                    withRating(cost).
                                    withResultingGroupId(changed.value(Constraint.INCOMING_CONSTRAINT_GROUP))
                    );
                });
        return linesRating;
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        final var argumentation = Xml.elementWithChildren(HasSize.class.getSimpleName());
        argumentation.appendChild(
                Xml.elementWithChildren("target-size"
                        , Xml.textNode(targetSize + "")));
        argumentation.appendChild(
                Xml.elementWithChildren("actual-size"
                        , Xml.textNode(allocations.size() + "")));
        return argumentation;
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return "size should be " + targetSize + ", but is " + groupsLineProcessing.size();
    }

    @Override
    public RatingEvent rating_before_removal
            (Table lines
                    , Line removal
                    , List<Constraint> children
                    , Table ratingsBeforeRemoval) {
        return rateLines(lines, removal, children, rating(lines, true));
    }

    private Rating rating(Table lines, boolean beforeRemoval) {
        final Rating rating;
        final int actualSize;
        if (beforeRemoval) {
            actualSize = lines.size() - 1;
        } else {
            actualSize = lines.size();
        }
        if (actualSize > 0) {
            final int difference = abs(targetSize - actualSize);
            rating = cost(difference / ((double) actualSize));
        } else {
            throw new AssertionError("negative size is: " + actualSize);
        }
        return rating;
    }

    @Override
    public Class<? extends Rater> type() {
        return HasSize.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(() -> Xml.elementWithChildren(HasSize.class.getSimpleName(), Xml.textNode("" + targetSize)));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg != null && arg instanceof HasSize) {
            return this.targetSize == ((HasSize) arg).targetSize;
        }
        return false;
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
    public String toString() {
        return getClass().getSimpleName() + ", " + targetSize;
    }
}
