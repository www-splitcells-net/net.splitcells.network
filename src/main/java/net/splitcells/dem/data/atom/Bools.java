package net.splitcells.dem.data.atom;

import org.assertj.core.api.Assert;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Bools {
    private Bools() {
        throw constructorIllegal();
    }
    public static void require(boolean arg) {
        if (arg) {
            throw new AssertionError();
        }
    }
}
