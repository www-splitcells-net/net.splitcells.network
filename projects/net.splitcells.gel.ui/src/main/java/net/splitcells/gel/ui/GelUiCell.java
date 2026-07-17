/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/codemirror-editor-bundle.js");
        env.withCell(WebsiteServerCell.class);
    }
}
