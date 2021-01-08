package net.splitcells.gel.atrisinājums;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.problēma.Problēma;

import static net.splitcells.dem.Dem.environment;

public class Atrisinājumi extends ResourceI<AtrisinājumuVeidotajs> {
    public Atrisinājumi() {
        super(() -> new AtrisinājumuVeidotajsI());
    }

    public static Atrisinājums atrisinājum(Problēma problēma) {
        return environment().config().configValue(Atrisinājumi.class).atrisinājum(problēma);
    }
}
