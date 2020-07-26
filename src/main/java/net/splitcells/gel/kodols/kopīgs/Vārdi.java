package net.splitcells.gel.kodols.kopīgs;

import net.splitcells.gel.kodols.Valoda;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * NOŅEMT Lieto {@link Valoda}.
 */
@Deprecated
public final class Vārdi {

    public static final String ARGUMENTI = "argi";

    private Vārdi() {
        throw constructorIllegal();
    }

}
