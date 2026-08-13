/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data;

import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.gel.data.assignment.Assignmentss;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.website.server.WebsiteServerCell;

import java.util.Optional;

import static net.splitcells.gel.data.view.View.MIRROR_NAME;
import static net.splitcells.website.server.project.renderer.ObjectsRenderer.registerObject;

/**
 * Enables the rendering of data on the {@link WebsiteServerCell}.
 * This needs additional memory and CPU and therefore is not enabled by default.
 */
public class RenderCell implements Cell {

    @Override public String groupId() {
        return "net.spitcells";
    }

    @Override public String artifactId() {
        return "gel.core";
    }

    @Override public void accept(Environment env) {
        env.withCell(WebsiteServerCell.class);
        env.config().configValue(Tables.class).withConnector(table -> {
            if (!table.name().equals(MIRROR_NAME)) {
                registerObject(table.discoverableRenderer(), Optional.of(table));
            }
        });
        env.config().configValue(Assignmentss.class).withConnector(assignments -> {
            if (!assignments.name().equals(MIRROR_NAME)) {
                registerObject(assignments.discoverableRenderer(), Optional.of(assignments));
            }
        });
        env.config().configValue(Histories.class).withConnector(history ->
                registerObject(history.discoverableRenderer(), Optional.of(history)));
    }
}
