package net.splitcells.dem.environment.resource;

import net.splitcells.dem.environment.config.OptionI;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.function.Supplier;

public abstract class ResourceI<T extends Closeable & Flushable> extends OptionI<T> implements Resource<T> {
    public ResourceI(Supplier<T> arg_default_value) {
        super(arg_default_value);
    }
}
