package net.splitcells.gel.rating.rater;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.lambdas.TriFunction;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

public class RaterBasedOnLineGroup implements Rater {

    public static RaterBasedOnLineGroup raterBasedOnLineGroup(RaterBasedOnLineGroupLambda rater) {
        return new RaterBasedOnLineGroup(rater);
    }

    private final RaterBasedOnLineGroupLambda rater;

    private RaterBasedOnLineGroup(RaterBasedOnLineGroupLambda rater) {
        this.rater = rater;
    }

    @Override
    public Collection<List<String>> paths() {
        throw not_implemented_yet();
    }

    @Override
    public void addContext(Discoverable context) {
        throw not_implemented_yet();
    }

    @Override
    public List<Domable> arguments() {
        throw not_implemented_yet();
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        return rater.rating(lines, Optional.of(addition), Optional.empty(), children);
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return rater.rating(lines, Optional.empty(), Optional.of(removal), children);
    }
}
