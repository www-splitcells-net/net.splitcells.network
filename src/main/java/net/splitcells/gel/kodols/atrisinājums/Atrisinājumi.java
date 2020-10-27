package net.splitcells.gel.kodols.atrisinājums;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāzes;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.kodols.problēma.Problēma;

import static net.splitcells.dem.Dem.environment;

public class Atrisinājumi extends ResourceI<AtrisinājumuVeidotajs> {
    public Atrisinājumi() {
        super(() -> new AtrisinājumuVeidotajsI());
    }

    public static Atrisinājums atrisinājum(Problēma problēma) {
        return environment().config().configValue(Atrisinājumi.class).atrisinājum(problēma);
    }
}
