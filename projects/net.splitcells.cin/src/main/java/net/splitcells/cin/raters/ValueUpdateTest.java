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
package net.splitcells.cin.raters;

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.cin.EntityManager.ADD_VALUE;
import static net.splitcells.cin.EntityManager.EVENT_TYPE;
import static net.splitcells.cin.EntityManager.PLAYER_ATTRIBUTE;
import static net.splitcells.cin.EntityManager.PLAYER_ENERGY;
import static net.splitcells.cin.EntityManager.PLAYER_VALUE;
import static net.splitcells.cin.EntityManager.RESULT_VALUE;
import static net.splitcells.cin.EntityManager.SET_VALUE;
import static net.splitcells.cin.EntityManager.TIME;
import static net.splitcells.cin.raters.ValueUpdate.valueUpdate;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.proposal.Proposal.EXISTING_ASSIGNMENT;
import static net.splitcells.gel.proposal.Proposal.EXISTING_DEMAND;
import static net.splitcells.gel.proposal.Proposals.proposalsForGroups;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;

public class ValueUpdateTest {
    @UnitTest
    public void test() {
        final var testSubject = defineProblem("entity-manager")
                .withDemandAttributes(TIME)
                .withNoDemands()
                .withSupplyAttributes(PLAYER_ATTRIBUTE, PLAYER_VALUE, EVENT_TYPE)
                .withSupplies()
                .withConstraint(query -> {
                    query.then(valueUpdate(PLAYER_ENERGY));
                    return query;
                })
                .toProblem()
                .asSolution();
        final var demands = testSubject.demands();
        final var supplies = testSubject.supplies();
        final int startTime = 1;
        final int endTime = 2;
        testSubject.constraint().rating().requireEqualsTo(noCost());

        final var assign0 = testSubject.assign(demands.addTranslated(listWithValuesOf(startTime))
                , supplies.addTranslated(listWithValuesOf(PLAYER_ENERGY, 0, RESULT_VALUE)));
        testSubject.constraint().rating().requireEqualsTo(cost(1));
        final var proposalsForGroups0a = proposalsForGroups(testSubject
                , list(testSubject.constraint(), testSubject.constraint().child(0))
                , testSubject.unorderedLines());
        proposalsForGroups0a.get(0).contextAssignments().unorderedLines().requireSizeOf(1);
        proposalsForGroups0a.get(0).proposedAllocationsWithSupplies().unorderedLines().requireEmpty();
        proposalsForGroups0a.get(0).proposedDisallocations().unorderedLines().requireEmpty();
        proposalsForGroups0a.get(1).contextAssignments().unorderedLines().requireSizeOf(1);
        proposalsForGroups0a.get(1).proposedAllocationsWithSupplies().unorderedLines().requireEmpty();
        proposalsForGroups0a.get(1).proposedDisallocations().unorderedLines().requireEmpty();
        final var proposalsForGroups0b = testSubject.propose();
        proposalsForGroups0b.contextAssignments().unorderedLines().requireSizeOf(1);
        proposalsForGroups0b.proposedAllocationsWithSupplies().unorderedLines().requireEmpty();
        proposalsForGroups0b.proposedDisallocations().unorderedLines().requireEmpty();
        proposalsForGroups0b.contextAssignments().unorderedLines().requireSizeOf(1);
        proposalsForGroups0b.proposedAllocationsWithSupplies().unorderedLines().requireEmpty();
        proposalsForGroups0b.proposedDisallocations().unorderedLines().requireEmpty();

        final var assign1 = testSubject.assign(demands.addTranslated(listWithValuesOf(endTime))
                , supplies.addTranslated(listWithValuesOf(PLAYER_ENERGY, 1, RESULT_VALUE)));
        testSubject.constraint().rating().requireEqualsTo(cost(2));
        final var proposalsForGroups1a = proposalsForGroups(testSubject
                , list(testSubject.constraint(), testSubject.constraint().child(0))
                , testSubject.unorderedLines());
        proposalsForGroups1a.get(1).contextAssignments().requireSizeOf(2);
        proposalsForGroups1a.get(1).proposedDisallocations()
                .requireSizeOf(1)
                .unorderedLinesStream2()
                .filter(l -> assign1.equalsTo(l.value(EXISTING_ASSIGNMENT)))
                .requireSizeOf(1);
        proposalsForGroups1a.get(1).proposedAllocationsWithSupplies().requireSizeOf(1)
                .unorderedLinesStream2()
                .filter(l ->
                        testSubject.demandOfAssignment(l.value(EXISTING_DEMAND)).equalsTo(l.value(EXISTING_DEMAND))
                                && Thing.equals(l.value(PLAYER_ATTRIBUTE), null)
                                && Thing.equals(l.value(PLAYER_VALUE), 0)
                                && Thing.equals(l.value(EVENT_TYPE), null))
                .requireSizeOf(1);
        final var proposalsForGroups1b = testSubject.propose();
        proposalsForGroups1b.contextAssignments().requireSizeOf(2);
        proposalsForGroups1b.proposedDisallocations()
                .requireSizeOf(1)
                .unorderedLinesStream2()
                .filter(l -> assign1.equalsTo(l.value(EXISTING_ASSIGNMENT)))
                .requireSizeOf(1);
        proposalsForGroups1b.proposedAllocationsWithSupplies().requireSizeOf(1)
                .unorderedLinesStream2()
                .filter(l ->
                        testSubject.demandOfAssignment(l.value(EXISTING_DEMAND)).equalsTo(l.value(EXISTING_DEMAND))
                                && Thing.equals(l.value(PLAYER_ATTRIBUTE), null)
                                && Thing.equals(l.value(PLAYER_VALUE), 0)
                                && Thing.equals(l.value(EVENT_TYPE), null))
                .requireSizeOf(1);

        final var assign2 = testSubject.assign(demands.addTranslated(listWithValuesOf(endTime))
                , supplies.addTranslated(listWithValuesOf(PLAYER_ENERGY, 1, SET_VALUE)));
        testSubject.constraint().rating().requireEqualsTo(noCost());
        final var proposalsForGroups2a = testSubject.propose();
        proposalsForGroups2a.contextAssignments().requireSizeOf(3);
        proposalsForGroups2a.proposedDisallocations().requireSizeOf(0);
        proposalsForGroups2a.proposedAllocationsWithSupplies().requireSizeOf(0);
        final var proposalsForGroups2b = testSubject.propose();
        proposalsForGroups2b.contextAssignments().requireSizeOf(3);
        proposalsForGroups2b.proposedDisallocations().requireSizeOf(0);
        proposalsForGroups2b.proposedAllocationsWithSupplies().requireSizeOf(0);

        final var assign3 = testSubject.assign(demands.addTranslated(listWithValuesOf(endTime))
                , supplies.addTranslated(listWithValuesOf(PLAYER_ENERGY, 1, ADD_VALUE)));
        testSubject.constraint().rating().requireEqualsTo(cost(4));
        final var proposalsForGroups3a = proposalsForGroups(testSubject
                , list(testSubject.constraint(), testSubject.constraint().child(0))
                , testSubject.unorderedLines());
        proposalsForGroups3a.get(1).contextAssignments().requireSizeOf(4);
        proposalsForGroups3a.get(1).proposedDisallocations().requireSizeOf(1).unorderedLinesStream2()
                .filter(l -> assign1.equalsTo(l.value(EXISTING_ASSIGNMENT)))
                .requireSizeOf(1);
        proposalsForGroups3a.get(1).proposedAllocationsWithSupplies().requireSizeOf(1)
                .unorderedLinesStream2()
                .filter(l ->
                        testSubject.demandOfAssignment(l.value(EXISTING_DEMAND)).equalsTo(l.value(EXISTING_DEMAND))
                                && Thing.equals(l.value(PLAYER_ATTRIBUTE), null)
                                && Thing.equals(l.value(PLAYER_VALUE), 2)
                                && Thing.equals(l.value(EVENT_TYPE), null))
                .requireSizeOf(1);
        final var proposalsForGroups3b = testSubject.propose();
        proposalsForGroups3b.contextAssignments().requireSizeOf(4);
        proposalsForGroups3b.proposedDisallocations().requireSizeOf(1).unorderedLinesStream2()
                .filter(l -> assign1.equalsTo(l.value(EXISTING_ASSIGNMENT)))
                .requireSizeOf(1);
        proposalsForGroups3b.proposedAllocationsWithSupplies().requireSizeOf(1)
                .unorderedLinesStream2()
                .filter(l ->
                        testSubject.demandOfAssignment(l.value(EXISTING_DEMAND)).equalsTo(l.value(EXISTING_DEMAND))
                                && Thing.equals(l.value(PLAYER_ATTRIBUTE), null)
                                && Thing.equals(l.value(PLAYER_VALUE), 2)
                                && Thing.equals(l.value(EVENT_TYPE), null))
                .requireSizeOf(1);

        final var assign4 = testSubject.assign(demands.addTranslated(listWithValuesOf(endTime))
                , supplies.addTranslated(listWithValuesOf(PLAYER_ENERGY, 1, ADD_VALUE)));
        testSubject.constraint().rating().requireEqualsTo(cost(10));
        final var proposalsForGroups4a = proposalsForGroups(testSubject
                , list(testSubject.constraint(), testSubject.constraint().child(0))
                , testSubject.unorderedLines());
        proposalsForGroups4a.get(1).contextAssignments().requireSizeOf(5);
        proposalsForGroups4a.get(1).proposedDisallocations().requireSizeOf(1).unorderedLinesStream2()
                .filter(l -> assign1.equalsTo(l.value(EXISTING_ASSIGNMENT)))
                .requireSizeOf(1);
        proposalsForGroups4a.get(1).proposedAllocationsWithSupplies().requireSizeOf(1)
                .unorderedLinesStream2()
                .filter(l ->
                        testSubject.demandOfAssignment(l.value(EXISTING_DEMAND)).equalsTo(l.value(EXISTING_DEMAND))
                                && Thing.equals(l.value(PLAYER_ATTRIBUTE), null)
                                && Thing.equals(l.value(PLAYER_VALUE), 3)
                                && Thing.equals(l.value(EVENT_TYPE), null))
                .requireSizeOf(1);
        final var proposalsForGroups4b = testSubject.propose();
        proposalsForGroups4b.contextAssignments().requireSizeOf(5);
        proposalsForGroups4b.proposedDisallocations().requireSizeOf(1).unorderedLinesStream2()
                .filter(l -> assign1.equalsTo(l.value(EXISTING_ASSIGNMENT)))
                .requireSizeOf(1);
        proposalsForGroups4b.proposedAllocationsWithSupplies().requireSizeOf(1)
                .unorderedLinesStream2()
                .filter(l ->
                        testSubject.demandOfAssignment(l.value(EXISTING_DEMAND)).equalsTo(l.value(EXISTING_DEMAND))
                                && Thing.equals(l.value(PLAYER_ATTRIBUTE), null)
                                && Thing.equals(l.value(PLAYER_VALUE), 3)
                                && Thing.equals(l.value(EVENT_TYPE), null))
                .requireSizeOf(1);

        final var assign5 = testSubject.assign(demands.addTranslated(listWithValuesOf(endTime))
                , supplies.addTranslated(listWithValuesOf(PLAYER_ENERGY, 1, ADD_VALUE)));
        testSubject.constraint().rating().requireEqualsTo(cost(18));
        final var proposalsForGroups5a = proposalsForGroups(testSubject
                , list(testSubject.constraint(), testSubject.constraint().child(0))
                , testSubject.unorderedLines());
        proposalsForGroups5a.get(1).contextAssignments().requireSizeOf(6);
        proposalsForGroups5a.get(1).proposedDisallocations().requireSizeOf(1).unorderedLinesStream2()
                .filter(l -> assign1.equalsTo(l.value(EXISTING_ASSIGNMENT)))
                .requireSizeOf(1);
        proposalsForGroups5a.get(1).proposedAllocationsWithSupplies().requireSizeOf(1)
                .unorderedLinesStream2()
                .filter(l ->
                        testSubject.demandOfAssignment(l.value(EXISTING_DEMAND)).equalsTo(l.value(EXISTING_DEMAND))
                                && Thing.equals(l.value(PLAYER_ATTRIBUTE), null)
                                && Thing.equals(l.value(PLAYER_VALUE), 4)
                                && Thing.equals(l.value(EVENT_TYPE), null))
                .requireSizeOf(1);
        final var proposalsForGroups5b = testSubject.propose();
        proposalsForGroups5b.contextAssignments().requireSizeOf(6);
        proposalsForGroups5b.proposedDisallocations().requireSizeOf(1).unorderedLinesStream2()
                .filter(l -> assign1.equalsTo(l.value(EXISTING_ASSIGNMENT)))
                .requireSizeOf(1);
        proposalsForGroups5b.proposedAllocationsWithSupplies().requireSizeOf(1)
                .unorderedLinesStream2()
                .filter(l ->
                        testSubject.demandOfAssignment(l.value(EXISTING_DEMAND)).equalsTo(l.value(EXISTING_DEMAND))
                                && Thing.equals(l.value(PLAYER_ATTRIBUTE), null)
                                && Thing.equals(l.value(PLAYER_VALUE), 4)
                                && Thing.equals(l.value(EVENT_TYPE), null))
                .requireSizeOf(1);

        testSubject.remove(assign5);
        testSubject.constraint().rating().requireEqualsTo(cost(10));
        testSubject.remove(assign4);
        testSubject.constraint().rating().requireEqualsTo(cost(4));
        testSubject.remove(assign3);
        testSubject.constraint().rating().requireEqualsTo(noCost());
        testSubject.remove(assign2);
        testSubject.constraint().rating().requireEqualsTo(cost(2));
        testSubject.remove(assign1);
        testSubject.constraint().rating().requireEqualsTo(cost(1));
        testSubject.remove(assign0);
        testSubject.constraint().rating().requireEqualsTo(noCost());
    }
}
