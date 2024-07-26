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
package net.splitcells.gel.ui.no.code.editor;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.constraint.Query;
import net.splitcells.website.Formats;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class FunctionMeta implements ProjectsRendererExtension {
    public static ProjectsRendererExtension functionMeta() {
        return new FunctionMeta();
    }

    private static final String PATH = "/net/splitcells/gel/ui/no/code/editor/function-meta.json";
    private static final Map<String, Integer> argumentsPerFunctions = map();
    private static final Set<String> varArgumentsPerFunctions = setOfUniques();
    private static final Set<String> functionNames = setOfUniques();

    static {
        argumentsPerFunctions.put("attribute", 2);
        argumentsPerFunctions.put("database", 1);
        argumentsPerFunctions.put("forEach", 1);
        argumentsPerFunctions.put("hasSize", 1);
        argumentsPerFunctions.put("minimalDistance", 2);
        argumentsPerFunctions.put("solution", 3);
        argumentsPerFunctions.put("then", 1);
        varArgumentsPerFunctions.add("database");
        varArgumentsPerFunctions.add("forAllCombinationsOf");
        varArgumentsPerFunctions.add("solution");
        functionNames.addAll(argumentsPerFunctions.keySet());
        functionNames.addAll(varArgumentsPerFunctions);
    }

    private FunctionMeta() {
    }

    /**
     * TODO HACK This data should be queried from the {@link Query} interface,
     * in order to avoid duplicating this information.
     * Currently, the {@link Query} interface does not provide this information.
     *
     * @param path
     * @param projectsRenderer
     * @param config
     * @return
     */
    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRenderer, Config config) {
        if (PATH.equals(path)) {
            final var metaDoc = StringUtils.stringBuilder();
            metaDoc.append("{");
            boolean firstName = true;
            for (final var name : functionNames) {
                if (firstName) {
                    metaDoc.append("\"" + name + "\":{");
                    firstName = false;
                } else {
                    metaDoc.append(",\"" + name + "\":{");
                }
                metaDoc.append("\"number-of-arguments\":"
                        + argumentsPerFunctions.get(name)
                        + ",\"has-variable-arguments\":"
                        + varArgumentsPerFunctions.contains(name)
                        + "}");
            }
            metaDoc.append("}");
            return Optional.of(binaryMessage(toBytes(metaDoc.toString()), Formats.JSON));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRenderer) {
        // Avoid first slash.
        return setOfUniques(Path.of(PATH.substring(1)));
    }
}
