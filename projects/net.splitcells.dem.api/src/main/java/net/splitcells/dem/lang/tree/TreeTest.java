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
package net.splitcells.dem.lang.tree;

import lombok.val;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.tree.Tree.JSON_ARRAY;
import static net.splitcells.dem.lang.tree.Tree.JSON_OBJECT;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.BinaryUtils.binaryOutputStream;

public class TreeTest {

    @UnitTest
    public void testXmlName() {
        requireEquals(tree("&<>\"'~").xmlName(), "&amp;&lt;&gt;&quot;&apos;&Tilde;");
    }

    @UnitTest
    public void testToXmlString() {
        final var article = TreeI.tree("article", SEW);
        final var content = TreeI.tree("content", SEW);
        content.withChild(TreeI.tree("deck", SEW));
        article.withChild(content);
        requireEquals(article.toXmlString(true)
                , "<article xmlns=\"http://splitcells.net/sew.xsd\"><content><deck/></content></article>");
    }

    @UnitTest
    public void testWithPath() {
        final var testSubject = TreeI.tree("article", SEW)
                .withPath(TreeI.tree("content", SEW)
                        , TreeI.tree("deck", SEW));
        requireEquals(testSubject.toXmlString(true)
                , "<article xmlns=\"http://splitcells.net/sew.xsd\"><content><deck/></content></article>");
    }

    @UnitTest
    public void testToJsonStringWithPathToDictionary() {
        final var testSubject = tree("")
                .withChild(tree("path start")
                        .withChild(tree("path end")
                                .withProperty("a", "b")
                                .withProperty("b", "c")));
        requireEquals(testSubject.toJsonString(), "{\"\":{\"path start\":{\"path end\":{\"a\":\"b\",\"b\":\"c\"}}}}");
    }

    @UnitTest
    public void testToJsonWithExplicitArray() {
        final var testSubject = tree("").withChildren(
                tree(JSON_ARRAY, JSON).withChildren(tree("1"))
                , tree("children").withChild(tree(JSON_ARRAY, JSON).withChildren(tree("2")))
                , tree(JSON_ARRAY, JSON).withChildren(tree("3"), tree("5"))
        );
        requireEquals(testSubject.toJsonString(), "{\"\":[[\"1\"],\"children\":[\"2\"],[\"3\",\"5\"]]}");
    }

    @UnitTest
    public void testToJsonWithExplicitObject() {
        final var testSubject = tree(JSON_OBJECT, JSON).withChildren(
                tree(JSON_OBJECT, JSON).withChildren(tree("1").withChild(tree("2")))
                , tree("4").withChild(tree("5"))
        );
        requireEquals(testSubject.toJsonString(), "{{\"1\":\"2\"},\"4\":\"5\"}");
    }

    @UnitTest
    public void testToJsonStringWithPathToArray() {
        final var testSubject = tree("")
                .withChild(tree("path start")
                        .withChild(tree("path end")
                                .withChildren(tree("1"), tree("2"))));
        requireEquals(testSubject.toJsonString(), "{\"\":{\"path start\":{\"path end\":[\"1\",\"2\"]}}}");
    }

    @UnitTest
    public void testToJsonStringWithNamedArray() {
        final var testSubject = tree("")
                .withChild(tree("name")
                        .withChildren(tree("3"), tree("4")));
        requireEquals(testSubject.toJsonString(), "{\"\":{\"name\":[\"3\",\"4\"]}}");
    }

    @UnitTest
    public void testEncodeJsonString() {
        final var testSubject = tree("").withChild(tree("\\\n\r\t\""));
        requireEquals(testSubject.toJsonString(), "{\"\":\"\\\\\\n\\r\\t\\\"\"}");
    }

    @UnitTest
    public void testToJsonStringWithArray() {
        final var testSubject = tree("")
                .withChild(tree("1\n"))
                .withChild(tree("2\r"))
                .withChild(tree("3"));
        requireEquals(testSubject.toJsonString(), "{\"\":[\"1\\n\",\"2\\r\",\"3\"]}");
    }

