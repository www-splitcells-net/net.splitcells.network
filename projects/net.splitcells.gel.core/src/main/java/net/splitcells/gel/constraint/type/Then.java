package net.splitcells.gel.constraint.type;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;

import net.splitcells.gel.constraint.type.framework.ConstraintBasedOnLocalGroupsAI;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.Rater;

public class Then extends ConstraintBasedOnLocalGroupsAI {

    public static Then then(Rater rater) {
        return new Then(rater);
    }

    public static Then then(Rating rating) {
        return new Then(constantRater(rating));
    }

    protected Then(Rater rater) {
        super(rater, rater.getClass().getSimpleName());
    }

    @Override
    public Class<? extends Constraint> type() {
        return Then.class;
    }

    @Override
    protected String localNaturalArgumentation(Report report) {
        return "Then " + rater.toSimpleDescription(report.line()
                , lineProcessing.columnView(Constraint.INCOMING_CONSTRAINT_GROUP).lookup(report.group())
                , report.group());
    }
}
