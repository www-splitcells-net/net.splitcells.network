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

    public static ForAll veidot(Rater grupēšana) {
        return priekšVisiem(grupēšana);
    }

    public static ForAll priekšVisiem(Rater grupēšana) {
        return new ForAll(grupēšana);
    }

    protected ForAll(Rater grupēšana) {
        super(raterBasedGrouping(grupēšana));
    }

    public Rater grouping() {
        return rater;
    }

    @Override
    public Class<? extends Constraint> type() {
        return ForAll.class;
    }

    @Override
    protected List<String> localNaturalArgumentation(Report ziņojums) {
        if (rater.type().equals(ForAllAttributeValues.class)) {
            return list(rater.toSimpleDescription(ziņojums.rinda(), ziņojums.grupa()));
        } else {
            return list("Priekš visiem " + rater.toSimpleDescription(ziņojums.rinda(), ziņojums.grupa()));
        }
    }
}
