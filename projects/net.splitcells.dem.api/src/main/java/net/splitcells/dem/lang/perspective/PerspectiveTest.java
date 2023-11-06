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

import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import java.io.ByteArrayOutputStream;

import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
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
                .withChild(perspective("1\n"))
                .withChild(perspective("2\r"))
                .withChild(perspective("3"));
        requireEquals(testSubject.toJsonString(), "[\"1\\n\",\"2\\r\",\"3\"]");
    }

    @UnitTest
    public void testToJsonStringWithDictionary() {
        final var testSubject = perspective("")
                .withProperty("1", "2\n")
                .withProperty("3", "4")
                .withProperty("4", "5\r");
        requireEquals(testSubject.toJsonString(), "{\"1\":\"2\\n\",\"3\":\"4\",\"4\":\"5\\r\"}");
    }

    @UnitTest
    public void testToJsonStringWithNamedDictionary() {
        final var testSubject = perspective("name\r\n")
                .withProperty("1", "2")
                .withProperty("3", "4")
                .withProperty("4", "5");
        requireEquals(testSubject.toJsonString(), "{\"name\\r\\n\":{\"1\":\"2\",\"3\":\"4\",\"4\":\"5\"}}");
    }

    @UnitTest
    public void testToJsonStringWithNestedDictionary() {
        final var testSubject = perspective("")
                .withProperty("1", "2")
                .withChild(perspective("test")
                        .withProperty("a", "b\r")
                        .withProperty("c", "d\n")
                )
                .withProperty("3", "4");
        requireEquals(testSubject.toJsonString(), "{\"1\":\"2\",\"test\":{\"a\":\"b\\r\",\"c\":\"d\\n\"},\"3\":\"4\"}");
    }

    @UnitTest
    public void testPrintCommonMarkString() {
        final var resultData = new ByteArrayOutputStream();
        final var testData = perspective("Lorem ipsum dolor sit amet")
                .withProperty("consectetur adipiscing elit", "Cras lobortis mi risus")
                .withProperty("eu viverra purus feugiat sit amet", perspective("Fusce viverra ipsum in arcu scelerisque egestas")
                        .withProperty("Vivamus sagittis commodo eleifend", "Nullam lobortis purus ut felis viverra vulputate")
                        .withProperty("Quisque elementum vitae nulla sit amet pretium"
                                , perspective("Maecenas nunc urna").withProperty("ullamcorper dictum pellentesque in", "vehicula a magna. Vivamus luctus efficitur ex"))
                        .withProperty("el ultrices erat luctus lacinia", "Donec vestibulum semper ipsum"))
                .withProperty("sed pretium felis", "Aliquam orci nunc");
        testData.printCommonMarkString(stringSender(resultData));
        requireEquals(resultData.toString(),
                "* Lorem ipsum dolor sit amet:\n"
                        + "    * consectetur adipiscing elit:\n"
                        + "        * Cras lobortis mi risus\n"
                        + "    * eu viverra purus feugiat sit amet:\n"
                        + "        * Fusce viverra ipsum in arcu scelerisque egestas:\n"
                        + "            * Vivamus sagittis commodo eleifend:\n"
                        + "                * Nullam lobortis purus ut felis viverra vulputate\n"
                        + "            * Quisque elementum vitae nulla sit amet pretium:\n"
                        + "                * Maecenas nunc urna:\n"
                        + "                    * ullamcorper dictum pellentesque in:\n"
                        + "                        * vehicula a magna. Vivamus luctus efficitur ex\n"
                        + "            * el ultrices erat luctus lacinia:\n"
                        + "                * Donec vestibulum semper ipsum\n"
                        + "    * sed pretium felis:\n"
                        + "        * Aliquam orci nunc\n");
    }
}
