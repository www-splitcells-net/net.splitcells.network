package net.splitcells.dem.environment.resource;

import net.splitcells.dem.environment.config.Option;
import net.splitcells.dem.resource.communication.Closeable;

public interface Resource<T extends Closeable> extends Option<T> {
}
