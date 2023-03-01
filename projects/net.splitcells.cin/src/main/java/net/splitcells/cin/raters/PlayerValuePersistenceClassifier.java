package net.splitcells.cin.raters;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;

import java.util.function.BiPredicate;

import static net.splitcells.dem.data.order.Comparator.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineGroup.groupRouter;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class PlayerValuePersistenceClassifier {

    public static Rater playerValuePersistenceClassifier(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate
            , BiPredicate<Integer, Integer> classifier
            , String name) {
        return groupRouter((lines, children) -> {
            final var ratingEvent = ratingEvent();
            final var lineValues = lines.columnView(LINE).values();
            final var timeValues = lineValues
                    .stream()
                    .map(l -> l.value(timeAttribute))
                    .distinct()
                    .sorted(ASCENDING_INTEGERS)
                    .collect(toList());
            final var startTime = timeValues.get(0);
            final var incomingConstraintGroup = lines.lines().get(0).value(INCOMING_CONSTRAINT_GROUP);
            final var centerXPosition = incomingConstraintGroup.metaData().value(PositionClustersCenterX.class);
            final var centerYPosition = incomingConstraintGroup.metaData().value(PositionClustersCenterY.class);
            final var centerStartPosition = lineValues.stream()
                    .filter(l -> l.value(timeAttribute).equals(startTime))
                    .filter(l -> l.value(xCoordinate).equals(centerXPosition))
                    .filter(l -> l.value(yCoordinate).equals(centerYPosition))
                    .findFirst();
            if (centerStartPosition.isEmpty()) {
                lines.linesStream().forEach(line -> ratingEvent.updateRating_withReplacement(line
                        , localRating()
                                .withPropagationTo(list())
                                .withRating(noCost())
                                .withResultingGroupId(incomingConstraintGroup)));
                return ratingEvent;
            }
            final var centerEndPosition = lineValues
                    .stream()
                    .filter(l -> l.value(timeAttribute).equals(startTime + 1))
                    .filter(l -> l.value(xCoordinate).equals(centerXPosition))
                    .filter(l -> l.value(yCoordinate).equals(centerYPosition))
                    .findFirst();
            final var startPlayer = centerStartPosition.get().value(playerAttribute);
            if (startPlayer != playerValue) {
                lines.linesStream()
                        .forEach(line -> ratingEvent.updateRating_withReplacement(line
                                , localRating()
                                        .withPropagationTo(list())
                                        .withRating(noCost())
                                        .withResultingGroupId(incomingConstraintGroup)));
                return ratingEvent;
            }
            if (centerEndPosition.isEmpty()) {
                lines.linesStream().forEach(line -> ratingEvent.updateRating_withReplacement(line
                        , localRating()
                                .withPropagationTo(children)
                                .withRating(noCost())
                                .withResultingGroupId(incomingConstraintGroup)));
                return ratingEvent;
            }
            final var endPlayer = centerEndPosition.get().value(playerAttribute);
            if (classifier.test(playerValue, endPlayer)) {
                lines.linesStream()
                        .forEach(line -> ratingEvent.updateRating_withReplacement(line
                                , localRating()
                                        .withPropagationTo(children)
                                        .withRating(noCost())
                                        .withResultingGroupId(incomingConstraintGroup)));
            } else {
                lines.linesStream()
                        .forEach(line -> ratingEvent.updateRating_withReplacement(line
                                , localRating()
                                        .withPropagationTo(list())
                                        .withRating(cost(1))
                                        .withResultingGroupId(incomingConstraintGroup)));
            }
            return ratingEvent;
        }, name);
    }

    private PlayerValuePersistenceClassifier() {
        throw constructorIllegal();
    }
}
