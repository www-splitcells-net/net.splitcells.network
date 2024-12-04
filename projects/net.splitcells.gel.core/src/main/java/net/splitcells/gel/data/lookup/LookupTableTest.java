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

import net.splitcells.gel.Gel;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.optimization.primitive.LinearDeinitializer.linearDeinitializer;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;

public class LookupTableTest {
    @Test
    public void testLookup() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = Gel.defineProblem("testLookup")
                .withDemandAttributes(a, b)
                .withDemands(list(list(1, 1), list(1, 2)))
                .withSupplyAttributes()
                .withNoSupplies()
                .withConstraint(forAll())
                .toProblem()
                .asSolution();
        testSubject.persistedLookup(a, 1).persistedLookup(b, 2).unorderedLines();
        testSubject.optimize(offlineLinearInitialization());
        testSubject.persistedLookup(a, 1).persistedLookup(b, 2).unorderedLines();
        testSubject.optimize(linearDeinitializer());
        testSubject.persistedLookup(a, 1).persistedLookup(b, 2).unorderedLines();
        testSubject.optimize(offlineLinearInitialization());
        testSubject.persistedLookup(a, 1).persistedLookup(b, 2).unorderedLines();
        testSubject.optimize(linearDeinitializer());
        testSubject.persistedLookup(a, 1).persistedLookup(b, 2).unorderedLines();
    }
}