    @UnitTest
    public void testToJsonStringWithDictionary() {
        final var testSubject = tree("")
                .withProperty("1", "2\n")
                .withProperty("3", "4")
                .withProperty("4", "5\r");
        requireEquals(testSubject.toJsonString(), "{\"\":{\"1\":\"2\\n\",\"3\":\"4\",\"4\":\"5\\r\"}}");
    }

    @UnitTest
    public void testToJsonStringWithNamedDictionary() {
        final var testSubject = tree("").withChild(
                tree("name\r\n")
                        .withProperty("1", "2")
                        .withProperty("3", "4")
                        .withProperty("4", "5"));
        requireEquals(testSubject.toJsonString(), "{\"\":{\"name\\r\\n\":{\"1\":\"2\",\"3\":\"4\",\"4\":\"5\"}}}");
    }

    @UnitTest
    public void testToJsonStringWithNestedDictionary() {
        final var testSubject = tree("")
                .withProperty("1", "2")
                .withChild(tree("test")
                        .withProperty("a", "b\r")
                        .withProperty("c", "d\n")
                )
                .withProperty("3", "4");
        requireEquals(testSubject.toJsonString(), "{\"\":{\"1\":\"2\",\"test\":{\"a\":\"b\\r\",\"c\":\"d\\n\"},\"3\":\"4\"}}");
    }

    @UnitTest
    public void testPrintCommonMarkString() {
        final var resultData = binaryOutputStream();
        final var testData = tree("Lorem ipsum dolor sit amet")
                .withProperty("consectetur adipiscing elit", "Cras lobortis mi risus")
                .withProperty("eu viverra purus feugiat sit amet", tree("Fusce viverra ipsum in arcu scelerisque egestas")
                        .withProperty("Vivamus sagittis commodo eleifend", "Nullam lobortis purus ut felis viverra vulputate")
                        .withProperty("Quisque elementum vitae nulla sit amet pretium"
                                , tree("Maecenas nunc urna").withProperty("ullamcorper dictum pellentesque in", "vehicula a magna. Vivamus luctus efficitur ex"))
                        .withProperty("el ultrices erat luctus lacinia", "Donec vestibulum semper ipsum"))
                .withProperty("sed pretium felis", "Aliquam orci nunc");
        testData.printCommonMarkString(stringSender(resultData));
        requireEquals(resultData.toString(),
                """
                        * Lorem ipsum dolor sit amet:
                            * consectetur adipiscing elit: Cras lobortis mi risus
                            * eu viverra purus feugiat sit amet:
                                * Fusce viverra ipsum in arcu scelerisque egestas:
                                    * Vivamus sagittis commodo eleifend: Nullam lobortis purus ut felis viverra vulputate
                                    * Quisque elementum vitae nulla sit amet pretium:
                                        * Maecenas nunc urna:
                                            * ullamcorper dictum pellentesque in: vehicula a magna. Vivamus luctus efficitur ex
                                    * el ultrices erat luctus lacinia: Donec vestibulum semper ipsum
                            * sed pretium felis: Aliquam orci nunc
                        """);
    }

    @UnitTest
    public void testToXmlStringWithPrefixesAndGenericNameSpaceCase() {
        final var resultData = TreeI.tree("test", SEW).withChild(TreeI.tree("case", SEW))
                .toXmlStringWithPrefixes();
        requireEquals(resultData, "<s:test><s:case/></s:test>");
    }

    @UnitTest
    public void testToXmlStringWithAllNameSpaceDeclarationsAtTop() {
        final var resultData = TreeI.tree("test", SEW).withChild(TreeI.tree("case", SEW))
                .toXmlStringWithAllNameSpaceDeclarationsAtTop();
        requireEquals(resultData, "<s:test xmlns:s=\"http://splitcells.net/sew.xsd\" ><s:case/></s:test>");
    }

    @UnitTest
    public void testToHtmlString() {
        final var testSubject = tree("html", HTML);
        testSubject.withProperty("xmlns", HTML_ATTRIBUTE, "http://www.w3.org/1999/xhtml");
        testSubject.withChild(tree("body", HTML));
        requireEquals(testSubject.toHtmlString(), "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body/></html>");
    }

