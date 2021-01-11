package net.splitcells.gel.data.database;

import static java.util.Arrays.asList;

import java.util.Collection;

import net.splitcells.gel.data.table.Line;

@FunctionalInterface
public interface AfterAdditionSubscriber {

    void register_addition(Line line);

    default void register_papildinājumi(Collection<Line> lines) {
        lines.forEach(line -> register_addition(line));
    }

    default void register_papildinājumi(Line... lines) {
        register_papildinājumi(asList(lines));
    }
}
