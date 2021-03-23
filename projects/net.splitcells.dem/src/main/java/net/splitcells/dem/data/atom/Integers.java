package net.splitcells.dem.data.atom;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Integers {
    private Integers() {
        throw constructorIllegal();
    }

    public static boolean isEven(Integer arg) {
        return arg % 2 == 0;
    }
}
