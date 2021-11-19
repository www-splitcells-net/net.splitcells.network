package net.splitcells.website.server.renderer;

import net.splitcells.dem.data.set.Set;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Provides a path system, where every object is mapped to a path and the content of the object can be queried
 * by the path.
 * Note that there is no guarantee,
 * that accessing an object does not change it.
 */
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
     * <p>
     * This can be considered the address of the renderer,
     * that identifies the renderer.
     *
     * @return Prefix of all paths, supported by the renderer.
     */
    @Deprecated
    String resourceRootPath();

    default Path resourceRootPath2() {
        return Path.of(resourceRootPath());
    }
}
