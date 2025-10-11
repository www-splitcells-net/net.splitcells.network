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
    private Path basePath;
    private Path resourceFolder;
    private Path resourceListFile;
    private String fileSystemSeparator = FileSystems.getDefault().getSeparator();
    private String basePathStr;

    @Override
    public void execute() throws MojoExecutionException {
        basePath = Path.of(project.getBuild().getDirectory(), "classes");
        resourceFolder = basePath.resolve(project.getGroupId() + "." + project.getArtifactId() + ".resources");
        resourceListFile = basePath.resolve(project.getGroupId() + "." + project.getArtifactId() + ".resources.list.txt");
        basePathStr = basePath.toAbsolutePath().toString().replace(fileSystemSeparator, "/");
        try {
            if (!Files.isDirectory(resourceFolder)) {
                try {
                    Files.createDirectories(resourceFolder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Throwable t) {
            throw new MojoExecutionException("Could not create resource folder: " + resourceListFile, t);
        }
        try {
            getLog().debug("Writing resource list file to `" + resourceListFile.toAbsolutePath() + "`. The content are the file paths relative to `" + basePath.toAbsolutePath() + "`.");
            final var resourceList = new StringBuilder();
            /* "+1" makes the paths relative by removing the first slash.
             * Everything is done in one file loop, in order to minimize file access and therefore to speed up the process.
             */
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
        } catch (Throwable t) {
            throw new MojoExecutionException("Could not create resource list file: " + resourceListFile, t);
        }
    }
}
