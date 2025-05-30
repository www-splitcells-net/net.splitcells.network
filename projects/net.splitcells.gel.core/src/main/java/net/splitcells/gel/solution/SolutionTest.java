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
package net.splitcells.gel.solution;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.Gel;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.rating.type.Cost;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.tree.XmlConfig.xmlConfig;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.solution.optimization.meta.hill.climber.FunctionalHillClimber.functionalHillClimber;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.GroupSelectors.groupSelector;

public class SolutionTest {

    /**
     * <p>TODO Use some general test problem template.</p>
     * <p>TODO Test with some {@link Line} with {@link Cost#noCost()} and some {@link Line}.</p>
     * <p>TODO Test content without using a big string in this repo, in order to not bloat this repo.</p>
     */
    @Test
    public void testToFodsTableAnalysis() {
        final Solution testSubject = pseudoNQueenProblem(8, 8).asSolution();
        testSubject.history().withRegisterEventIsEnabled(true);
        testSubject.optimize(offlineLinearInitialization());
        testSubject.toFodsTableAnalysis().toXmlString(xmlConfig());
    }

    /**
     * Don't write the result to files outside the build folder, in order to avoid messing up the user's computer files.
     */
    @Test
    public void testCreateStandardAnalysis() {
        final Solution testSubject = pseudoNQueenProblem(8, 8).asSolution();
        testSubject.history().withRegisterEventIsEnabled(true);
        testSubject.optimize(offlineLinearInitialization());
        testSubject.createStandardAnalysis();
    }

    @Test
    @Disabled
    public void testPerformance() {
        final var d = attribute(Integer.class, "d");
        final var s = attribute(Integer.class, "s");
        final var testSubject = defineProblem("testPerformance")
                .withDemandAttributes(d)
                .withDemands(rangeClosed(1, 10000).mapToObj(i -> list((Object) i)).collect(toList()))
                .withSupplyAttributes(s)
                .withSupplies(rangeClosed(1, 100000).mapToObj(i -> list((Object) modulus(i, 10))).collect(toList()))
                .withConstraint(c -> {
                    c.forAll(s).then(lineValueRater(l -> l.value(d) == -1));
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.history().processWithoutHistory(() -> {
            testSubject.optimize(onlineLinearInitialization());
            simpleConstraintGroupBasedRepair(groupSelector(randomness(), 1
                    , 1), a -> solution -> {
            }, false).optimize(testSubject);
        });
    }

    private static Problem pseudoNQueenProblem(int rows, int columns) {
        var column = attribute(Integer.class, "column");
        var row = attribute(Integer.class, "row");
        final var demands = listWithValuesOf(
                rangeClosed(1, columns)
                        .mapToObj(i -> list((Object) i))
                        .collect(Lists.toList()));
        final var supplies = listWithValuesOf(
                rangeClosed(1, rows)
                        .mapToObj(i -> list((Object) i))
                        .collect(Lists.toList()));
        return Gel.defineProblem()
                .withDemandAttributes(column)
                .withDemands(demands)
                .withSupplyAttributes(row)
                .withSupplies(supplies)
                .withConstraint(
                        r -> {
                            r.forAll(row).forAll(column).then(hasSize(1));
                            r.forAll(row).then(hasSize(1));
                            r.forAll(column).then(hasSize(1));
                            return r;
                        })
                .toProblem();
    }

}
