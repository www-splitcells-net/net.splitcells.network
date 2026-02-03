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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import lombok.val;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static java.nio.file.Files.createDirectories;
import static net.splitcells.maven.plugin.resource.list.MetaData.parseMetaData;
import static net.splitcells.maven.plugin.resource.list.MetaData.parseMetaDataFromReuse;

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
    protected MavenProject project;
    private String fileSystemSeparator = FileSystems.getDefault().getSeparator();
    protected Path resourceFolder;
    private String basePathStr;
    private String resourceFolderName;

    public String normalizedResourcePath(Path resource) {
        final var absoluteResourcePath = resource
                .toAbsolutePath()
                .toString()
                .replace(fileSystemSeparator, "/");
        {
            // Extend resource list.
            // "+1" makes the paths relative by removing the first slash.
            var resourcePath = absoluteResourcePath.substring(basePathStr.length() + 1);
            if (Files.isDirectory(resource)) {
                resourcePath += "/";
            }
            return resourcePath;
        }
    }

    public String normalizedResourcePathWithoutProjectPrefix(Path resource) {
        return normalizedResourcePath(resource).substring(resourceFolderName.length() + 1);
    }

    @Override
    public void execute() throws MojoExecutionException {
        final Path basePath = Path.of(project.getBuild().getDirectory(), "classes");
        resourceFolderName = project.getGroupId() + "." + project.getArtifactId() + ".resources";
        resourceFolder = basePath.resolve(resourceFolderName);
        final Path metaDataFolder = basePath.resolve(project.getGroupId() + "." + project.getArtifactId() + ".resources.meta");
        final Path resourceListFile = basePath.resolve(project.getGroupId() + "." + project.getArtifactId() + ".resources.list.txt");
        basePathStr = basePath.toAbsolutePath().toString().replace(fileSystemSeparator, "/");
        final Map<Path, MetaData> inventory = new HashMap<>();
        try {
            if (Files.isRegularFile(resourceFolder)) {
                throw new MojoExecutionException("No file is allowed to use the same path as the resource folder: " + resourceFolder);
            }
            if (!Files.isDirectory(resourceFolder)) {
                try {
                    createDirectories(resourceFolder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Files.isRegularFile(metaDataFolder)) {
                throw new MojoExecutionException("No file is allowed to use the same path as the meta resource folder: " + metaDataFolder);
            }
            if (!Files.isDirectory(metaDataFolder)) {
                try {
                    createDirectories(metaDataFolder);
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
            // Every read of the affected files is done in one file loop, in order to minimize file access and therefore to speed up the process.
            try (final var walk = java.nio.file.Files.walk(resourceFolder)) {
                walk.forEach(resource -> {
                    resourceList.append(normalizedResourcePath(resource)).append("\n");
                    if (Files.isRegularFile(resource)) {
                        // Create meta data.
                        final String resourceContent;
                        try {
                            try {
                                resourceContent = Files.readString(resource);
                            } catch (MalformedInputException e) {
                                // Only UTF-8 compatible files are considered.
                                return;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Could not read resource file: " + resource, e);
                        }
                        val relativeResource = resourceFolder.relativize(resource);
                        inventory.compute(relativeResource, (filePath, oldMetaData) -> {
                            if (oldMetaData == null) {
                                return parseMetaData(new MetaData(relativeResource), resourceContent);
                            }
                            return parseMetaData(oldMetaData, resourceContent);
                        });
                    }
                });
            }
            parseMetaDataFromReuse(this, project.getBasedir().toPath()).forEach(reuseMeta -> {
                inventory.compute(reuseMeta.filePath, (filePath, oldMetaData) -> {
                    if (oldMetaData == null) {
                        return reuseMeta;
                    }
                    return oldMetaData.replaceWith(reuseMeta);
                });
            });
            inventory.values().forEach(metaData -> {
                val targetFile = metaDataFolder.resolve(metaData.filePath);
                try {
                    createDirectories(targetFile.getParent());
                } catch (IOException e) {
                    throw new RuntimeException("Could not create folder for meta data file: " + targetFile.getParent(), e);
                }
                try (final BufferedWriter metaWriter = new BufferedWriter(new FileWriter(targetFile.toFile()))) {
                    metaWriter.write(metaData.toFileString());
                } catch (IOException e) {
                    throw new RuntimeException("Could not write meta file: " + metaData.filePath, e);
                }
            });
            try (final BufferedWriter resourceListWriter = new BufferedWriter(new FileWriter(resourceListFile.toFile()))) {
                resourceListWriter.write(resourceList.toString());
            }
        } catch (Throwable t) {
            throw new MojoExecutionException("Could not create resource list file: " + resourceListFile, t);
        }
    }
}
