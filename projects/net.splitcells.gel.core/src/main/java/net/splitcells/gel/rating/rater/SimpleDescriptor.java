package net.splitcells.gel.rating.rater;

import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

@FunctionalInterface
public interface SimpleDescriptor {
    String toSimpleDescription(Line line, Table groupLineProcessing, GroupId incomingGroup);
}
