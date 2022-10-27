package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.project.renderer.ObjectsMediaRendererI.objectsMediaRenderer;

public class ObjectsMediaRenderer extends OptionI<ObjectsMediaRendererI> {
    public ObjectsMediaRenderer() {
        super(() -> objectsMediaRenderer(Path.of("/net/splitcells/run/")));
    }

    public static void registerMediaObject(DiscoverableMediaRenderer object) {
        configValue(ObjectsMediaRenderer.class).withMediaObject(object);
    }
}
