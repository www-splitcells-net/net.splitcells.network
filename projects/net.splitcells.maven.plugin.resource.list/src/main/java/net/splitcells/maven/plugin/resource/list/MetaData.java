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

import lombok.val;
import org.tomlj.Toml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.stream.IntStream.range;

public class MetaData {

    public static List<MetaData> parseMetaDataFromReuse(ResourceListMojo mojo, Path projectPath) {
        val projectName = mojo.project.getGroupId() + "." + mojo.project.getArtifactId();
        final List<MetaData> parsedMetaData = new ArrayList<>();
        var reuseToml = projectPath.resolve("REUSE.toml");
        final boolean parentReuse;
        if (!Files.exists(reuseToml)) {
            reuseToml = projectPath.resolve("../../REUSE.toml");
            parentReuse = true;
        } else {
            parentReuse = false;
        }
        if (Files.exists(reuseToml)) {
            mojo.getLog().info("Found REUSE.toml at " + reuseToml);
            try {
                val tomlParsing = Toml.parse(reuseToml);
                if (!tomlParsing.errors().isEmpty()) {
                    throw new RuntimeException("The REUSE TOML file containing license data is not correct: " + reuseToml);
                }
                val annotations = tomlParsing.getArray("annotations");
                range(0, annotations.size()).forEach(i -> {
                    val table = annotations.getTable(i);
                    val path = table.getString("path");
                    if (path == null) {
                        throw new RuntimeException("Path is missing for annotation: " + table);
                    }
                    String filePathStr;
                    if (parentReuse) {
                        val slashCount = path.codePoints().filter(ch -> ch == '/').count();
                        if (slashCount < 2) {
                            return;
                        }
                        val projectFolder = path.substring(0, path.indexOf("/", path.indexOf("/") + 1));
                        val projectFolderName = projectFolder.substring(projectFolder.indexOf("/") + 1);
                        if (!projectFolderName.equals(projectName)) {
                            mojo.getLog().debug("Skipping path: " + path);
                            return;
                        }
                        filePathStr = path.substring(path.indexOf("/", path.indexOf("/") + 1));
                    } else {
                        filePathStr = path;
                    }
                    if (filePathStr.startsWith("/")) {
                        filePathStr = filePathStr.substring(1);
                    }
                    if (filePathStr.contains("**")) {
                        val matcher = Path.of(filePathStr).getFileSystem().getPathMatcher("glob:" + filePathStr);
                        try (final var walk = java.nio.file.Files.walk(mojo.resourceFolder)) {
                            walk.forEach(p -> {
                                p = Path.of(mojo.normalizedResourcePathWithoutProjectPrefix(p));
                                if (matcher.matches(p)) {
                                    mojo.getLog().debug("Glob match: " + p);
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        val metaData = new MetaData(Path.of(filePathStr));
                        metaData.copyrightText = Optional.of(table.getString("SPDX-FileCopyrightText"));
                        metaData.license = Optional.of(table.getString("SPDX-License-Identifier"));
                        mojo.getLog().debug("Parsing path: " + metaData);
                        parsedMetaData.add(metaData);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return parsedMetaData;
    }

    public static MetaData parseMetaData(MetaData metaData, String fileContent) {
        val licenseMatch = SPX_LICENSE.matcher(fileContent);
        if (metaData.license.isEmpty() && licenseMatch.find()) {
            metaData.license = Optional.of(licenseMatch.group(2));
        }
        val copyrightMatch = SPX_COPYRIGHT_TEXT.matcher(fileContent);
        if (metaData.copyrightText.isEmpty() && copyrightMatch.find()) {
            metaData.copyrightText = Optional.of(copyrightMatch.group(2));
        }
        return metaData;
    }

    /**
     * The SPDX String is split, in order to avoid a bug in the REUSE license parser.
     */
    private static final Pattern SPX_LICENSE = Pattern.compile("(SPDX" + "-License-Identifier: )([a-zA-Z0-9-. ]+)");
    private static final Pattern SPX_COPYRIGHT_TEXT = Pattern.compile("(SPDX" + "-FileCopyrightText: )([a-zA-Z0-9-. *`]+)");
    /**
     * This is the SPDX-License-Identifier of the file's license.
     */
    Optional<String> license = Optional.empty();
    /**
     * This is the SPDX-FileCopyrightText of the file's copyright text.
     */
    Optional<String> copyrightText = Optional.empty();
    /**
     * This is the {@link Path} to the licensed file relative to this project's root folder.
     */
    final Path filePath;

    public MetaData(Path argFilePath) {
        filePath = argFilePath;
    }

    public MetaData replaceWith(MetaData metaData) {
        if (metaData.license.isPresent()) {
            license = metaData.license;
        }
        if (metaData.copyrightText.isPresent()) {
            copyrightText = metaData.copyrightText;
        }
        return this;
    }

    @Override public String toString() {
        return "filePath=" + filePath + ", license=" + license.orElse("No license info") + ", copyrightText=" + copyrightText.orElse("No copyright text");
    }

    public String toFileString() {
        return "license=" + license.orElse("No license info") + "\ncopyrightText=" + copyrightText.orElse("No copyright text");
    }
}
