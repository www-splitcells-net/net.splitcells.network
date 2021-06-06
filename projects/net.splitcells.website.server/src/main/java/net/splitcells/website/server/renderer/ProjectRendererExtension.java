package net.splitcells.website.server.renderer;

import net.splitcells.dem.lang.perspective.Perspective;

import java.nio.file.Path;
import java.util.Optional;

public interface ProjectRendererExtension {
    Optional<byte[]> renderFile(String path, Path projectFolder);

    Perspective extendProjectLayout(Perspective layout, Path projectFolder, String resourceRootPath);

}
