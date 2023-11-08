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
package net.splitcells.website.server.project;

import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.dem.resource.FileSystems;
import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.translation.to.html.XslTransformer;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.FileSystemInMemory.fileSystemInMemory;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.website.server.translation.to.html.PathBasedUriResolver.pathBasedUriResolver;

public class FileStructureTransformer {

    public static FileStructureTransformer fileStructureTransformer(FileSystemView xslLibs
            , String transformerXsl
            , SourceValidator sourceValidator
            , Function<String, Optional<String>> config) {
        return new FileStructureTransformer(xslLibs, transformerXsl, sourceValidator, config);
    }

    private final SourceValidator sourceValidator;
    private final String transformerXsl;
    private final Function<String, Optional<String>> config;
    private final FileSystemView xslLibs;

    private FileStructureTransformer(FileSystemView xslLibs
            , String transformerXsl
            , SourceValidator sourceValidator
            , Function<String, Optional<String>> config) {
        this.sourceValidator = sourceValidator;
        this.transformerXsl = transformerXsl;
        this.config = config;
        this.xslLibs = xslLibs;
    }

    /**
     * TODO Create an option, to enable or disable this, for better performance.
     * <p>
     * Creates a new XslTransformer, so that the XSL-files are reloaded
     * during each rendering.
     *
     * @return
     */
    private XslTransformer transformer() {
        try {

            return new XslTransformer
                    (xslLibs.inputStream(Paths.path(transformerXsl))
                            , pathBasedUriResolver(xslLibs
                            , fileSystemInMemory()
                            , config::apply));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String transform(String content) {
        sourceValidator.validate(content).ifPresent(error -> {
            domsole().append(perspective(error, STRING), LogLevel.ERROR);
        });
        return transformer().transform(content);
    }
}
