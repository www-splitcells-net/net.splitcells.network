package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.Files;
import net.splitcells.website.server.renderer.LayoutRenderer;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Paths.userHome;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;

public class UserCommandExtension implements ProjectRendererExtension {
    public static UserCommandExtension userCommandExtension() {
        return new UserCommandExtension();
    }

    private static final String RENDERING_PATH = "/net/splitcells/os/state/interface/installed/index.html";
    private static final Path BIN_FOLDER = userHome().resolve("bin/net.splitcells.os.state.interface.commands.managed");

    private UserCommandExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (RENDERING_PATH.equals(path)) {
            final var layout = perspective(NameSpaces.VAL, NameSpaces.NATURAL);
            try {
                java.nio.file.Files.walk(BIN_FOLDER).forEach(command ->
                        LayoutRenderer.extend(layout
                                , listWithValuesOf(command.getFileName().toString().split("\\.")))
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return projectRenderer.renderString(layout.toString())
                    .map(r -> renderingResult(r, TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (Files.isDirectory(BIN_FOLDER)) {
            projectRenderer.extendPerspectiveWithPath(layout
                    , Path.of(RENDERING_PATH));
        }
        return layout;
    }
}
