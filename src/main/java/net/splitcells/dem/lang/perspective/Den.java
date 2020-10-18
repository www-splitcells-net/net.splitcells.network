package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.namespace.NameSpaces;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;

public class Den implements Perspective {

    protected static Den den(String value) {
        return new Den(value);
    }

    private final Perspective perspective;

    private Den(String value) {
        perspective = perspective(value, NameSpaces.DEN);
    }

    @Override
    public NameSpace nameSpace() {
        return perspective.nameSpace();
    }

    @Override
    public String name() {
        return perspective.name();
    }

    @Override
    public List<Perspective> children() {
        return perspective.children();
    }
}
