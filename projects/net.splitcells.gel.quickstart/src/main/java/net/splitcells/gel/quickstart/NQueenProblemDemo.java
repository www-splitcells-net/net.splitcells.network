/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.quickstart;

import net.splitcells.gel.Gel;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.rating.rater.Rater;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.raterBasedOnLineValue;
import static net.splitcells.gel.solution.optimization.meta.Backtracking.backtracking;
import static org.assertj.core.api.Assertions.assertThat;

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
public class NQueenProblemDemo {
    public static final Attribute<Integer> COLUMN = attribute(Integer.class, "column");
    public static final Attribute<Integer> ROW = attribute(Integer.class, "row");
    
    public static void main(String... args) {
        final var testSubject = nQueenProblem(8, 8).asSolution();
        backtracking().optimize(testSubject);
        assertThat(testSubject.isOptimal()).isTrue();
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
