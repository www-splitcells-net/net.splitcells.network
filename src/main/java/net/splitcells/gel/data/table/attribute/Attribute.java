package net.splitcells.gel.data.table.attribute;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.gel.data.table.Line;

public interface Attribute<T> extends Domable {

    String vārds();

    default boolean vienāds(Line arg) {
        return this == arg;
    }

    Bool irGadījumsNo(Object arg);
    
    default T deserializēVērtību(String vērtība) {
        throw new UnsupportedOperationException();
    }
    
}
