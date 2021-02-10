package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.namespace.NameSpace;

import java.util.Optional;

public interface PerspectiveView extends Domable {

    NameSpace nameSpace();

    String name();

    default Optional<Perspective> value() {
        if (children().size() == 1) {
            return Optional.of(children().get(0));
        }
        return Optional.empty();
    }

    ListView<Perspective> children();

    default boolean nameIs(String value, NameSpace nameSpace) {
        return nameSpace().equals(nameSpace) && name().equals(value);
    }
}
