package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.Files;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Paths.userHome;

public class UserCommandRenderer implements ProjectRendererExtension {
    public static UserCommandRenderer userCommandRenderer() {
        return new UserCommandRenderer();
    }
    
    private static final String RENDERING_PATH = "net/splitcells/os/state/interface/installed/index.html";

    private UserCommandRenderer() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (RENDERING_PATH.equals(path)) {
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (Files.isDirectory(userHome().resolve("bin/net.splitcells.os.state.interface.commands.managed"))) {
            projectRenderer.extendPerspectiveWithPath(layout
                    , Path.of(RENDERING_PATH));
        }
        return layout;
    }
}
