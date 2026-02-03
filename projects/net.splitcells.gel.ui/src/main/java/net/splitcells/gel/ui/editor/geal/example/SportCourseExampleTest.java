/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui.editor.geal.example;

import lombok.val;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.ui.editor.geal.example.SportCourseExample.availableCoursesCsv;
import static net.splitcells.gel.ui.editor.geal.example.SportCourseExample.studentChoicesCsv;

public class SportCourseExampleTest {
    @UnitTest public void testStudentChoicesCsv() {
        requireEquals(studentChoicesCsv(randomness(1), 2),
                """
                        Student,Chosen Sport, Chosen Sport Type, Is Secondary Choice
                        1,Volleyball,Team sport,0
                        1,Running,Individual sport,0
                        1,Parkour,Other sport,0
                        1,Handball,Team sport,1
                        1,Gymnastics,Individual sport,1
                        2,Volleyball,Team sport,0
                        2,Swimming,Individual sport,0
                        2,Yoga,Other sport,0
                        2,Basketball,Team sport,1
                        2,Running,Individual sport,1
                        """
        );
    }

    @UnitTest public void testAvailableCoursesCsv() {
        requireEquals(availableCoursesCsv(randomness(1), 2, 2, 1, 2, 3),
                """
                        Assigned Course Number,Assigned Sport,Assigned Sport Type
                        1,Team sport,Handball
                        1,Team sport,Handball
                        2,Individual sport,Swimming
                        2,Individual sport,Swimming
                        3,Individual sport,Running
                        3,Individual sport,Running
                        4,Individual sport,Climbing
                        4,Individual sport,Climbing
                        5,Individual sport,Parkour
                        5,Individual sport,Parkour
                        6,Individual sport,Parkour
                        6,Individual sport,Parkour
                        7,Team sport,Football
                        7,Team sport,Football
                        8,Individual sport,Gymnastics
                        8,Individual sport,Gymnastics
                        9,Individual sport,Badminton
                        9,Individual sport,Badminton
                        10,Individual sport,Yoga
                        10,Individual sport,Yoga
                        11,Individual sport,Parkour
                        11,Individual sport,Parkour
                        12,Individual sport,Parkour
                        12,Individual sport,Parkour
                        """);
    }
}
