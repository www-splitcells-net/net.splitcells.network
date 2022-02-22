/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutRenderer;
import net.splitcells.website.server.project.LayoutUtils;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Paths.userHome;
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

/**
 * Renders all commands, that are installed via 'net.splitcells.os.state.interface' for the current user
 * at '/net/splitcells/os/state/interface/installed/index.html'.
 */
public class UserCommandRenderer implements Renderer {
    public static UserCommandRenderer userCommandRenderer() {
        return new UserCommandRenderer();
    }

    private static final String RENDERING_PATH = "net/splitcells/os/state/interface/installed/index.html";
    private static final Path BIN_FOLDER = userHome().resolve("bin/net.splitcells.os.state.interface.commands.managed/");

    private UserCommandRenderer() {

    }

    /**
     * @param path
     * @param projectRenderer
     * @return
     */
    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (RENDERING_PATH.equals(path) && isDirectory(BIN_FOLDER)) {
            final var layout = perspective(NameSpaces.VAL, NameSpaces.DEN);
            try {
                java.nio.file.Files.walk(BIN_FOLDER).forEach(command -> {
                            final var commandName = listWithValuesOf(command.getFileName().toString().split("\\."));
                            // Filters commands installed via 'command.managed.install'.
                            if (!commandName.lastValue().get().matches("[0-9]+")) {
                                LayoutRenderer.extend(layout
                                        , commandName, NameSpaces.DEN);
                            }
                        }
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
        if (isDirectory(BIN_FOLDER)) {
            LayoutUtils.extendPerspectiveWithPath(layout
                    , Path.of(RENDERING_PATH));
        }
        return layout;
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        return setOfUniques(Path.of(RENDERING_PATH));
    }
}
