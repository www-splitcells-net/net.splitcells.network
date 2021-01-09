package net.splitcells.gel.constraint.tips;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.tips.struktūra.IerobežojumsBalstītaUzVietējieGrupasAI;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.rater.Rater;

public class Tad extends IerobežojumsBalstītaUzVietējieGrupasAI {

    public static Tad tad(Rater vērtētājs) {
        return new Tad(vērtētājs);
    }

    public static Tad tad(Rating novērtējums) {
        return new Tad(constantRater(novērtējums));
    }

    protected Tad(Rater vērtētājs) {
        super(vērtētājs, vērtētājs.getClass().getSimpleName());
    }

    @Override
    public Class<? extends Constraint> type() {
        return Tad.class;
    }

    @Override
    protected List<String> vietēijaDabiskaArgumentācija(Report ziņojums) {
        return list(vērtētājs.uzVienkāršuAprakstu(ziņojums.rinda(), ziņojums.grupa()));
    }
}
