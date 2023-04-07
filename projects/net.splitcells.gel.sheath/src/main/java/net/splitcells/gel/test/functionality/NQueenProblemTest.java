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
package net.splitcells.gel.test.functionality;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.dem.resource.communication.log.IsEchoToFile;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.gel.Gel;
import net.splitcells.gel.GelDev;
import net.splitcells.gel.GelEnv;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.problem.derived.SimplifiedAnnealingProblem;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.primitive.UsedSupplySwitcher;
import net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.resource.Files.createDirectory;
import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.testing.TestTypes.CAPABILITY_TEST;
import static net.splitcells.dem.utils.MathUtils.max;
import static net.splitcells.dem.utils.MathUtils.naturalLogarithm;
import static net.splitcells.dem.utils.MathUtils.power;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.raterBasedOnLineValue;
import static net.splitcells.gel.rating.rater.classification.GroupMultiplier.groupMultiplier;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.optimization.meta.Backtracking.backtracking;
import static net.splitcells.gel.solution.optimization.meta.LinearIterator.linearIterator;
import static net.splitcells.gel.solution.optimization.meta.hill.climber.FunctionalHillClimber.functionalHillClimber;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedOfflineRepair.simpleConstraintGroupBasedOfflineRepair;

/**
 * TODO Clean up this.
 */
public class NQueenProblemTest extends TestSuiteI {
    public static final Attribute<Integer> COLUMN = attribute(Integer.class, "column");
    public static final Attribute<Integer> ROW = attribute(Integer.class, "row");

    @Tag(CAPABILITY_TEST)
    @Test
    public void test_8_queen_problem_with_rolling_the_dice() {
        // TODO Setting the randomness seed.
        final Solution testSubject = nQueenProblem(8, 8).asSolution();
        testSubject.optimize(offlineLinearInitialization());
        testSubject.optimize(functionalHillClimber(UsedSupplySwitcher.usedSupplySwitcher(6), 50));
        testSubject.optimize(functionalHillClimber(UsedSupplySwitcher.usedSupplySwitcher(8), 100));
        createDirectory(environment().config().configValue(ProcessPath.class));
        writeToFile(environment().config().configValue(ProcessPath.class).resolve("history.fods")
                , testSubject.history().toFods());
        writeToFile(environment().config().configValue(ProcessPath.class).resolve("analysis.fods")
                , testSubject.toFodsTableAnalysis());
        testSubject.constraint().rating().requireEqualsTo(cost(0));
    }

    @Tag(CAPABILITY_TEST)
    @Test
    public void test_8_queen_problem_with_repair() {
        GelDev.process(() -> {
            final var testSubject = nQueenProblem(8, 8).asSolution();
            onlineLinearInitialization().optimize(testSubject);
            testSubject.optimizeWithMethod(ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair(3)
                    , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
            testSubject.optimizeWithMethod(ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair(2)
                    , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
            testSubject.optimizeWithMethod(ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair(1)
                    , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
            testSubject.optimizeWithMethod(ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair(0)
                    , (currentSolution, step) -> !currentSolution.isOptimal());
            require(testSubject.isOptimal());
        }, GelEnv.standardDeveloperConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()));
        }));
    }

    @Tag(CAPABILITY_TEST)
    @Test
    public void test_8_queen_problem_with_backtracking() {
        final var testSubject = nQueenProblem(8, 8).asSolution();
        backtracking().optimize(testSubject);
        require(testSubject.isOptimal());
    }

    /**
     * TODO
     */
    @DisabledTest
    @Tag(CAPABILITY_TEST)
    @Test
    public void test_8_queen_problem_with_annealing_hill_climber() {
        GelDev.process(() -> {
            final var testSubject = nQueenProblem(8, 8).asSolution();
            // The temperature functions was determined by trial and error with universal allocation program's temperature functions.
            testSubject.optimize(offlineLinearInitialization());
            SimplifiedAnnealingProblem.simplifiedAnnealingProblem(testSubject,
                            i -> max(naturalLogarithm(4.0d) / power(naturalLogarithm(i + 3d), 15d)
                                    , 0d))
                    .optimizeOnce(functionalHillClimber(
                            linearIterator(
                                    list(
                                            UsedSupplySwitcher.usedSupplySwitcher(8))),
                            120_000));
            // NOTE usedSupplySwitcher(2) finds many non improving steps.
            createDirectory(environment().config().configValue(ProcessPath.class));
            writeToFile(environment().config().configValue(ProcessPath.class).resolve("history.fods")
                    , testSubject.history().toFods());
            writeToFile(environment().config().configValue(ProcessPath.class).resolve("analysis.fods")
                    , testSubject.toFodsTableAnalysis());
            domsole().append(testSubject.constraint().rating(), Optional.empty(), LogLevel.UNKNOWN_ERROR);
            testSubject.constraint().rating().requireEqualsTo(cost(0));
        }, GelEnv.standardDeveloperConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(IsEchoToFile.class, false)
                    .withConfigValue(MessageFilter.class, logMessage -> logMessage.path()
                            .equals(list("demands", "Solution", "isComplete", "optimize", "after", "cost")))
            //.withConfigValue(Domsole.class, uiRouter(env.config().configValue(MessageFilter.class)))
            ;
        }));
    }

    private Problem nQueenProblem(int rows, int columns) {
        final var demands = listWithValuesOf(
                rangeClosed(1, columns)
                        .mapToObj(i -> list((Object) i))
                        .collect(Lists.toList()));
        final var supplies = listWithValuesOf(
                rangeClosed(1, rows)
                        .mapToObj(i -> list((Object) i))
                        .collect(Lists.toList()));
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
     * TODO Use this.
     */
    private static Rater diagonals(int rows, int columns) {
        return groupMultiplier(
                ascDiagonals(rows, columns),
                descDiagonals(rows, columns)
        );
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
