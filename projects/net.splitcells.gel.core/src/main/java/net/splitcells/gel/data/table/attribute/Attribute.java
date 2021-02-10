package net.splitcells.gel.data.table.attribute;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.gel.data.table.Line;

public interface Attribute<T> extends Domable {

    String name();

    default boolean equalz(Line arg) {
        return this == arg;
    }

    Bool isInstanceOf(Object arg);
    
    default T deserializeValue(String value) {
        throw new UnsupportedOperationException();
    }
    
}
