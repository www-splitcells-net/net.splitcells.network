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
package net.splitcells.gel.constraint;

import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.ExecutionException;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.assertThrows;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.constraint.type.ForAll.FOR_ALL_NAME;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.classification.ForAllAttributeValues.forAllAttributeValues;
import static net.splitcells.gel.rating.rater.lib.classification.ForAllValueCombinations.FOR_ALL_VALUE_COMBINATIONS_NAME;

public class QueryTest {
    @UnitTest
    public void testConstraintPath() {
        final var d = attribute(Integer.class, "d");
        final var s = attribute(Integer.class, "s");
        final var testData = defineProblem("testConstraintPath")
                .withDemandAttributes(d)
                .withNoDemands()
                .withSupplyAttributes(s)
                .withNoSupplies()
                .withConstraint(r -> {
                    r.forAll(d).forAll(s).forAll(d);
                    return r;
                }).toProblem()
                .asSolution();
        final var testProduct = testData.constraint().query().forAll(d).forAll(s).forAll(d).constraintPath();
        testProduct.assertEquals(list(testData.constraint()
                , testData.constraint().childrenView().get(0)
                , testData.constraint().childrenView().get(0).childrenView().get(0)
                , testData.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0)));
    }

    @UnitTest
    public void testForWithMultipleClassifiers() {
        final var d = attribute(Integer.class, "d");
        final var s = attribute(Integer.class, "s");
        final var testSubject = defineProblem("testForWithMultipleClassifiers")
                .withDemandAttributes(d)
                .withNoDemands()
                .withSupplyAttributes(s)
                .withNoSupplies()
                .withConstraint(r -> {
                    r.forAll(list(forAllAttributeValues(d), forAllAttributeValues(s))).forAll(forAllAttributeValues(d));
                    return r;
                }).toProblem()
                .asSolution();
        final var testResult = testSubject.constraint();
        testResult.childrenView().requireSizeOf(2);
        testResult.childrenView().get(0).childrenView().requireSizeOf(1);
        testResult.childrenView().get(1).childrenView().requireSizeOf(1);
        testResult.childrenView().get(0).childrenView().get(0).childrenView().requireSizeOf(1);
        testResult.childrenView().get(0).childrenView().get(0).childrenView().get(0).childrenView().requireSizeOf(0);
    }

    @UnitTest
    public void testParseConstraintErrorForAllWithAttributes() {
        assertThrows(ExecutionException.class
                , () -> {
                    final var d = attribute(Integer.class, "d");
                    final var s = attribute(Integer.class, "s");
                    defineProblem("testConstraintErrorForAllWithAttributes")
                            .withDemandAttributes(d)
                            .withNoDemands()
                            .withSupplyAttributes(s)
                            .withNoSupplies()
                            .withConstraint(r -> {
                                r.parseConstraint(FOR_ALL_NAME, list(hasSize(1)), list(d));
                                return r;
                            });
                });
    }

    @UnitTest
    public void testParseConstraintThenWithTooManyRaters() {
        assertThrows(ExecutionException.class
                , () -> {
                    final var d = attribute(Integer.class, "d");
                    final var s = attribute(Integer.class, "s");
                    defineProblem("testConstraintThenWithTooManyRaters")
                            .withDemandAttributes(d)
                            .withNoDemands()
                            .withSupplyAttributes(s)
                            .withNoSupplies()
                            .withConstraint(r -> {
                                r.parseConstraint(THEN_NAME, list(hasSize(1), hasSize(1)), list());
                                return r;
                            });
                });
    }

    @UnitTest
    public void testParseConstraintThenWithAttributes() {
        assertThrows(ExecutionException.class
                , () -> {
                    final var d = attribute(Integer.class, "d");
                    final var s = attribute(Integer.class, "s");
                    defineProblem("testConstraintThenWithAttributes")
                            .withDemandAttributes(d)
                            .withNoDemands()
                            .withSupplyAttributes(s)
                            .withNoSupplies()
                            .withConstraint(r -> {
                                r.parseConstraint(THEN_NAME, list(hasSize(1)), list(s));
                                return r;
                            });
                });
    }

    @UnitTest
    public void testParseConstraintForAllCombinationsWithRaters() {
        assertThrows(ExecutionException.class
                , () -> {
                    final var d = attribute(Integer.class, "d");
                    final var s = attribute(Integer.class, "s");
                    defineProblem("testConstraintThenWithAttributes")
                            .withDemandAttributes(d)
                            .withNoDemands()
                            .withSupplyAttributes(s)
                            .withNoSupplies()
                            .withConstraint(r -> {
                                r.parseConstraint(FOR_ALL_VALUE_COMBINATIONS_NAME, list(hasSize(1)), list(s));
                                return r;
                            });
                });
    }

}
