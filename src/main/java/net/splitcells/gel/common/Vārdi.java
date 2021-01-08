package net.splitcells.gel.common;

import net.splitcells.gel.Language;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * NOŅEMT Lieto {@link Language}.
 */
@Deprecated
public final class Vārdi {

    public static final String ARGUMENTI = "argi";

    private Vārdi() {
        throw constructorIllegal();
    }

}
