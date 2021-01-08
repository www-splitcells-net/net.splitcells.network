package net.splitcells.gel.solution;

import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.problem.Problēma;

public abstract class OptimizationBuilder extends AspectOrientedConstructor<Atrisinājums> implements Closeable, Flushable {
    abstract Atrisinājums atrisinājum(Problēma problēma);
}
