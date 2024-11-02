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
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.constraint.Query;
import net.splitcells.website.Formats;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
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
    /**
     * TODO Use read only version.
     */
    private static final Map<String, Integer> argumentsPerFunctions = createArgumentsPerFunctions();
    /**
     * TODO Use read only version.
     */
    private static final Set<String> varArgumentsPerFunctions = varArgumentsPerFunctions();
    /**
     * TODO Use read only version.
     */
    private static final Set<String> functionNames = Sets.<String>setOfUniques()
            .with(argumentsPerFunctions.keySet())
            .with(varArgumentsPerFunctions);

    private static Map<String, Integer> createArgumentsPerFunctions() {
        final Map<String, Integer> rVal = map();
        rVal.put("attribute", 2);
        rVal.put("database", 1);
        rVal.put("forEach", 1);
        rVal.put("hasSize", 1);
        rVal.put("minimalDistance", 2);
        rVal.put("solution", 3);
        rVal.put("then", 1);
        return rVal;
    }

    private static Set<String> varArgumentsPerFunctions() {
        final Set<String> rVal = setOfUniques();
        rVal.add("database");
        rVal.add("forAllCombinationsOf");
        rVal.add("solution");
        return rVal;
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
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRenderer) {
        // Avoid first slash.
        return setOfUniques(Path.of(PATH.substring(1)));
    }
}
