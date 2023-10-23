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
package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.testing.Assertions.requireEquals;

public class PerspectiveTest {
    @UnitTest
    public void testToXmlString() {
        final var article = perspective("article", SEW);
        final var content = perspective("content", SEW);
        content.withChild(perspective("deck", SEW));
        article.withChild(content);
        requireEquals(article.toXmlString(true)
                , "<article xmlns=\"http://splitcells.net/sew.xsd\"><content><deck/></content></article>");
    }

    @UnitTest
    public void testWithPath() {
        final var testSubject = perspective("article", SEW)
                .withPath(perspective("content", SEW)
                        , perspective("deck", SEW));
        requireEquals(testSubject.toXmlString(true)
                , "<article xmlns=\"http://splitcells.net/sew.xsd\"><content><deck/></content></article>");
    }

    @UnitTest
    public void testToJsonStringWithArray() {
        final var testSubject = perspective("")
                .withChild(perspective("1"))
                .withChild(perspective("2"))
                .withChild(perspective("3"));
        requireEquals(testSubject.toJsonString(), "[\"1\",\"2\",\"3\"]");
    }

    @UnitTest
    public void testToJsonStringWithDictionary() {
        final var testSubject = perspective("")
                .withProperty("1", "2")
                .withProperty("3", "4")
                .withProperty("4", "5");
        requireEquals(testSubject.toJsonString(), "{\"1\":\"2\",\"3\":\"4\",\"4\":\"5\"}");
    }

    @UnitTest
    public void testToJsonStringWithNamedDictionary() {
        final var testSubject = perspective("name")
                .withProperty("1", "2")
                .withProperty("3", "4")
                .withProperty("4", "5");
        requireEquals(testSubject.toJsonString(), "{\"name\":{\"1\":\"2\",\"3\":\"4\",\"4\":\"5\"}}");
    }

    @UnitTest
    public void testToJsonStringWithNestedDictionary() {
        final var testSubject = perspective("")
                .withProperty("1", "2")
                .withChild(perspective("test")
                        .withProperty("a", "b")
                        .withProperty("c", "d")
                )
                .withProperty("3", "4");
        requireEquals(testSubject.toJsonString(), "{\"1\":\"2\",\"test\":{\"a\":\"b\",\"c\":\"d\"},\"3\":\"4\"}");
    }
}
