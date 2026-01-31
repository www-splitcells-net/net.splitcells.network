/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui.editor.geal.example;

import lombok.val;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.editor.GelEditorFileSystem;
import net.splitcells.website.Format;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StringUtils.requireMatch;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.ui.editor.geal.EditorProcessor.PROBLEM_DEFINITION;
import static net.splitcells.website.Format.COMMON_MARK;
import static net.splitcells.website.Format.CSV;
import static net.splitcells.website.server.messages.FieldUpdate.fieldUpdate;
import static net.splitcells.website.server.messages.FormUpdate.formUpdate;
import static net.splitcells.website.server.messages.RenderingType.PLAIN_TEXT;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

/**
 * <p>As only one course per sport type and semester is allowed, a course id is not needed.</p>
 * <p>TODO Constants like {@link #DEFAULT_SEMESTER_COUNT} or {@link #DEFAULT_MAX_COURSE_SIZE} should be parsed from
 * the original `sports-course-planning.txt`,
 * in order to avoid divergence between the example data and the problem definition.</p>
 */
public class SportCourseExample implements ProjectsRendererExtension {
    private static final String PATH = "/net/splitcells/gel/ui/editor/geal/example/sports-course-planning.json";
    private static final int DEFAULT_STUDENT_COUNT = 126;
    private static final int DEFAULT_SEMESTER_COUNT = 4;
    private static final int DEFAULT_TEAM_SPORT_COUNTS = 4;
    private static final int DEFAULT_INDIVIDUAL_SPORT_COUNTS = 3;
    private static final int DEFAULT_OTHER_SPORT_COUNTS = 1;
    private static final int DEFAULT_COURSES_PER_SEMESTER = 9;
    private static final int DEFAULT_TEAM_COURSES_PER_SEMESTER = 2;
    private static final int DEFAULT_INDIVIDUAL_COURSES_PER_SEMESTER = 4;
    private static final int DEFAULT_MAX_COURSE_SIZE = 28;
    private static final List<String> SPORT_TYPES = list("Team sport", "Individual sport", "Other sport");
    private static final List<String> DEFAULT_TEAM_SPORTS = list("Volleyball", "Football", "Handball", "Basketball");
    private static final List<String> DEFAULT_INDIVIDUAL_SPORTS = list("Swimming", "Running", "Gymnastics", "Badminton");
    private static final List<String> DEFAULT_OTHER_SPORTS = list("Parkour", "Climbing", "Yoga", "Dancing");

    public static SportCourseExample sportCourseExample() {
        return new SportCourseExample();
    }

    private SportCourseExample() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path)) {
            val formUpdate = formUpdate();
            val rnd = randomness();
            formUpdate.getFields().put(PROBLEM_DEFINITION, fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(configValue(GelEditorFileSystem.class)
                            .readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/sports-course-planning.txt")))
                    .setType(COMMON_MARK));
            formUpdate.getFields().put("student-choices.csv", fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(studentChoicesCsv(rnd, DEFAULT_STUDENT_COUNT)))
                    .setType(CSV));
            formUpdate.getFields().put("available-courses.csv", fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(availableCoursesCsv(rnd
                            , DEFAULT_SEMESTER_COUNT
                            , DEFAULT_MAX_COURSE_SIZE
                            , DEFAULT_TEAM_COURSES_PER_SEMESTER
                            , DEFAULT_INDIVIDUAL_COURSES_PER_SEMESTER
                            , DEFAULT_COURSES_PER_SEMESTER - DEFAULT_TEAM_COURSES_PER_SEMESTER - DEFAULT_INDIVIDUAL_COURSES_PER_SEMESTER)))
                    .setType(CSV));
            formUpdate.getFields().put("available-half-years.csv", fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(availableHalfYearsCsv()))
                    .setType(CSV));
            return Optional.of(binaryMessage(StringUtils.toBytes(formUpdate.toTree().toJsonString()), Format.TEXT_PLAIN));
        }
        return Optional.empty();
    }

    protected static String studentChoicesCsv(Randomness rnd, int studentCount) {
        val testData = stringBuilder();
        testData.append("Student,Chosen Sport, Chosen Sport Type, Is Secondary Choice\n");
        rangeClosed(1, studentCount).forEach(i -> {
            testData.append(studentChoice(i, rnd, 1, 0, false));
            testData.append(studentChoice(i, rnd, 0, 1, false));
            testData.append(studentChoice(i, rnd, 0, 0, false));
            testData.append(studentChoice(i, rnd, 1, 0, true));
            testData.append(studentChoice(i, rnd, 0, 1, true));
        });
        return testData.toString();
    }

    private static String studentChoice(int studentId, Randomness rnd, float teamProbability, float individualProbability, boolean isSecondaryChoice) {
        val choiceSportType = rnd.integerFromDistribution(teamProbability, individualProbability, 1f - teamProbability - individualProbability);
        final String choiceSport;
        if (choiceSportType == 0) {
            choiceSport = rnd.chooseOneOf(DEFAULT_TEAM_SPORTS);
        } else if (choiceSportType == 1) {
            choiceSport = rnd.chooseOneOf(DEFAULT_INDIVIDUAL_SPORTS);
        } else if (choiceSportType == 2) {
            choiceSport = rnd.chooseOneOf(DEFAULT_OTHER_SPORTS);
        } else {
            throw execException(choiceSportType + "");
        }
        return studentId
                + "," + choiceSport
                + "," + SPORT_TYPES.get(choiceSportType)
                + "," + (isSecondaryChoice ? 1 : 0)
                + "\n";
    }

    protected static String availableCoursesCsv(Randomness rnd
            , int semesterCount
            , int maxCourseSize
            , int teamCoursesPerSemester
            , int individualCoursesPerSemester
            , int otherCoursesPerSemester) {
        val testData = stringBuilder();
        testData.append("Assigned Sport,Assigned Sport Type\n");
        rangeClosed(1, semesterCount).forEach(semester -> {
            if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) require(DEFAULT_TEAM_SPORTS.size() > teamCoursesPerSemester);
            rangeClosed(1, teamCoursesPerSemester).forEach(iTeamCourse -> {
                val sportType = rnd.chooseOneOf(DEFAULT_TEAM_SPORTS);
                rangeClosed(1, maxCourseSize).forEach(courseSeat -> {
                    testData.append(SPORT_TYPES.get(0));
                    testData.append(",");
                    testData.append(sportType);
                    testData.append("\n");
                });
            });
            rangeClosed(1, individualCoursesPerSemester).forEach(iIndividualCourse -> {
                val sportType = rnd.chooseOneOf(DEFAULT_INDIVIDUAL_SPORTS);
                rangeClosed(1, maxCourseSize).forEach(courseSeat -> {
                    testData.append(SPORT_TYPES.get(1));
                    testData.append(",");
                    testData.append(sportType);
                    testData.append("\n");
                });
            });
            rangeClosed(1, otherCoursesPerSemester).forEach(iIndividualCourse -> {
                val sportType = rnd.chooseOneOf(DEFAULT_INDIVIDUAL_SPORTS);
                rangeClosed(1, maxCourseSize).forEach(courseSeat -> {
                    testData.append(SPORT_TYPES.get(1));
                    testData.append(",");
                    testData.append(sportType);
                    testData.append("\n");
                });
            });
        });
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
