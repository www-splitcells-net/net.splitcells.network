package net.splitcells.dem.object;

import static net.splitcells.dem.utils.NotImplementedYet.not_implemented_yet;

public interface DeepCloneable {
    default <R> R deepClone(Class<? extends R> arg) {
        throw notImplementedYet();
    }
}
