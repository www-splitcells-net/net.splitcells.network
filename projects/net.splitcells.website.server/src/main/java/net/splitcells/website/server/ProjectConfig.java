/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server;

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.resource.FileSystemView;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

public class ProjectConfig {
    public static ProjectConfig projectConfig(String rootPath, FileSystemView projectFiles) {
        return new ProjectConfig(rootPath, projectFiles);
    }

    private final String rootPath;
    private final FileSystemView projectFiles;

    private ProjectConfig(String rootPath, FileSystemView projectFiles) {
        if (!rootPath.endsWith("/")) {
            throw execException(tree("The given root path has to end with `/`, but does not.")
                    .withProperty("root path", rootPath));
        }
        this.rootPath = rootPath;
        this.projectFiles = projectFiles;
    }

    public String rootPath() {
        return rootPath;
    }

    public FileSystemView projectFiles() {
        return projectFiles;
    }

    @Override public int hashCode() {
        return Thing.hashCode(rootPath, projectFiles);
    }

    @Override public boolean equals(Object other) {
        if (other instanceof ProjectConfig otherConfig) {
            return rootPath.equals(otherConfig.rootPath) && projectFiles.equals(otherConfig.projectFiles);
        }
        return false;
    }
}
