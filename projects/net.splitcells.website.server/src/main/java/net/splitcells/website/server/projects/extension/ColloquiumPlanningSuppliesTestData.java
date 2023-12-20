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
package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.IdentifiedNameGenerator;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.website.Formats;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.util.Optional;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class ColloquiumPlanningSuppliesTestData implements ProjectsRendererExtension {

    private static final String PATH = "/net/splitcells/gel/ui/colloquium-planning-supplies-test-data.csv";

    public static ColloquiumPlanningSuppliesTestData colloquiumPlanningSuppliesTestData() {
        return new ColloquiumPlanningSuppliesTestData();
    }

    private ColloquiumPlanningSuppliesTestData() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path)) {
            final var testData = stringBuilder();
            final var randomness = randomness();
            final var nameGenerator = IdentifiedNameGenerator.identifiedNameGenerator();
            final List<String> examinerNames = list();
            range(0, 40).forEach(i -> examinerNames.add(nameGenerator.nextName()));
            final List<String> checkerNames = list();
            range(0, 41).forEach(i -> checkerNames.add(nameGenerator.nextName()));
            var studentName = nameGenerator.nextName();
            for (int roomNumber = 1; roomNumber <= 6; ++roomNumber) {
                for (int week = 1; week <= 2; ++week) {
                    for (int examDay = 1; examDay <= 5; ++examDay) {
                        for (int shift = 1; shift <= 5; ++shift) {
                            testData.append(examDay + "," + shift + "," + roomNumber + "\n");
                        }
                    }
                }
            }
            return Optional.of(binaryMessage(StringUtils.toBytes(testData.toString()), Formats.TEXT_PLAIN));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques(Path.of(PATH.substring(1)));
    }
}
