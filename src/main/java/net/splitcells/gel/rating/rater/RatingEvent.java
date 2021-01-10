package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.structure.LocalRating;
import net.splitcells.gel.rating.structure.Rating;
import org.assertj.core.api.Assertions;

public interface RatingEvent {

    Map<Line, LocalRating> additions();

    Set<Line> removal();

    default void pieliktNovērtējumu_caurPapildinājumu(Line subject, Rating additionalRating, List<Constraint> children,
                                                      Optional<Rating> ratingBeforeAddtion) {
        final Rating currentRating;
        if (additions().containsKey(subject)) {
            currentRating = additions().get(subject).rating();
        } else {
            currentRating = ratingBeforeAddtion.orElse(noCost());
        }
        additions().put
                (subject
                        , localRating()
                                .withPropagationTo(children)
                                .withRating(currentRating.combine(additionalRating))
                                .withResultingGroupId(subject.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
    }

    default void updateRating_viaAddition(Line subject, Rating additionalRating, List<Constraint> children,
                                          Optional<Rating> ratingBeforeAddition) {
        final Rating currentRating;
        if (additions().containsKey(subject)) {
            currentRating = additions().get(subject).rating();
        } else {
            currentRating = ratingBeforeAddition.orElse(noCost());
        }
        updateRating_withReplacement(subject
                , localRating()
                        .withPropagationTo(children)
                        .withRating(currentRating.combine(additionalRating))
                        .withResultingGroupId(subject.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
    }

    default void updateRating_withReplacement(Line subject, LocalRating newRating) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(additions().keySet()).doesNotContain(subject);
            assertThat(removal()).doesNotContain(subject);
            {
                Assertions.assertThat(subject.value(Constraint.LINE)).isNotNull();
                Assertions.assertThat(subject.value(Constraint.INCOMING_CONSTRAINT_GROUP)).isNotNull();
            }
        }
        removal().add(subject);
        additions().put(subject, newRating);
    }
}
