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
package net.splitcells.gel.rating.rater;

import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.view.attribute.Attribute;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.AllDifferent.allDifferent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class AllDifferentTest extends TestSuiteI {

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_rating_with_all_values_different() {
        final Attribute<Integer> attribute = attribute(Integer.class);
        final Database lineSupplier = database(attribute);
        final var testSubject = then(allDifferent(attribute));
        testSubject.rating().requireEqualsTo(noCost());
        setOfUniques(testSubject.defying()).requireEmptySet();
        testSubject.register_addition(lineSupplier.addTranslated(list(1)));
        testSubject.register_addition(lineSupplier.addTranslated(list(2)));
        setOfUniques(testSubject.defying()).requireEmptySet();
        testSubject.rating().requireEqualsTo(noCost());
        // TODO Test removal.
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_rating_with_all_values_same() {
        final Attribute<Integer> attribute = attribute(Integer.class);
        final Database lineSupplier = database(attribute);
        final var testSubject = then(allDifferent(attribute));
        testSubject.rating().requireEqualsTo(noCost());
        testSubject.register_addition(lineSupplier.addTranslated(list(1)));
        testSubject.register_addition(lineSupplier.addTranslated(list(1)));
        testSubject.rating().requireEqualsTo(cost(2.0));
        // TODO Test removal.
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_rating_with_some_values_same() {
        final var attribute = attribute(Integer.class);
        final var lineSupplier = database(attribute);
        final var testSubject = then(allDifferent(attribute));
        testSubject.rating().requireEqualsTo(noCost());
        testSubject.register_addition(lineSupplier.addTranslated(list(1)));
        testSubject.register_addition(lineSupplier.addTranslated(list(1)));
        testSubject.register_addition(lineSupplier.addTranslated(list(2)));
        testSubject.rating().requireEqualsTo(cost(2.0));
        // TODO Test removal.
    }
}
