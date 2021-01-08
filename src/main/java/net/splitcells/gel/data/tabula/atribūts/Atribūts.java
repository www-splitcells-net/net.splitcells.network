package net.splitcells.gel.data.tabula.atribūts;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.gel.data.tabula.Rinda;

public interface Atribūts<T> extends Domable {

    String vārds();

    default boolean vienāds(Rinda arg) {
        return this == arg;
    }

    Bool irGadījumsNo(Object arg);
    
    default T deserializēVērtību(String vērtība) {
        throw new UnsupportedOperationException();
    }
    
}
