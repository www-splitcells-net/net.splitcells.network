package net.splitcells.gel.rating.vērtētājs;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.gel.rating.tips.Cena.bezMaksas;
import static net.splitcells.gel.rating.struktūra.VietējieNovērtējumsI.lokalsNovērtejums;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.rating.struktūra.VietējieNovērtējums;
import net.splitcells.gel.rating.struktūra.Novērtējums;
import org.assertj.core.api.Assertions;

public interface NovērtējumsNotikums {

    Map<Rinda, VietējieNovērtējums> papildinājumi();

    Set<Rinda> noņemšana();

    default void pieliktNovērtējumu_caurPapildinājumu(Rinda priekjšmets, Novērtējums papilduNovērtējums, List<Ierobežojums> bērni,
                                                      Optional<Novērtējums> novērtejumsPirmsPapildinājumu) {
        final Novērtējums momentānsNovērtējums;
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
                                .arRadītuGrupasId(priekjšmets.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
    }

    default void updateRating_viaAddition(Rinda priekšmets, Novērtējums papilduNovērtējums, List<Ierobežojums> bērni,
                                          Optional<Novērtējums> novērtējumsPirmsPapildinājumu) {
        final Novērtējums currentNovērtējums;
        if (papildinājumi().containsKey(priekšmets)) {
            currentNovērtējums = papildinājumi().get(priekšmets).novērtējums();
        } else {
            currentNovērtējums = novērtējumsPirmsPapildinājumu.orElse(bezMaksas());
        }
        atjaunaNovērtējumu_caurAizvietošana(priekšmets
                , lokalsNovērtejums()
                        .arIzdalīšanaUz(bērni)
                        .arNovērtējumu(currentNovērtējums.kombinē(papilduNovērtējums))
                        .arRadītuGrupasId(priekšmets.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
    }

    default void atjaunaNovērtējumu_caurAizvietošana(Rinda priekšmets, VietējieNovērtējums jaunsNovērtējums) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(papildinājumi().keySet()).doesNotContain(priekšmets);
            assertThat(noņemšana()).doesNotContain(priekšmets);
            {
                Assertions.assertThat(priekšmets.vērtība(Ierobežojums.RINDA)).isNotNull();
                Assertions.assertThat(priekšmets.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)).isNotNull();
            }
        }
        noņemšana().add(priekšmets);
        papildinājumi().put(priekšmets, jaunsNovērtējums);
    }
}
