/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui.editor.geal.example;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.ui.editor.geal.example.SportCourseExample.*;

public class SportCourseExampleTest {
    @UnitTest public void testStudentChoicesCsv() {
        requireEquals(studentChoicesCsv(randomness(1), 2),
                """
                        Student,Chosen Sport, Chosen Sport Type, Is Secondary Choice
                        Ottfried Amberger,Volleyball,Team sport,0
                        Ottfried Amberger,Running,Individual sport,0
                        Ottfried Amberger,Parkour,Other sport,0
                        Ottfried Amberger,Gymnastics,Individual sport,1
                        Ottfried Amberger,Yoga,Other sport,1
                        Ottfried Herschberger,Volleyball,Team sport,0
                        Ottfried Herschberger,Swimming,Individual sport,0
                        Ottfried Herschberger,Yoga,Other sport,0
                        Ottfried Herschberger,Dancing,Other sport,1
                        Ottfried Herschberger,Climbing,Other sport,1
                        """
        );
    }

    @UnitTest public void testAvailableSemestersCsv() {
        requireEquals(availableSemestersCsv(4,3,2),
                """
                        Assigned Semester
                        1
                        1
                        1
                        1
                        1
                        1
                        1
                        1
                        1
                        1
                        1
                        1
                        2
                        2
                        2
                        2
                        2
                        2
                        2
                        2
                        2
                        2
                        2
                        2
                        """);
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
