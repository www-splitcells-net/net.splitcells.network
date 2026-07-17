/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.network.worker.via.java;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.network.worker.via.java.Logger.logger;

public class LoggerTest {
    @UnitTest
    public void test() {
        final var testSubject = logger();
        require(testSubject.parseTestIdentifier("[engine:junit-jupiter]/[class:net.splitcells.gel.test.functionality.NQueenProblemTest]/[method:test_8_queen_problem_with_repair()]")
                .orElseThrow().equals("net/splitcells/gel/test/functionality/NQueenProblemTest/test_8_queen_problem_with_repair"));
    }
}
