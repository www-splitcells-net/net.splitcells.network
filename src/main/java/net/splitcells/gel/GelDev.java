package net.splitcells.gel;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public final class GelDev {
    private GelDev() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        GelEnv.process(() -> {

        });
    }
}
