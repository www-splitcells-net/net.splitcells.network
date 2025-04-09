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
package net.splitcells.dem.object;

import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Converter.converter;
import static net.splitcells.dem.object.ConvertibleTestExampleA.convertibleTestExampleA;
import static net.splitcells.dem.testing.Assertions.requireEquals;

public class ConvertibleTest {
    @UnitTest
    public void testConvertFromTree() {
        final var testName = "test-name";
        final var testA = "3";
        final var testB = "4";
        final var input = tree("")
                .withProperty("name", testName)
                .withProperty("a", testA + "")
                .withProperty("b", testB + "");
        final var output = convertibleTestExampleA().withA("5").withB("6").withName("another-name");
        converter().convert(input, output);
        requireEquals(output.name(), testName);
        requireEquals(output.a(), testA);
        requireEquals(output.b(), testB);
    }

    @UnitTest
    public void testConvertToTree() {
        final var testName = "test-name";
        final var testA = "3";
        final var testB = "4";
        final var input = convertibleTestExampleA().withA(testA).withB(testB).withName(testName);
        final var output = tree("")
                .withProperty("name", "another-name")
                .withProperty("a",  "5")
                .withProperty("b", "6");
        converter().convert(input, output);
        requireEquals(output.propertyInstance("name").orElseThrow().value().orElseThrow().name(), testName);
        requireEquals(output.propertyInstance("a").orElseThrow().value().orElseThrow().name(), testA);
        requireEquals(output.propertyInstance("b").orElseThrow().value().orElseThrow().name(), testB);
    }

    @UnitTest
    public void testConvert() {
        final var testName = "test-name";
        final var testA = "3";
        final var testB = "4";
        final var input = convertibleTestExampleA().withA(testA).withB(testB).withName(testName);
        final var output = convertibleTestExampleA().withA("5").withB("6").withName("another-name");
        converter().convert(input, output);
        requireEquals(output.name(), testName);
        requireEquals(output.a(), testA);
        requireEquals(output.b(), testB);
    }
}
