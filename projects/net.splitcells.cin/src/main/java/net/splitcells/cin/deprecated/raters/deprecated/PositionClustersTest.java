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
package net.splitcells.cin.deprecated.raters.deprecated;

import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.cin.deprecated.raters.deprecated.PositionClusters.groupNameOfPositionCluster;
import static net.splitcells.cin.deprecated.raters.deprecated.PositionClusters.positionClustering;
import static net.splitcells.cin.deprecated.raters.deprecated.PositionClusters.positionClusters;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.common.Language.FOR_ALL;
import static net.splitcells.gel.constraint.Constraint.RESULTING_CONSTRAINT_GROUP;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class PositionClustersTest {

    @Test
    public void testAllPositionClusterOfSinglePosition() {
        final var x = attribute(Integer.class, "x");
        final var y = attribute(Integer.class, "y");
        final var testSubject = defineProblem("testTopRightPositionCluster")
                .withDemandAttributes(x, y)
                .withDemands(list(list(2, 2)))
                .withSupplyAttributes()
                .withSupplies(list(list()))
                .withConstraint(c -> {
                    c.forAll(positionClustering(x, y)).forAll();
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.assign(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().child(0).child(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .requireContentsOf(list(FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 3, 0, 1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(3, 3, 1, 1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(3, 1, 1, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(3, 2, 1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 2, 0, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, 2, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, 1, -1, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, 3, -1, 1)));
    }

    @Test
    public void testTopRightPositionCluster() {
        final var x = attribute(Integer.class, "x");
        final var y = attribute(Integer.class, "y");
        final var testSubject = defineProblem("testTopRightPositionCluster")
                .withDemandAttributes(x, y)
                .withDemands(list(
                        list(1, 1)
                        , list(1, 2)
                        , list(2, 2)
                        , list(2, 1)
                        , list(2, 0)
                        , list(1, 0)
                        , list(0, 0)
                        , list(0, 1)
                        , list(0, 2)
                        , list(1, 0)
                        , list(4, 4)
                        , list(4, 5)
                        , list(5, 5)
                        , list(5, 4)
                        , list(5, 3)
                        , list(4, 3)
                        , list(3, 3)
                        , list(3, 4)
                        , list(3, 5)
                        , list(4, 3)
                ))
                .withSupplyAttributes()
                .withSupplies(list(
                        list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                ))
                .withConstraint(c -> {
                    c.forAll(positionClusters(x, y)).forAll();
                    return c;
                })
                .toProblem()
                .asSolution();
        rangeClosed(1, 20).forEach(i -> testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .requireEquals(list(FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, 4, 0, 0)));
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines()
                .requireSizeOf(20);
        rangeClosed(1, 20).forEach(i -> testSubject.undoAssignment(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0)));
        rangeClosed(1, 20).forEach(i -> testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0)));
        rangeClosed(1, 20).forEach(i -> testSubject.undoAssignment(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0)));
        rangeClosed(1, 20).forEach(i -> testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0)));
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines()
                .requireSizeOf(20);
        rangeClosed(1, 20).forEach(i -> testSubject.undoAssignment(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .requireEquals(list());
    }

    @Test
    public void testBottomRightPositionCluster() {
        final var x = attribute(Integer.class, "x");
        final var y = attribute(Integer.class, "y");
        final var testSubject = defineProblem("testBottomRightPositionCluster")
                .withDemandAttributes(x, y)
                .withDemands(list(
                        list(1, -2)
                        , list(1, -1)
                        , list(2, -1)
                        , list(2, -2)
                        , list(2, -3)
                        , list(1, -3)
                        , list(0, -3)
                        , list(0, -2)
                        , list(0, -1)
                        , list(1, -3)
                        , list(4, -5)
                        , list(4, -4)
                        , list(5, -4)
                        , list(5, -5)
                        , list(5, -6)
                        , list(4, -6)
                        , list(3, -6)
                        , list(3, -5)
                        , list(3, -4)
                        , list(4, -6)
                ))
                .withSupplyAttributes()
                .withSupplies(list(
                        list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                ))
                .withConstraint(c -> {
                    c.forAll(positionClusters(x, y));
                    return c;
                })
                .toProblem()
                .asSolution();
        rangeClosed(1, 20).forEach(i -> testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .requireEquals(list(FOR_ALL.value() + " " + groupNameOfPositionCluster(1, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(1, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(4, -5, 0, 0)));
    }

    @Test
    public void testBottomLeftPositionCluster() {
        final var x = attribute(Integer.class, "x");
        final var y = attribute(Integer.class, "y");
        final var testSubject = defineProblem("testBottomLeftPositionCluster")
                .withDemandAttributes(x, y)
                .withDemands(list(
                        list(-2, -2)
                        , list(-2, -1)
                        , list(-1, -1)
                        , list(-1, -2)
                        , list(-1, -3)
                        , list(-2, -3)
                        , list(-3, -3)
                        , list(-3, -2)
                        , list(-3, -1)
                        , list(-2, -3)
                        , list(-5, -5)
                        , list(-5, -4)
                        , list(-4, -4)
                        , list(-4, -5)
                        , list(-4, -6)
                        , list(-5, -6)
                        , list(-6, -6)
                        , list(-6, -5)
                        , list(-6, -4)
                        , list(-5, -6)
                ))
                .withSupplyAttributes()
                .withSupplies(list(
                        list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                ))
                .withConstraint(c -> {
                    c.forAll(positionClusters(x, y));
                    return c;
                })
                .toProblem()
                .asSolution();
        rangeClosed(1, 20).forEach(i -> testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .requireEquals(list(FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, -2, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, -5, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, -5, 0, 0)));
    }

    @Test
    public void testTopLeftPositionCluster() {
        final var x = attribute(Integer.class, "x");
        final var y = attribute(Integer.class, "y");
        final var testSubject = defineProblem("testTopLeftPositionCluster")
                .withDemandAttributes(x, y)
                .withDemands(list(
                        list(-2, 1)
                        , list(-2, 2)
                        , list(-1, 2)
                        , list(-1, 1)
                        , list(-1, 0)
                        , list(-2, 0)
                        , list(-3, 0)
                        , list(-3, 1)
                        , list(-3, 2)
                        , list(-2, 0)
                        , list(-5, 4)
                        , list(-5, 5)
                        , list(-4, 5)
                        , list(-4, 4)
                        , list(-4, 3)
                        , list(-5, 3)
                        , list(-6, 3)
                        , list(-6, 4)
                        , list(-6, 5)
                        , list(-5, 3)
                ))
                .withSupplyAttributes()
                .withSupplies(list(
                        list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                ))
                .withConstraint(c -> {
                    c.forAll(positionClusters(x, y));
                    return c;
                })
                .toProblem()
                .asSolution();
        rangeClosed(1, 20).forEach(i -> testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .requireEquals(list(FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-2, 1, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, 4, 0, 0)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-5, 4, 0, 0)));
        rangeClosed(1, 20).forEach(i -> testSubject.undoAssignment(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .requireEquals(list());
    }

    @Test
    public void testTopRightPositionClusterWithOffset() {
        final var x = attribute(Integer.class, "x");
        final var y = attribute(Integer.class, "y");
        final var testSubject = defineProblem("testTopRightPositionClusterWithOffset")
                .withDemandAttributes(x, y)
                .withDemands(list(
                        list(1, 1)
                        , list(1, 2)
                        , list(2, 2)
                        , list(2, 1)
                        , list(2, 0)
                        , list(1, 0)
                        , list(0, 0)
                        , list(0, 1)
                        , list(0, 2)
                        , list(1, 0)
                        , list(4, 4)
                        , list(4, 5)
                        , list(5, 5)
                        , list(5, 4)
                        , list(5, 3)
                        , list(4, 3)
                        , list(3, 3)
                        , list(3, 4)
                        , list(3, 5)
                        , list(4, 3)
                ))
                .withSupplyAttributes()
                .withSupplies(list(
                        list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                ))
                .withConstraint(c -> {
                    c.forAll(positionClusters(x, y, -1, -1));
                    return c;
                })
                .toProblem()
                .asSolution();
        rangeClosed(1, 20).forEach(i -> testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .requireEquals(list(FOR_ALL.value() + " " + groupNameOfPositionCluster(2, 2, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, 2, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, 2, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, 2, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, -3, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, -3, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-3, -3, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-3, 2, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(-3, 2, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, -3, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(5, 5, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(5, 5, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(5, 5, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(5, 5, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(5, 2, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(5, 2, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, 2, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, 5, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(2, 5, -1, -1)
                        , FOR_ALL.value() + " " + groupNameOfPositionCluster(5, 2, -1, -1)));
        rangeClosed(1, 20).forEach(i -> testSubject.undoAssignment(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .requireEquals(list());
    }
}