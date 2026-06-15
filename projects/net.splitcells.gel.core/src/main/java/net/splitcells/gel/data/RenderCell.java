/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data;

import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.assignment.Assignmentss;
import net.splitcells.gel.data.table.Tables;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.data.view.View.MIRROR_NAME;
import static net.splitcells.website.server.project.renderer.ObjectsRenderer.registerObject;

public class RenderCell implements Cell {

    @Override public String groupId() {
        return "net.spitcells";
    }

    @Override public String artifactId() {
        return "gel.core";
    }

    @Override public void accept(Environment env) {
        env.config().configValue(Tables.class).withConnector(table -> {
            if (!table.name().equals(MIRROR_NAME)) {
                registerObject(table.discoverableRenderer());
            }
        });
        env.config().configValue(Assignmentss.class).withConnector(assignments -> {
            if (!assignments.name().equals(MIRROR_NAME)) {
                registerObject(assignments.discoverableRenderer());
            }
        });
    }
}
