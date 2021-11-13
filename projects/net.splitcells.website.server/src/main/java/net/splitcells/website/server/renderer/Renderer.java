package net.splitcells.website.server.renderer;

import net.splitcells.dem.data.set.Set;

import java.nio.file.Path;
import java.util.Optional;

public interface Renderer {

    /**
     * @return List Of All Path That Can Be Rendered
     */
    Set<Path> projectPaths();

    /**
     * TODO Use {@link Path} objects instead of {@link String}s.
     *
     * @param path Path To Be Rendered
     * @return This is the rendering result, if the path is supported.
     */
    Optional<RenderingResult> render(String path);

    /**
     * TODO Use {@link Path} object instead of {@link String}.
     *
     * This can be considered the address of the renderer,
     * that identifies the renderer.
     *
     * @return Prefix of all paths, supported by the renderer.
     */
    String resourceRootPath();
}
