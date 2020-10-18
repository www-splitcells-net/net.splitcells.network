package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.object.Discoverable;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * Extending classes should always provide an empty constructor,
 * because this is just data and not something like a template.
 */
public abstract class PerspectiveDocument implements Discoverable {

    private final List<String> path;
    private final Perspective perspective;

    protected PerspectiveDocument(Class<?> clazz) {
        this(list(clazz.getPackage().getName().split("\\.")));
    }

    protected PerspectiveDocument(List<String> path) {
        this.path = path;
        perspective = createPerspective();
    }

    protected abstract Perspective createPerspective();

    @Override
    public List<String> path() {
        return path;
    }

    public Perspective perspective() {
        return perspective;
    }
}