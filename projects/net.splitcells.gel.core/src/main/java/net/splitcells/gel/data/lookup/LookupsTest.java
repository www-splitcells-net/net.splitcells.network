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
package net.splitcells.gel.data.lookup;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.GroupSelectors.groupSelector;

public class LookupsTest {
    @Test
    @Disabled
    public void testPerformance() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = table("testPerformance", a, b);
        rangeClosed(1, 100000).forEach(i ->
                testSubject.addTranslated(list(i, (Object) modulus(i, 10))));
        testSubject.persistedLookup(b, 1);
        testSubject.persistedLookup(a, 1);
        testSubject.persistedLookup(b, 1).persistedLookup(a, 1);
    }

    @Test
    public void testPersistedCorrectness() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = table("test-subject", a, b);
        testSubject.persistedLookup(a, 0).unorderedLines().requireEmpty();
        testSubject.persistedLookup(b, 0).unorderedLines().requireEmpty();
        testSubject.addTranslated(list(1, 2));
        testSubject.persistedLookup(a, 0).unorderedLines().requireEmpty();
        testSubject.persistedLookup(b, 0).unorderedLines().requireEmpty();
        testSubject.persistedLookup(a, 2).unorderedLines().requireEmpty();
        testSubject.persistedLookup(b, 1).unorderedLines().requireEmpty();
        testSubject.persistedLookup(a, 1).unorderedLines().requireSizeOf(1);
        testSubject.persistedLookup(b, 2).unorderedLines().requireSizeOf(1);
    }

}
