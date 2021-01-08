package net.splitcells.gel.data.datubāze;

import static java.util.Arrays.asList;

import java.util.Collection;

import net.splitcells.gel.data.tabula.Rinda;

@FunctionalInterface
public interface PapildinājumsKlausītājs {

    void reģistrē_papildinājumi(Rinda rinda);

    default void register_papildinājumi(Collection<Rinda> rindas) {
        rindas.forEach(line -> reģistrē_papildinājumi(line));
    }

    default void register_papildinājumi(Rinda... rindas) {
        register_papildinājumi(asList(rindas));
    }
}
