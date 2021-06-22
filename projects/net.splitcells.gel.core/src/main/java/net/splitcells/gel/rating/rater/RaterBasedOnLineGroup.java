package net.splitcells.gel.rating.rater;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

import java.util.Collection;
import java.util.Optional;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

public class RaterBasedOnLineGroup implements Rater {

    public static RaterBasedOnLineGroup groupRater(GroupRater rater) {
        return raterBasedOnLineGroup((lines, addition, removal, children) -> {
            final var lineRating = rater.lineRating(lines, addition, removal);
            final var ratingEvent = ratingEvent();
            lines.getLines().stream()
                    .filter(e -> addition.map(line -> e.index() != line.index()).orElse(true))
                    .filter(e -> removal.map(line -> e.index() != line.index()).orElse(true))
                    .forEach(e -> ratingEvent.updateRating_withReplacement(e
                            , localRating()
                                    .withPropagationTo(children)
                                    .withRating(lineRating)
                                    .withResultingGroupId
                                            (e.value(Constraint.INCOMING_CONSTRAINT_GROUP))));
            addition.ifPresent(line -> ratingEvent.additions()
                    .put(line
                            , localRating()
                                    .withPropagationTo(children)
                                    .withRating(lineRating)
                                    .withResultingGroupId
                                            (line.value(Constraint.INCOMING_CONSTRAINT_GROUP))));
            return ratingEvent;
        });
    }

    public static RaterBasedOnLineGroup raterBasedOnLineGroup(RaterBasedOnLineGroupLambda rater) {
        return new RaterBasedOnLineGroup(rater);
    }

    private final RaterBasedOnLineGroupLambda rater;

    private RaterBasedOnLineGroup(RaterBasedOnLineGroupLambda rater) {
        this.rater = rater;
    }

    @Override
    public Collection<List<String>> paths() {
        throw notImplementedYet();
    }

    @Override
    public void addContext(Discoverable context) {
        throw notImplementedYet();
    }

    @Override
    public List<Domable> arguments() {
        throw notImplementedYet();
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        return rater.rating(lines, Optional.of(addition), Optional.empty(), children);
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return rater.rating(lines, Optional.empty(), Optional.of(removal), children);
    }
}
