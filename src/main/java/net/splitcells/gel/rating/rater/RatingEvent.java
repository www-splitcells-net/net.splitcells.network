package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.gel.rating.type.Cost.bezMaksas;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;
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

    Map<Line, LocalRating> papildinājumi();

    Set<Line> noņemšana();

    default void pieliktNovērtējumu_caurPapildinājumu(Line priekjšmets, Rating papilduNovērtējums, List<Constraint> bērni,
                                                      Optional<Rating> novērtejumsPirmsPapildinājumu) {
        final Rating momentānsNovērtējums;
        if (papildinājumi().containsKey(priekjšmets)) {
            momentānsNovērtējums = papildinājumi().get(priekjšmets).novērtējums();
        } else {
            momentānsNovērtējums = novērtejumsPirmsPapildinājumu.orElse(bezMaksas());
        }
        papildinājumi().put
                (priekjšmets
                        , lokalsNovērtejums()
                                .arIzdalīšanaUz(bērni)
                                .arNovērtējumu(momentānsNovērtējums.kombinē(papilduNovērtējums))
                                .arRadītuGrupasId(priekjšmets.value(Constraint.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
    }

    default void updateRating_viaAddition(Line priekšmets, Rating papilduNovērtējums, List<Constraint> bērni,
                                          Optional<Rating> novērtējumsPirmsPapildinājumu) {
        final Rating currentNovērtējums;
        if (papildinājumi().containsKey(priekšmets)) {
            currentNovērtējums = papildinājumi().get(priekšmets).novērtējums();
        } else {
            currentNovērtējums = novērtējumsPirmsPapildinājumu.orElse(bezMaksas());
        }
        atjaunaNovērtējumu_caurAizvietošana(priekšmets
                , lokalsNovērtejums()
                        .arIzdalīšanaUz(bērni)
                        .arNovērtējumu(currentNovērtējums.kombinē(papilduNovērtējums))
                        .arRadītuGrupasId(priekšmets.value(Constraint.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
    }

    default void atjaunaNovērtējumu_caurAizvietošana(Line priekšmets, LocalRating jaunsNovērtējums) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(papildinājumi().keySet()).doesNotContain(priekšmets);
            assertThat(noņemšana()).doesNotContain(priekšmets);
            {
                Assertions.assertThat(priekšmets.value(Constraint.RINDA)).isNotNull();
                Assertions.assertThat(priekšmets.value(Constraint.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)).isNotNull();
            }
        }
        noņemšana().add(priekšmets);
        papildinājumi().put(priekšmets, jaunsNovērtējums);
    }
}
