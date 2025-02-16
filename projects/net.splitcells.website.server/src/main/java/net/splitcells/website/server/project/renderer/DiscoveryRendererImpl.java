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
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class DiscoveryRendererImpl implements ProjectRenderer {
    public static DiscoveryRendererImpl discoveryRendererImpl() {
        return new DiscoveryRendererImpl();
    }

    private DiscoveryRendererImpl() {

    }

    @Override
    public FileSystemView projectFileSystem() {
        throw notImplementedYet();
    }

    @Override
    public Optional<byte[]> renderString(String arg) {
        throw notImplementedYet();
    }

    @Override
    public Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title, Optional<String> path, Config config, ProjectsRenderer projectsRenderer) {
        throw notImplementedYet();
    }

    @Override
    public Optional<byte[]> renderXml(String xml, LayoutConfig layoutConfig, Config config) {
        throw notImplementedYet();
    }

    @Override
    public Optional<byte[]> renderRawXml(String xml, Config config) {
        throw notImplementedYet();
    }

    @Override
    public Set<Path> projectPaths() {
        throw notImplementedYet();
    }

    @Override
    public Set<Path> relevantProjectPaths() {
        throw notImplementedYet();
    }

    @Override
    public Optional<BinaryMessage> render(String path) {
        throw notImplementedYet();
    }

    @Override
    public String resourceRootPath() {
        throw notImplementedYet();
    }

    public synchronized ObjectsRendererI withObject(DiscoverableRenderer object) {
        throw notImplementedYet();
    }
}
