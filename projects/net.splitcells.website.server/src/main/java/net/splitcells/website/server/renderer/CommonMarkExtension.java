package net.splitcells.website.server.renderer;

import net.splitcells.dem.lang.perspective.Perspective;

import java.nio.file.Path;
import java.util.Optional;

public class CommonMarkExtension implements ProjectRendererExtension {
    @Override
    public Optional<byte[]> renderFile(String path, Path projectFolder) {
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, Path projectFolder, String resourceRootPath) {
        return null;
    }
}
