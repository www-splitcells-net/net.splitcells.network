/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Tree;
import net.splitcells.dem.resource.FileSystem;
import net.splitcells.dem.resource.FileSystems;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutRenderer;
import net.splitcells.website.server.project.LayoutUtils;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;

import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.perspective.TreeI.perspective;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

/**
 * Renders all commands, that are installed via 'net.splitcells.os.state.interface' for the current user
 * at '/net/splitcells/os/state/interface/installed/index.html'.
 */
public class UserCommandProjectRendererExtension implements ProjectRendererExtension {
    public static UserCommandProjectRendererExtension userCommandRenderer() {
        return new UserCommandProjectRendererExtension();
    }

    private static final String RENDERING_PATH = "net/splitcells/os/state/interface/installed/index.html";
    private static final Path BIN_FOLDER_PATH = Path.of("bin/net.splitcells.os.state.interface.commands.managed/");
    private static final FileSystem BIN_FOLDER = FileSystems.userHome();

    private UserCommandProjectRendererExtension() {

    }

    /**
     * @param path
     * @param projectRenderer
     * @return
     */
    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (RENDERING_PATH.equals(path) && BIN_FOLDER.isDirectory(BIN_FOLDER_PATH)) {
            final var layout = perspective(NameSpaces.VAL, NameSpaces.DEN);
            try {

                BIN_FOLDER.walkRecursively(BIN_FOLDER_PATH).forEach(command -> {
                            final var commandName = listWithValuesOf(command.getFileName().toString().split("\\."));
                            // Filters commands installed via 'command.managed.install'.
                            if (!commandName.lastValue().get().matches("[0-9]+")) {
                                LayoutRenderer.extend(layout, commandName, NameSpaces.DEN);
                            }
                        }
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return projectRenderer.renderString(layout.toString())
                    .map(r -> binaryMessage(r, TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public Tree extendProjectLayout(Tree layout, ProjectRenderer projectRenderer) {
        if (BIN_FOLDER.isDirectory(BIN_FOLDER_PATH)) {
            LayoutUtils.extendPerspectiveWithPath(layout, Path.of(RENDERING_PATH));
        }
        return layout;
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        return setOfUniques(Path.of(RENDERING_PATH));
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return projectPaths(projectRenderer);
    }
}
