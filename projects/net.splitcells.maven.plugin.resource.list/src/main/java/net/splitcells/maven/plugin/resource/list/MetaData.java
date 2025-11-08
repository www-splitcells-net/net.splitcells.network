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
import org.apache.maven.plugin.AbstractMojo;
import org.tomlj.Toml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.IntStream.range;

public class MetaData {

    public static List<MetaData> parseMetaDataFromReuse(AbstractMojo mojo, Path projectPath) {
        final List<MetaData> metaData = new ArrayList<>();
        var reuseToml = projectPath.resolve("REUSE.toml");
        var parentReuse = false;
        if (!Files.exists(reuseToml)) {
            reuseToml = projectPath.resolve("../../REUSE.toml");
            parentReuse = true;
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
                    mojo.getLog().info("Parsing path: " + table.getString("path"));
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return metaData;
    }

    /**
     * The SPDX String is split, in order to avoid a bug in the REUSE license parser.
     */
    private static final Pattern SPX_LICENSE = Pattern.compile("(SPDX" + "-License-Identifier: )([a-zA-Z0-9-. ]+)");
    private static final Pattern SPX_COPYRIGHT_TEXT = Pattern.compile("(SPDX" + "-FileCopyrightText: )([a-zA-Z0-9-. *`]+)");
    String license;
    String copyrightText;
    Path filePath;

    public static MetaData parseMetaData(String fileContent) {
        val metaData = new MetaData();
        val licenseMatch = SPX_LICENSE.matcher(fileContent);
        if (licenseMatch.find()) {
            metaData.license = licenseMatch.group(2);
        }
        val copyrightMatch = SPX_COPYRIGHT_TEXT.matcher(fileContent);
        if (copyrightMatch.find()) {
            metaData.copyrightText = copyrightMatch.group(2);
        }
        return metaData;
    }

    @Override
    public String toString() {
        return "license=" + license + "\ncopyrightText=" + copyrightText;
    }
}
