package net.splitcells.gel.rating.rater;

import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.rating.structure.Rating;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface GroupRater {
    Rating lineRating(Table lines, Optional<Line> addition, Optional<Line> removal);
}
