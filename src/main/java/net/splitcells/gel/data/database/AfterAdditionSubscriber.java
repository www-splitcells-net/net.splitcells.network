package net.splitcells.gel.data.database;

import static java.util.Arrays.asList;

import java.util.Collection;

import net.splitcells.gel.data.table.Line;

@FunctionalInterface
public interface AfterAdditionSubscriber {

    void reģistrē_papildinājumi(Line rinda);

    default void register_papildinājumi(Collection<Line> rindas) {
        rindas.forEach(line -> reģistrē_papildinājumi(line));
    }

    default void register_papildinājumi(Line... rindas) {
        register_papildinājumi(asList(rindas));
    }
}
