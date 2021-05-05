package net.splitcells.gel.rating.rater;

import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface RaterBasedOnLineGroupLambda {
    RatingEvent rating(Table lines, Optional<Line> addition, Optional<Line> removal, List<Constraint> children);
}
