package net.splitcells.gel.rating.rater;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.rating.framework.Rating;

import java.util.Optional;

@FunctionalInterface
public interface GroupRater {

    static GroupRater describedGroupRater(GroupRater arg, String description) {
        return new GroupRater() {
            @Override
            public Rating lineRating(Table lines, Optional<Line> addition, Optional<Line> removal) {
                return arg.lineRating(lines, addition, removal);
            }

            @Override
            public String toString() {
                return description;
            }
        };
    }

    Rating lineRating(Table lines, Optional<Line> addition, Optional<Line> removal);
}
