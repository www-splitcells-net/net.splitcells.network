package net.splitcells.gel.solution;

import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.problem.Problēma;

public abstract class OptimizationBuilder extends AspectOrientedConstructor<Solution> implements Closeable, Flushable {
    abstract Solution atrisinājum(Problēma problēma);
}
