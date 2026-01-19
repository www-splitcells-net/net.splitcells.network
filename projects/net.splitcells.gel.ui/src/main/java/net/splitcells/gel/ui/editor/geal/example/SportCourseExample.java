/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui.editor.geal.example;

import lombok.val;
import net.splitcells.dem.data.set.Set;
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

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;
import static net.splitcells.gel.ui.editor.geal.EditorProcessor.PROBLEM_DEFINITION;
import static net.splitcells.website.Format.COMMON_MARK;
import static net.splitcells.website.Format.CSV;
import static net.splitcells.website.server.messages.FieldUpdate.fieldUpdate;
import static net.splitcells.website.server.messages.FormUpdate.formUpdate;
import static net.splitcells.website.server.messages.RenderingType.PLAIN_TEXT;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class SportCourseExample implements ProjectsRendererExtension {
    private static final String PATH = "/net/splitcells/gel/ui/editor/geal/example/sports-course-planning.json";
    private static final int DEFAULT_STUDENT_COUNT = 126;
    private static final int DEFAULT_SEMESTER_COUNT = 126;
    private static final float DEFAULT_SECONDARY_CHOICE_COUNT = 0.5f;
    private static final int DEFAULT_TEAM_SPORT_COUNTS = 4;
    private static final int DEFAULT_INDIVIDUAL_SPORT_COUNTS = 3;
    private static final int DEFAULT_OTHER_SPORT_COUNTS = 1;
    private static final int DEFAULT_COURSES_PER_SEMESTER = 9;
    private static final int DEFAULT_TEAM_COURSES_PER_SEMESTER = 2;
    private static final int DEFAULT_INDIVIDUAL_COURSES_PER_SEMESTER = 4;

    public static SportCourseExample sportCourseExample() {
        return new SportCourseExample();
    }

    private SportCourseExample() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path)) {
            val formUpdate = formUpdate();
            formUpdate.getFields().put(PROBLEM_DEFINITION, fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(configValue(GelEditorFileSystem.class)
                            .readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/sports-course-planning.txt")))
                    .setType(COMMON_MARK));
            formUpdate.getFields().put("student-choices.csv", fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(studentChoicesCsv()))
                    .setType(CSV));
            formUpdate.getFields().put("available-courses.csv", fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(availableCoursesCsv()))
                    .setType(CSV));
            formUpdate.getFields().put("available-half-years.csv", fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(availableHalfYearsCsv()))
                    .setType(CSV));
            return Optional.of(binaryMessage(StringUtils.toBytes(formUpdate.toTree().toJsonString()), Format.TEXT_PLAIN));
        }
        return Optional.empty();
    }

    private static String studentChoicesCsv() {
        val testData = stringBuilder();
        testData.append("Student,Chosen Sport, Chosen Sport Type, Is Secondary Choice\n");
        return testData.toString();
    }

    private static String availableCoursesCsv() {
        val testData = stringBuilder();
        testData.append("Assigned Sport,Assigned Sport Type\n");
        return testData.toString();
    }

    private static String availableHalfYearsCsv() {
        val testData = stringBuilder();
        testData.append("Assigned Semester\n");
        return testData.toString();
    }

    @Override public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques(Path.of(PATH.substring(1)));
    }
}
