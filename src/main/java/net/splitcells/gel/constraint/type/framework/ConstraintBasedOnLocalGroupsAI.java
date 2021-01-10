package net.splitcells.gel.constraint.type.framework;

import static net.splitcells.dem.data.set.list.Lists.list;

import java.util.function.Function;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

@Deprecated
public abstract class ConstraintBasedOnLocalGroupsAI extends ConstraintAI {
    protected final Rater vērtētājs;

    protected ConstraintBasedOnLocalGroupsAI(Function<Constraint, Rater> vērtētājuRažotajs) {
        super(Constraint.standartaGrupa());
        vērtētājs = vērtētājuRažotajs.apply(this);
    }

    protected ConstraintBasedOnLocalGroupsAI(Rater vērtētājs, String vārds) {
        this(Constraint.standartaGrupa(), vērtētājs, vārds);
    }

    protected ConstraintBasedOnLocalGroupsAI(Rater vērtētājs) {
        this(Constraint.standartaGrupa(), vērtētājs, "");
    }

    protected ConstraintBasedOnLocalGroupsAI(GroupId standartaGrupa, Rater vērtētājs, String vārds) {
        super(standartaGrupa, vārds);
        this.vērtētājs = vērtētājs;
    }

    @Override
    public void apstrāde_rindu_papildinajumu(Line papildinājums) {
        final var ienākošaGrupa = papildinājums.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID);
        apstrādeNovērtējumiNotikumu(
                vērtētājs.vērtē_pēc_papildinājumu(
                        rindas.columnView(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(ienākošaGrupa)
                        , papildinājums
                        , bērni
                        , rindasApstrāde
                                .columnView(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(ienākošaGrupa)));
    }

    protected void apstrādeNovērtējumiNotikumu(RatingEvent novērtējumsNotikums) {
        novērtējumsNotikums.noņemšana().forEach(noņemšana ->
                rindasApstrāde.allocations_of_demand(noņemšana).forEach(rindasApstrāde::remove));
        novērtējumsNotikums.papildinājumi().forEach((line, resultUpdate) -> {
            final var r = pieliktRadījums(resultUpdate);
            int i = r.index();
            rindasApstrāde.allocate(line, r);
        });
    }

    @Override
    protected void apstrāda_rindas_primsNoņemšana(GroupId ienākošaGrupaId, Line noņemšana) {
        apstrādeNovērtējumiNotikumu(
                vērtētājs.vērtē_pirms_noņemšana(
                        rindas.columnView(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID).uzmeklēšana(ienākošaGrupaId)
                        , rindas.columnView(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(ienākošaGrupaId)
                                .columnView(RINDA)
                                .uzmeklēšana(noņemšana)
                                .gūtRinda(0)
                        , bērni
                        , rindasApstrāde.columnView(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID).uzmeklēšana(ienākošaGrupaId)));
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