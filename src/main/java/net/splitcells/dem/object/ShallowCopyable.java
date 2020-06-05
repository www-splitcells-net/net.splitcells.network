package net.splitcells.dem.object;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

public interface ShallowCopyable {

    default <R> R shallowCopy(Class<? extends R> arg) {
        throw not_implemented_yet();
    }

}
