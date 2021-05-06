package net.splitcells.dem.object;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public interface DeepCloneable {
    default <R> R deepClone(Class<? extends R> arg) {
        throw notImplementedYet();
    }
}
