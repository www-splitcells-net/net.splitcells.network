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
package net.splitcells.maven.plugin.resource.list;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * <p>This Maven Plugin creates a file, that list all resources of the jar to be built,
 * and adds this file to the jar's resources,
 * so that other Java code can list all jar resources in a portable way.
 * This file is located at "${project.build.directory}/classes/${project.groupId}.${project.artifactId}.resources.list.txt".</p>
 * <p>This goal should be executed in the compile phase.</p>
 */
@Mojo(name = "generate-resource-list")
public class ResourceListMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        final var basePath = Path.of(project.getBuild().getDirectory(), "classes");
        final var resourceFolder = basePath.resolve(project.getGroupId() + "." + project.getArtifactId() + ".resources");
        final var resourceListFile = basePath.resolve(project.getGroupId() + "." + project.getArtifactId() + ".resources.list.txt");
        try {
            if (!Files.isDirectory(resourceFolder)) {
                try {
                    Files.createDirectories(resourceFolder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            final var fileSystemSeparator = FileSystems.getDefault().getSeparator();
            final var basePathStr = basePath.toAbsolutePath().toString().replace(fileSystemSeparator, "/");
            getLog().info("Writing resource list file to `" + resourceListFile.toAbsolutePath() + "`. The content are the file paths relative to `" + basePath.toAbsolutePath() + "`.");
            final var resourceList = new StringBuilder();
            // "+1" makes the paths relative by removing the first slash.
            try (final var walk = java.nio.file.Files.walk(resourceFolder)) {
                walk.forEach(resource -> {
                    var resourceStr = resource
                            .toAbsolutePath()
                            .toString()
                            .replace(fileSystemSeparator, "/")
                            .substring(basePathStr.length() + 1);
                    if (Files.isDirectory(resource)) {
                        resourceStr += "/";
                    }
                    resourceList.append(resourceStr);
                    resourceList.append("\n");
                });
            }
            try (final BufferedWriter resourceListWriter = new BufferedWriter(new FileWriter(resourceListFile.toFile()))) {
                resourceListWriter.write(resourceList.toString());
            }
        } catch (Throwable e) {
            throw new MojoExecutionException("Could not create resource list file: " + resourceListFile, e);
        }
    }
}
