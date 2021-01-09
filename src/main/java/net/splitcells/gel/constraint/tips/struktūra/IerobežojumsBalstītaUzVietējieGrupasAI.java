package net.splitcells.gel.constraint.tips.struktūra;

import static net.splitcells.dem.data.set.list.Lists.list;

import java.util.function.Function;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

@Deprecated
public abstract class IerobežojumsBalstītaUzVietējieGrupasAI extends IerobežojumsAI {
    protected final Rater vērtētājs;

    protected IerobežojumsBalstītaUzVietējieGrupasAI(Function<Ierobežojums, Rater> vērtētājuRažotajs) {
        super(Ierobežojums.standartaGrupa());
        vērtētājs = vērtētājuRažotajs.apply(this);
    }

    protected IerobežojumsBalstītaUzVietējieGrupasAI(Rater vērtētājs, String vārds) {
        this(Ierobežojums.standartaGrupa(), vērtētājs, vārds);
    }

    protected IerobežojumsBalstītaUzVietējieGrupasAI(Rater vērtētājs) {
        this(Ierobežojums.standartaGrupa(), vērtētājs, "");
    }

    protected IerobežojumsBalstītaUzVietējieGrupasAI(GrupaId standartaGrupa, Rater vērtētājs, String vārds) {
        super(standartaGrupa, vārds);
        this.vērtētājs = vērtētājs;
    }

    @Override
    public void apstrāde_rindu_papildinajumu(Rinda papildinājums) {
        final var ienākošaGrupa = papildinājums.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID);
        apstrādeNovērtējumiNotikumu(
                vērtētājs.vērtē_pēc_papildinājumu(
                        rindas.kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(ienākošaGrupa)
                        , papildinājums
                        , bērni
                        , rindasApstrāde
                                .kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(ienākošaGrupa)));
    }

    protected void apstrādeNovērtējumiNotikumu(RatingEvent novērtējumsNotikums) {
        novērtējumsNotikums.noņemšana().forEach(noņemšana ->
                rindasApstrāde.piešķiršanas_no_prasības(noņemšana).forEach(rindasApstrāde::noņemt));
        novērtējumsNotikums.papildinājumi().forEach((line, resultUpdate) -> {
            final var r = pieliktRadījums(resultUpdate);
            int i = r.indekss();
            rindasApstrāde.piešķirt(line, r);
        });
    }

    @Override
    protected void apstrāda_rindas_primsNoņemšana(GrupaId ienākošaGrupaId, Rinda noņemšana) {
        apstrādeNovērtējumiNotikumu(
                vērtētājs.vērtē_pirms_noņemšana(
                        rindas.kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID).uzmeklēšana(ienākošaGrupaId)
                        , rindas.kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(ienākošaGrupaId)
                                .kolonnaSkats(RINDA)
                                .uzmeklēšana(noņemšana)
                                .gūtRinda(0)
                        , bērni
                        , rindasApstrāde.kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID).uzmeklēšana(ienākošaGrupaId)));
        super.apstrāda_rindas_primsNoņemšana(ienākošaGrupaId, noņemšana);
    }

    @Override
    public List<String> path() {
        return golvenaisKonteksts
                .map(konteksts -> konteksts.path())
                .orElseGet(() -> list())
                .withAppended(this.getClass().getSimpleName());
    }

    @Override
    public List<Domable> arguments() {
        return list(vērtētājs);
    }

}