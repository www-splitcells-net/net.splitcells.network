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
package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * Renders the {@link Configuration} of the current {@link Dem#config()}.
 *
 * @deprecated This is deprecated for now, as the config can contain security sensitive info and
 * no authorization system exists for this project yet.
 * When an authorization is present, this may enabled in the future.
 */
@Deprecated
public class DemConfigExtension implements ProjectsRendererExtension {
    @Deprecated
    private static DemConfigExtension demConfigExtension() {
        return new DemConfigExtension();
    }

    private static final String PATH = "/net/splitcells/dem/config.html";

    private DemConfigExtension() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path) && config.layout().isPresent()) {
            return projectsRendererI.renderContent(htmlTableOfConfig(), LayoutConfig.layoutConfig(PATH));
        }
        return Optional.empty();
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    private String htmlTableOfConfig() {
        final var tableContent = new StringBuilder();
        final var config = config();
        config.keys().forEach(f -> {
            tableContent.append("<tr><td>" + f.getName() + "</td>");
            tableContent.append("<td>" + config.configValueUntyped(f) + "</td>");
            tableContent.append("<td>" + config.configValueUntyped(f).getClass().getName()
                    + "</td></tr>");
        });
        return "<table xmlns=\"http://www.w3.org/1999/xhtml\"><tr><th>Key</th><th>Value</th><th>Value's Class</th></tr>"
                + tableContent
                + "</table>";
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        if (projectsRendererI.config().layout().isPresent()) {
            return setOfUniques(Path.of(PATH.substring(1)));
        }
        return setOfUniques();
    }
}
