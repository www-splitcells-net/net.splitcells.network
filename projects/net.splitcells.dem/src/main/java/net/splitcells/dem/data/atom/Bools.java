package net.splitcells.dem.data.atom;

import org.assertj.core.api.Assert;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Bools {
    private Bools() {
        throw constructorIllegal();
    }
    public static void require(boolean arg) {
        if (!arg) {
            throw new AssertionError();
        }
    }

    public static Bool bool(boolean arg) {
        return new BoolI(arg);
    }

    public static Bool truthful() {
        return new BoolI(true);
    }

    public static Bool untrue() {
        return new BoolI(false);
    }
}
