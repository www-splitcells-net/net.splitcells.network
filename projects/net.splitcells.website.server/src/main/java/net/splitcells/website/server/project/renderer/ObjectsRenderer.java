package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;
import java.util.function.Supplier;

import static net.splitcells.website.server.project.renderer.ObjectsRendererI.objectsRenderer;

public class ObjectsRenderer extends OptionI<ObjectsRendererI> {
    public ObjectsRenderer() {
        super(() -> objectsRenderer(Path.of("/net/splitcells/run/")));
    }
}
