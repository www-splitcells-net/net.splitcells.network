package net.splitcells.gel.constraint.type;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.classification.RaterBasedOnGrouping.raterBasedGrouping;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.type.framework.ConstraintBasedOnLocalGroupsAI;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.classification.ForAllAttributeValues;
import net.splitcells.gel.rating.rater.classification.Propagation;
import net.splitcells.gel.rating.rater.classification.RaterBasedOnGrouping;

public class ForAll extends ConstraintBasedOnLocalGroupsAI {

    public static ForAll forAll(Rater classifier) {
        return create(classifier);
    }

    @Deprecated
    public static ForAll create(Rater classifier) {
        return new ForAll(classifier);
    }

    protected ForAll(Rater classifier) {
        super(raterBasedGrouping(classifier));
    }

    public Rater classification() {
        return rater;
    }

    @Override
    public Class<? extends Constraint> type() {
        return ForAll.class;
    }

    @Override
    protected String localNaturalArgumentation(Report report) {
        final var raterBasedOnGrouping = rater.casted(RaterBasedOnGrouping.class);
        if (raterBasedOnGrouping.isPresent()) {
            if (raterBasedOnGrouping.get().classifier().type().equals(Propagation.class)) {
                return raterBasedOnGrouping.get().classifier().toSimpleDescription(report.line()
                        , lineProcessing
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(report.group())
                        , report.group());
            }
        }
        if (rater.type().equals(ForAllAttributeValues.class)) {
            return rater.toSimpleDescription(report.line()
                    , lineProcessing.columnView(Constraint.INCOMING_CONSTRAINT_GROUP).lookup(report.group())
                    , report.group());
        } else {
            return "For all " + rater.toSimpleDescription(report.line()
                    , lineProcessing.columnView(Constraint.INCOMING_CONSTRAINT_GROUP).lookup(report.group())
                    , report.group());
        }
    }
}
