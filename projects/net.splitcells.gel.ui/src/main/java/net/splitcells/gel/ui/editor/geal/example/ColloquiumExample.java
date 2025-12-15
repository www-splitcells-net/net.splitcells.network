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
package net.splitcells.gel.ui.editor.geal.example;

import lombok.val;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.website.Format;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.ui.editor.geal.EditorProcessor.FORM_UPDATE;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.processor.Response.response;

public class ColloquiumExample implements ProjectsRendererExtension {
    private static final String PATH = "/net/splitcells/gel/ui/editor/geal/example/colloquium-planning.json";

    public static ColloquiumExample colloquiumExample() {
        return new ColloquiumExample();
    }

    private ColloquiumExample() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path)) {
            val formUpdate = tree(FORM_UPDATE);
            return Optional.of(binaryMessage(StringUtils.toBytes(formUpdate.toJsonString()), Format.TEXT_PLAIN));
        }
        return Optional.empty();
    }

    @Override public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }
}
