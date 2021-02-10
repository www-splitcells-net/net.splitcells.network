package net.splitcells.dem.lang.dom;

import java.util.function.Function;

public interface DomableFunction<I, O> extends Domable, Function<I, O> {
}
