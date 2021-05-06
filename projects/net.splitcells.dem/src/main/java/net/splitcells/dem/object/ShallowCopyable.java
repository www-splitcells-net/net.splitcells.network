package net.splitcells.dem.object;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public interface ShallowCopyable {

    default <R> R shallowCopy(Class<? extends R> arg) {
        throw notImplementedYet();
    }

}
