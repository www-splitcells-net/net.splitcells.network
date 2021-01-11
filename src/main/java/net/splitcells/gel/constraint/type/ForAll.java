package net.splitcells.gel.constraint.type;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.classification.RaterBasedOnGrouping.raterBasedGrouping;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.type.framework.ConstraintBasedOnLocalGroupsAI;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.classification.ForAllAttributeValues;

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
    protected List<String> localNaturalArgumentation(Report report) {
        if (rater.type().equals(ForAllAttributeValues.class)) {
            return list(rater.toSimpleDescription(report.line(), report.group()));
        } else {
            return list("For all " + rater.toSimpleDescription(report.line(), report.group()));
        }
    }
}
