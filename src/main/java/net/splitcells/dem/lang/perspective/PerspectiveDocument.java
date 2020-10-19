package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.object.Discoverable;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * Instances of this class represent the content of XML/JSON like files.
 * XSD Schema functionality can be created via special classes, that wrap perspectives.
 * <p>
 * This currently not used. Real XML files are used instead, because XML provides things in
 * order support needed functionality: it supports namespaces, XSL, XSD and XPATH.
 * The only downside is, that defining schemas with XSD and validating files with these schemas is complicated
 * and costly. Especially the integration of XSD files into editors is not as easy as one might think.
 * The best way to solve this, is to automatically apply schema validation any time that XML files are processed.
 * <p>
 * This class will probably be a replacement for XML, if it does cause more problems.
 * <p>
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