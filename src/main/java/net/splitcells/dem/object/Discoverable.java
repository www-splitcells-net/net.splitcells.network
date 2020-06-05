package net.splitcells.dem.object;

import net.splitcells.dem.data.set.list.List;

import static net.splitcells.dem.data.set.list.Lists.list;

public interface Discoverable {

    /**
     * RENAME Find better name.
     */
    Discoverable NO_CONTEXT = () -> list();

    static Discoverable discoverable(List<String> path) {
        return () -> path;
    }

    List<String> path();
}
