package net.splitcells.gel.constraint.type;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.type.framework.ConstraintBasedOnLocalGroupsAI;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.rater.Rater;

public class Then extends ConstraintBasedOnLocalGroupsAI {

    public static Then tad(Rater vērtētājs) {
        return new Then(vērtētājs);
    }

    public static Then tad(Rating novērtējums) {
        return new Then(constantRater(novērtējums));
    }

    protected Then(Rater vērtētājs) {
        super(vērtētājs, vērtētājs.getClass().getSimpleName());
    }

    @Override
    public Class<? extends Constraint> type() {
        return Then.class;
    }

    @Override
    protected List<String> vietēijaDabiskaArgumentācija(Report ziņojums) {
        return list(vērtētājs.toSimpleDescription(ziņojums.rinda(), ziņojums.grupa()));
    }
}
