/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;

public class ProjectsRendererExtensions implements Option<List<ProjectsRendererExtension>> {
    @Override public List<ProjectsRendererExtension> defaultValue() {
        return list();
    }
    @Override public Optional<Tree> serialize(List<ProjectsRendererExtension> currentValue) {
        return Optional.empty();
    }
}