    @UnitTest
    public void testAsCompactXhtmlList() {
        final var testSubject = tree("root");
        testSubject.withProperty("attribute", "value");
        testSubject.withChild(tree("child"));
        requireEquals(testSubject.asCompactXhtmlList(), "<ol><li>root<ol><li>attribute<ol><li>value</li></ol></li><li>child</li></ol></li></ol>");
    }

    @UnitTest
    public void testAsCompactXhtmlListWithEmptyNodes() {
        final var testSubject = tree("");
        testSubject.withProperty("attribute", "value");
        testSubject.withChild(tree("child"));
        requireEquals(testSubject.asCompactXhtmlList(), "<ol><li>attribute<ol><li>value</li></ol></li><li>child</li></ol>");
    }

    @UnitTest
    public void testAsCompactXhtmlListWithNestedEmptyNodes() {
        final var testSubject = tree("");
        testSubject.withProperty("attribute", "value");
        testSubject.withProperty("", tree("").withProperty("", "child"));
        requireEquals(testSubject.asCompactXhtmlList(), "<ol><li>attribute<ol><li>value</li></ol></li><li>child</li></ol>");
    }

    @UnitTest
    public void testAsCompactXhtmlListWithNestedEmptyNodesWithEmptyNodes() {
        final var testSubject = tree("");
        testSubject.withChild(tree(""));
        testSubject.withProperty("attribute", "value");
        testSubject.withChild(tree(""));
        testSubject.withProperty("", tree("").withChild(tree(""))
                .withProperty("", "child"));
        requireEquals(testSubject.asCompactXhtmlList(), "<ol><li>attribute<ol><li>value</li></ol></li><li>child</li></ol>");
    }

    @UnitTest
    public void testWithMerged() {
        final var testSubject = tree("a");
        testSubject.withProperty("b", tree("c").withProperty("d", "e"));
        final var argument = tree("b").withProperty("c", tree("d").withProperty("f", "g"));
        //System.out.println(testSubject.withMerged(argument).asCompactXhtmlList());
        final var testResult = testSubject.withMerged(argument);
        requireEquals(testResult.name(), "a");
        requireEquals(testResult.childrenPath(0).name(), "b");
        testResult.children().requireSizeOf(1);
        requireEquals(testResult.childrenPath(0, 0).name(), "c");
        testResult.childrenPath(0, 0).children().requireSizeOf(1);
        requireEquals(testResult.childrenPath(0, 0, 0).name(), "d");
        testResult.childrenPath(0, 0, 0).children().requireSizeOf(2);
        requireEquals(testResult.childrenPath(0, 0, 0, 0).name(), "e");
        testResult.childrenPath(0, 0, 0, 0).children().requireEmpty();
        requireEquals(testResult.childrenPath(0, 0, 0, 1).name(), "f");
        testResult.childrenPath(0, 0, 0, 1).children().requireSizeOf(1);
        requireEquals(testResult.childrenPath(0, 0, 0, 1, 0).name(), "g");
        testResult.childrenPath(0, 0, 0, 1, 0).children().requireEmpty();
    }

    @UnitTest
    public void testChildrenPath() {
        final var testSubject = tree("a");
        testSubject.withProperty("b", tree("c").withChildren(tree("e"), tree("f")));
        requireEquals(testSubject.childrenPath(0, 0, 0).name(), "e");
        requireEquals(testSubject.childrenPath(0, 0, 1).name(), "f");
        requireEquals(testSubject.childrenPathTravel(0, 0, 0, 0).name(), "e");
        requireEquals(testSubject.childrenPathTravel(1, 0, 0, 0).name(), "c");
    }

    @UnitTest public void testEquals() {
        requireNot(tree("1").equals(trail("")));

        require(tree("1").equals(tree("1")));
        requireNot(tree("1").equals(tree("2")));
        requireNot(tree("2").equals(tree("1")));

        requireNot(tree("1", NATURAL).equals(tree("1", STRING)));
    }

    @UnitTest public void testToCompactTree() {
        val testSubject = tree("1");
        requireEquals(testSubject, testSubject.toCompactTree());
    }
}
