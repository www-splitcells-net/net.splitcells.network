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
package net.splitcells.gel.ui.editor.geal.example;

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.IdentifiedNameGenerator;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.editor.GelEditorFileSystem;
import net.splitcells.website.Format;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.ui.editor.geal.EditorProcessor.*;
import static net.splitcells.website.Format.COMMON_MARK;
import static net.splitcells.website.Format.CSV;
import static net.splitcells.website.server.messages.FieldUpdate.fieldUpdate;
import static net.splitcells.website.server.messages.FormUpdate.formUpdate;
import static net.splitcells.website.server.messages.RenderingType.PLAIN_TEXT;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class ColloquiumExample implements ProjectsRendererExtension {
    private static final String PATH = "/net/splitcells/gel/ui/editor/geal/example/colloquium-planning.json";

    public static ColloquiumExample colloquiumExample() {
        return new ColloquiumExample();
    }

    private ColloquiumExample() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path)) {
            val formUpdate = formUpdate();
            formUpdate.getFields().put(PROBLEM_DEFINITION, fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(configValue(GelEditorFileSystem.class)
                            .readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/colloquium-planning.txt")))
                    .setType(COMMON_MARK));
            formUpdate.getFields().put("demands.csv", fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(demandsCsv()))
                    .setType(CSV));
            formUpdate.getFields().put("supplies.csv", fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(suppliesCsv()))
                    .setType(CSV));
            return Optional.of(binaryMessage(StringUtils.toBytes(formUpdate.toTree().toJsonString()), Format.TEXT_PLAIN));
        }
        return Optional.empty();
    }

    @Override public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques(Path.of(PATH.substring(1)));
    }

    private static String demandsCsv() {
        final var testData = stringBuilder();
        testData.append("student,examiner,observer\n");
        final var randomness = randomness();
        final var nameGenerator = IdentifiedNameGenerator.identifiedNameGenerator();
        final Set<String> examinerNames = setOfUniques();
        range(0, 40).forEach(i -> examinerNames.add(nameGenerator.nextName()));
        final List<String> examinerNameList = list();
        examinerNameList.addAll(examinerNames);
        final Set<String> checkerNames = setOfUniques();
        range(0, 41).forEach(i -> checkerNames.add(nameGenerator.nextName()));
        final List<String> checkerNamesList = list();
        checkerNamesList.addAll(checkerNames);
        for (int student = 1; student <= 88; ++student) {
            var studentName = nameGenerator.nextName();
            for (int exam = 1; exam <= 177 / 88; ++exam) {
                testData.append(studentName + "," + randomness.chooseOneOf(examinerNameList) + "," + randomness.chooseOneOf(checkerNamesList) + "\n");
            }
        }
        return testData.toString();
    }

    private static String suppliesCsv() {
        final var testData = stringBuilder();
        testData.append("date,shift,roomNumber\n");
        final int examDayCountPerWeek = 5;
        for (int roomNumber = 1; roomNumber <= 6; ++roomNumber) {
            for (int week = 1; week <= 2; ++week) {
                for (int examDay = 1; examDay <= 5; ++examDay) {
                    for (int shift = 1; shift <= 5; ++shift) {
                        testData.append(modulus(examDay, examDayCountPerWeek) + 1
                                + (week - 1) * 7 + "," + shift + "," + roomNumber + "\n");
                    }
                }
            }
        }
        return testData.toString();
    }

}
