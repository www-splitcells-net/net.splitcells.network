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
package net.splitcells.website.server.translation.to.html;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.resource.FileSystem;

import static java.nio.file.Files.newInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

@JavaLegacyArtifact
public class PathBasedUriResolver implements URIResolver {

    public static PathBasedUriResolver pathBasedUriResolver(FileSystem folder, FileSystem configFiles
            , Function<String, Optional<String>> extension) {
        return new PathBasedUriResolver(folder, extension, configFiles);
    }

    private final FileSystem folder;
    private final FileSystem configFiles;
    private final Function<String, Optional<String>> extension;

    private PathBasedUriResolver(FileSystem folder, Function<String, Optional<String>> extension
            , FileSystem configFiles) {
        this.folder = folder;
        this.configFiles = configFiles;
        this.extension = extension;
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        try {
            final var extensionResult = extension.apply(href);
            final InputStream inputStream;
            if (extensionResult.isPresent()) {
                final var extensionResolution = new StreamSource
                        (new ByteArrayInputStream(extensionResult.get().getBytes(StandardCharsets.UTF_8)));
                /*
                 * Setting systemId to the underlying file in order to resolve relative paths
                 * used in the return value.
                 */
                extensionResolution.setSystemId(Paths.get(href).toString());
                return extensionResolution;
            }
            if (href.startsWith("/net.splitcells.website/current/xml")) {
                /*
                 * It is expected, that the website server is executed in a project.
                 * This project may contain configs at `/src/main/xml//net.splitcells.website/current/xml/**`.
                 * TODO The current implementation for this is a hack.
                 */
                inputStream = configFiles.inputStream(net.splitcells.dem.resource.Paths
                        .path(href.substring("/net.splitcells.website/current/xml".length())));
            } else if (Paths.get(href).isAbsolute()) {
                /*
                 * Absolute path are resolved to the host's filesystem.
                 * TODO Remove this, because this will only cause problems in future.
                 * Keep in mind, that this functionality is currently used in `variable.location.xsl`.
                 */
                inputStream = newInputStream(Paths.get(href));
            } else {
                final var rVal = new StreamSource(folder.inputStream(Paths.get(href)));
                rVal.setSystemId(Paths.get(href).toString());
                return rVal;
            }
            final var rVal = new StreamSource(inputStream);
            /*
             * Setting systemId to the underlying file in order to resolve relative paths
             * used in the return value.
             */
            rVal.setSystemId(Paths.get(href).toString());
            return rVal;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
