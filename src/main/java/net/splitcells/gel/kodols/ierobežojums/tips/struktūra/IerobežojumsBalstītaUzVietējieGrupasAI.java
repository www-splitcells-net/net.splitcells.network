package net.splitcells.gel.kodols.ierobežojums.tips.struktūra;

import static net.splitcells.dem.data.set.list.Lists.list;

import java.util.function.Function;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.Vērtētājs;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.NovērtējumsNotikums;

@Deprecated
public abstract class IerobežojumsBalstītaUzVietējieGrupasAI extends IerobežojumsAI {
    protected final Vērtētājs vērtētājs;

    protected IerobežojumsBalstītaUzVietējieGrupasAI(Function<Ierobežojums, Vērtētājs> vērtētājuRažotajs) {
        super(Ierobežojums.standartaGrupa());
        vērtētājs = vērtētājuRažotajs.apply(this);
    }

    protected IerobežojumsBalstītaUzVietējieGrupasAI(Vērtētājs vērtētājs, String vārds) {
        this(Ierobežojums.standartaGrupa(), vērtētājs, vārds);
    }

    protected IerobežojumsBalstītaUzVietējieGrupasAI(Vērtētājs vērtētājs) {
        this(Ierobežojums.standartaGrupa(), vērtētājs, "");
    }

    protected IerobežojumsBalstītaUzVietējieGrupasAI(GrupaId standartaGrupa, Vērtētājs vērtētājs, String vārds) {
        super(standartaGrupa, vārds);
        this.vērtētājs = vērtētājs;
    }

    @Override
    public void apstrāde_rindu_papildinajumu(Rinda papildinājums) {
        final var ienākošaGrupa = papildinājums.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID);
        apstrādeNovērtējumiNotikums(
                vērtētājs.vērtē_pēc_padildinājumu(
                        rindas.kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(ienākošaGrupa)
                        , papildinājums
                        , bērni
                        , rindasApstrāde
                                .kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(ienākošaGrupa)));
    }

    protected void apstrādeNovērtējumiNotikums(NovērtējumsNotikums novērtējumsNotikums) {
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
        apstrādeNovērtējumiNotikums(
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
