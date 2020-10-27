package net.splitcells.gel.kodols.atrisinājums;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāzes;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.kodols.problēma.Problēma;

import static net.splitcells.dem.Dem.environment;

public abstract class AtrisinājumuVeidotajs extends AspectOrientedConstructor<Atrisinājums> implements Closeable, Flushable {
    abstract Atrisinājums atrisinājum(Problēma problēma);
}
