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
package net.splitcells.gel.quickstart;

import net.splitcells.gel.Gel;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.rating.rater.framework.Rater;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.TestTypes.CAPABILITY_TEST;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.raterBasedOnLineValue;
import static net.splitcells.gel.solution.optimization.meta.Backtracking.backtracking;
import static net.splitcells.gel.solution.optimization.meta.hill.climber.FunctionalHillClimber.functionalHillClimber;
import static net.splitcells.gel.solution.optimization.primitive.LinearDeinitializer.linearDeinitializer;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;

/**
 * <p>This class solves the widely known 8 queen problem with the commonly known
 * backtracking algorithm.
 * It provides for newcomers an intro to the API of the framework,
 * where they can focus on the API instead of the problem.
 * By implementing something familiar,
 * it should be easier for the beginner to understand the API.</p>
 * <p>This class also show the bare minimum,
 * that should be done, in order to use this project.</p>
 */
public class NQueenProblemTest {
    public static final Attribute<Integer> COLUMN = attribute(Integer.class, "column");
    public static final Attribute<Integer> ROW = attribute(Integer.class, "row");

    @Tag(CAPABILITY_TEST)
    @Test
    public void test() {
        final var testSubject = nQueenProblem(8, 8).asSolution();
        testSubject.optimize(offlineLinearInitialization());
        testSubject.optimizeOnce(functionalHillClimber(100));
        testSubject.createAnalysis(Path.of("./target/analysis-hill-climber/"));
        testSubject.optimize(linearDeinitializer());
        backtracking().optimize(testSubject);
        require(testSubject.isOptimal());
        testSubject.createAnalysis(Path.of("./target/analysis-backtracking/"));

    }

    private static Problem nQueenProblem(int rows, int columns) {
        final var demands = listWithValuesOf(
                rangeClosed(1, columns)
                        .mapToObj(i -> list((Object) i))
                        .collect(toList()));
        final var supplies = listWithValuesOf(
                rangeClosed(1, rows)
                        .mapToObj(i -> list((Object) i))
                        .collect(toList()));
        return Gel.defineProblem()
                .withDemandAttributes(COLUMN)
                .withDemands(demands)
                .withSupplyAttributes(ROW)
                .withSupplies(supplies)
                .withConstraint(
                        r -> {
                            r.forAll(ROW).forAll(COLUMN).then(hasSize(1));
                            r.forAll(ROW).then(hasSize(1));
                            r.forAll(COLUMN).then(hasSize(1));
                            r.forAll(ascDiagonals(rows, columns)).then(hasSize(1));
                            r.forAll(descDiagonals(rows, columns)).then(hasSize(1));
                            return r;
                        })
                .toProblem();
    }

    /**
     * The ascending diagonal with the number 0 represents the diagonal in the middle.
     */
    private static Rater ascDiagonals(int rows, int columns) {
        return raterBasedOnLineValue("ascDiagonals", line -> line.value(ROW) - line.value(COLUMN));
    }

    /**
     * The descending diagonal with the number 0 represents the diagonal in the middle.
     */
    private static Rater descDiagonals(int rows, int columns) {
        return raterBasedOnLineValue("descDiagonals", line -> line.value(ROW) - Math.abs(line.value(COLUMN) - columns - 1));
    }
}
