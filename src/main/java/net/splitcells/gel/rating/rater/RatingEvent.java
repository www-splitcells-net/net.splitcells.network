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

    default void pieliktNovērtējumu_caurPapildinājumu(Line priekjšmets, Rating papilduNovērtējums, List<Constraint> bērni,
                                                      Optional<Rating> novērtejumsPirmsPapildinājumu) {
        final Rating momentānsNovērtējums;
        if (additions().containsKey(priekjšmets)) {
            momentānsNovērtējums = additions().get(priekjšmets).rating();
        } else {
            momentānsNovērtējums = novērtejumsPirmsPapildinājumu.orElse(noCost());
        }
        additions().put
                (priekjšmets
                        , localRating()
                                .withPropagationTo(bērni)
                                .withRating(momentānsNovērtējums.combine(papilduNovērtējums))
                                .withResultingGroupId(priekjšmets.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
    }

    default void updateRating_viaAddition(Line priekšmets, Rating papilduNovērtējums, List<Constraint> bērni,
                                          Optional<Rating> novērtējumsPirmsPapildinājumu) {
        final Rating currentNovērtējums;
        if (additions().containsKey(priekšmets)) {
            currentNovērtējums = additions().get(priekšmets).rating();
        } else {
            currentNovērtējums = novērtējumsPirmsPapildinājumu.orElse(noCost());
        }
        atjaunaNovērtējumu_caurAizvietošana(priekšmets
                , localRating()
                        .withPropagationTo(bērni)
                        .withRating(currentNovērtējums.combine(papilduNovērtējums))
                        .withResultingGroupId(priekšmets.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
    }

    default void atjaunaNovērtējumu_caurAizvietošana(Line priekšmets, LocalRating jaunsNovērtējums) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(additions().keySet()).doesNotContain(priekšmets);
            assertThat(removal()).doesNotContain(priekšmets);
            {
                Assertions.assertThat(priekšmets.value(Constraint.LINE)).isNotNull();
                Assertions.assertThat(priekšmets.value(Constraint.INCOMING_CONSTRAINT_GROUP)).isNotNull();
            }
        }
        removal().add(priekšmets);
        additions().put(priekšmets, jaunsNovērtējums);
    }
}
