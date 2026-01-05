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
package net.splitcells.gel.ui;

import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.gel.ui.editor.geal.EditorProcessor;
import net.splitcells.website.server.ServerConfig;
import net.splitcells.website.server.WebsiteServerCell;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.gel.ui.editor.geal.EditorProcessor.editorProcessor;
import static net.splitcells.website.server.ProjectConfig.projectConfig;

public class GelUiCell implements Cell {
    @Override
    public String groupId() {
        return "net.splitcells";
    }

    @Override
    public String artifactId() {
        return "gel.ui";
    }

    @Override
    public void accept(Environment env) {
        env.config().configValue(ServerConfig.class)
                .withAdditionalProject(projectConfig("/net/splitcells/gel/ui/"
                        , configValue(GelUiFileSystem.class)))
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/codemirror-editor-bundle.js")
                .withAdditionalProcessor(EditorProcessor.PATH, editorProcessor());
        env.withCell(WebsiteServerCell.class);
    }
}
