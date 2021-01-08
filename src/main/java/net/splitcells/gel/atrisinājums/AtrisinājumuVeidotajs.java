package net.splitcells.gel.atrisinājums;

import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.problēma.Problēma;

public abstract class AtrisinājumuVeidotajs extends AspectOrientedConstructor<Atrisinājums> implements Closeable, Flushable {
    abstract Atrisinājums atrisinājum(Problēma problēma);
}
