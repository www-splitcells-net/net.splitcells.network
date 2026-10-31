/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.tree;

import lombok.val;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import java.util.Optional;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.tree.CommonMarkConfig.commonMarkConfig;
import static net.splitcells.dem.lang.tree.JsonConfig.jsonConfig;
import static net.splitcells.dem.lang.tree.PathQueryConfig.pathQueryConfig;
import static net.splitcells.dem.lang.tree.Tree.*;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.lang.tree.XmlConfig.xmlConfig;
import static net.splitcells.dem.object.Merger.merger;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.dem.utils.BinaryUtils.binaryOutputStream;
import static net.splitcells.dem.utils.StringUtils.multiple;

public class TreeTest {

    @UnitTest public void testAsXhtmlList() {
        requireEquals(tree("a").withChildren(tree("a"), tree("b")).asXhtmlList(), "<ol><li>a</li><ol><li>a</li><li>b</li></ol></ol>");
    }

    @UnitTest public void testPathOfValueTree() {
        val a = tree("a");
        val b = tree("b");
        val c = tree("c");
        final var testData = TreeI.tree("test data").withPath(a, b, c);
        testData.pathOfValueTree("test data", "a", "b", "c").orElseThrow().requireEquals(list(a, b, c));
        requireEquals(testData.pathOfValueTree("test data", "b", "b", "c"), Optional.empty());
    }

    @UnitTest public void testExtendWith() {
        final var testData = tree("test data");
        testData.extendWith(list()).requireEqualsTo(testData);
        testData.extendWith(list("a", "b", "c"))
                .pathOfValueTree("test data", "a", "b", "c")
                .orElseThrow()
                .mapped(TreeView::name)
                .requireEquals(list("a", "b", "c"));
    }

    @UnitTest public void testPathOfDenValueTree() {
        val a = tree(VAL, DEN).withProperty(NAME, DEN, "a");
        val b = tree(VAL, DEN).withProperty(NAME, DEN, "b");
        val c = tree(VAL, DEN).withProperty(NAME, DEN, "c");
        final var testData = TreeI.tree("test data").withPath(a, b, c);
        testData.pathOfDenValueTree("a/b/c").orElseThrow().requireEquals(list(a, b, c));
        requireEquals(testData.pathOfDenValueTree("test data/a/b/c"), Optional.empty());
    }

    @UnitTest public void testSubtreeByName() {
        val a = tree("a");
        val b = tree("b");
        val c = tree("c");
        final var testData = TreeI.tree("test data").withPath(a, b, c);
        testData.subtreeByName().requireEqualsTo(testData);
        testData.subtreeByName("a", "b").requireEqualsTo(b);
    }

    @UnitTest public void testSubtree() {
        val a = tree(VAL, DEN).withProperty(NAME, DEN, "a");
        val b = tree(VAL, DEN).withProperty(NAME, DEN, "b");
        val c = tree(VAL, DEN).withProperty(NAME, DEN, "c");
        final var testData = TreeI.tree("test data").withPath(a, b, c);
        Tree.subtree(testData, list("a", "b")).requireEqualsTo(b);
    }

    @UnitTest public void testPropertiesWithValue() {
        requireEquals(tree("").withProperty("a", STRING, "b").withProperty("a", STRING, "b")
                        .propertiesWithValue("a", STRING, "b")
                , list(tree("a", STRING).withChild(tree("b", STRING)), tree("a", STRING).withChild(tree("b", STRING))));
    }

    @UnitTest public void testPropertyInstance() {
        requireEquals(tree("").propertyInstance("something"), Optional.empty());
        requireEquals(tree("").propertyInstance("something", STRING), Optional.empty());
        requireEquals(tree("").withProperty("a", STRING, "b").propertyInstance("a", STRING), Optional.of(tree("a", STRING).withChild(tree("b", STRING))));
    }

    @UnitTest public void testChildNamed() {
        val testData = tree("").withChild(tree("something", STRING).withChild(tree("abc")));
        requireEquals(testData.childNamed("anything", STRING), Optional.empty());
        requireEquals(testData.childNamed("something", STRING), Optional.of(tree("something", STRING).withChild(tree("abc"))));
    }

    @UnitTest public void testToMultilineStringPathsDescription() {
        requireEquals(toMultilineStringPathsDescription(list("a", "b")), "a\\nb");
        requireEquals(toMultilineStringPathsDescription(list()), "");
    }

    @UnitTest public void testToXmlStringWithPrefixes() {
        final var testSubject = tree("test")
                .withChild(tree("path-start")
                        .withChild(tree("path-end")
                                .withProperty("a", TEXT, tree("b", STRING))
                                .withOptionalProperty("b", Optional.of("c"))));
        requireEquals(testSubject.toXmlStringWithPrefixes(), "<nt:test><nt:path-start><nt:path-end><nt:a>b</nt:a><nt:b>c</nt:b></nt:path-end></nt:path-start></nt:test>");
        requireEquals(tree("").toXmlStringWithPrefixes(), "<empty/>");
        requireEquals(tree("a&b").toXmlStringWithPrefixes(), "a&amp;b");
        requireEquals(tree("a&b").withProperty("t", "c&d").toXmlStringWithPrefixes(), "<d:val name=\"a&amp;b\"><nt:t>c&amp;d</nt:t></d:val>");
        requireEquals(tree("html", HTML).toXmlStringWithPrefixes(), "<x:html/>");
        requireEquals(tree("html", HTML).withChild(tree("body", HTML)).toXmlStringWithPrefixes(), "<x:html><x:body/></x:html>");
        requireEquals(tree("some text", NATURAL).toXmlStringWithPrefixes(), "some text");
        requireEquals(tree("some text", DEN).toXmlStringWithPrefixes(), "some text");
        requireEquals(tree("some-text", NATURAL).toXmlStringWithPrefixes(), "<n:some-text/>");
        requireEquals(tree("some-wrapped-text", NATURAL).withChild(tree("wrap", NATURAL)).toXmlStringWithPrefixes(), "<n:some-wrapped-text><n:wrap/></n:some-wrapped-text>");
        requireEquals(tree("some-text", DEN).toXmlStringWithPrefixes(), "<d:some-text/>");
        requireEquals(tree("some-wrapped-text", DEN).withValue("wrap", DEN).toXmlStringWithPrefixes(), "<d:some-wrapped-text><d:wrap/></d:some-wrapped-text>");

    }

    @UnitTest public void testXmlName() {
        requireEquals(tree("&<>\"'~").xmlName(), "&amp;&lt;&gt;&quot;&apos;&Tilde;");
    }

    @UnitTest public void testToXmlString() {
        final var article = TreeI.tree("article", SEW);
        final var content = TreeI.tree("content", SEW);
        content.withChild(TreeI.tree("deck", SEW));
        article.withChild(content);
        requireEquals(article.toXmlString(true)
                , "<article xmlns=\"http://splitcells.net/sew.xsd\"><content><deck/></content></article>");
        requireEquals(tree("").toXmlString(false), "<empty/>");
        requireEquals(tree("", SEW).toXmlString(), "<empty/>");
        requireEquals(tree("invalid xml name?", SEW).toXmlString(xmlConfig().withPrintXmlDeclaration(false)), "invalid xml name?");
        requireEquals(tree("").toXmlString(false), "<empty/>");
        requireEquals(tree("body", HTML).toXmlString(false), "<body/>");
        requireEquals(tree("html", HTML).withChild(tree("body", HTML)).toXmlString(false), "<html><body/></html>");
        requireEquals(tree("value", DEN).toXmlString(false), "<value/>");
        requireEquals(tree("article", SEW).toXmlString(true), "<article xmlns=\"http://splitcells.net/sew.xsd\"/>");
    }

    @UnitTest public void testWithPath() {
        final var testSubject = TreeI.tree("article", SEW)
                .withPath(TreeI.tree("content", SEW)
                        , TreeI.tree("deck", SEW));
        requireEquals(testSubject.toXmlString(true)
                , "<article xmlns=\"http://splitcells.net/sew.xsd\"><content><deck/></content></article>");
    }

    @UnitTest public void testToJsonString() {
        requireEquals(tree("a").withChild(tree("b")).toJsonString(), "{\"a\":\"b\"}");
        requireEquals(tree("a").toJsonString(), "[\"a\"]");
        requireEquals(tree(JSON_ARRAY, JSON).withChildren(tree("1"), tree("2")).toJsonString(), "[\"1\",\"2\"]");
        requireThrow(() ->
                tree(JSON_OBJECT, JSON).withChildren(tree("1"), tree("2")).toJsonString());
        requireEquals(tree("a").withPath(tree("b"), tree(JSON_ARRAY, JSON)).toJsonString(), "{\"a\":{\"b\":\"array\"}}");
    }

    @UnitTest public void testToJsonStringWithPathToDictionary() {
        final var testSubject = tree("")
                .withChild(tree("path start")
                        .withChild(tree("path end")
                                .withProperty("a", "b")
                                .withProperty("b", "c")));
        requireEquals(testSubject.toJsonString(), "{\"\":{\"path start\":{\"path end\":{\"a\":\"b\",\"b\":\"c\"}}}}");
    }

    @UnitTest public void testToJsonWithExplicitArray() {
        final var testSubject = tree("").withChildren(
                tree(JSON_ARRAY, JSON).withChildren(tree("1"))
                , tree("children").withChild(tree(JSON_ARRAY, JSON).withChildren(tree("2")))
                , tree(JSON_ARRAY, JSON).withChildren(tree("3"), tree("5"))
        );
        requireEquals(testSubject.toJsonString(), "{\"\":[[\"1\"],\"children\":[\"2\"],[\"3\",\"5\"]]}");
    }

    @UnitTest public void testToJsonWithExplicitObject() {
        final var testSubject = tree(JSON_OBJECT, JSON).withChildren(
                tree(JSON_OBJECT, JSON).withChildren(tree("1").withChild(tree("2")))
                , tree("4").withChild(tree("5"))
        );
        requireEquals(testSubject.toJsonString(), "{{\"1\":\"2\"},\"4\":\"5\"}");
    }

    @UnitTest public void testToJsonStringWithPathToArray() {
        final var testSubject = tree("")
                .withChild(tree("path start")
                        .withChild(tree("path end")
                                .withChildren(tree("1"), tree("2"))));
        requireEquals(testSubject.toJsonString(), "{\"\":{\"path start\":{\"path end\":[\"1\",\"2\"]}}}");
    }

    @UnitTest public void testToJsonStringWithNamedArray() {
        final var testSubject = tree("")
                .withChild(tree("name")
                        .withChildren(tree("3"), tree("4")));
        requireEquals(testSubject.toJsonString(), "{\"\":{\"name\":[\"3\",\"4\"]}}");
    }

    @UnitTest public void testEncodeJsonString() {
        final var testSubject = tree("").withChild(tree("\\\n\r\t\""));
        requireEquals(testSubject.toJsonString(), "{\"\":\"\\\\\\n\\r\\t\\\"\"}");
    }

    @UnitTest public void testToJsonStringWithArray() {
        final var testSubject = tree("")
                .withChild(tree("1\n"))
                .withChild(tree("2\r"))
                .withChild(tree("3"));
        requireEquals(testSubject.toJsonString(), "{\"\":[\"1\\n\",\"2\\r\",\"3\"]}");
    }

    @UnitTest public void testToJsonStringWithDictionary() {
        final var testSubject = tree("")
                .withProperty("1", "2\n")
                .withProperty("3", "4")
                .withProperty("4", "5\r");
        requireEquals(testSubject.toJsonString(), "{\"\":{\"1\":\"2\\n\",\"3\":\"4\",\"4\":\"5\\r\"}}");
    }

    @UnitTest public void testToJsonStringWithNamedDictionary() {
        final var testSubject = tree("").withChild(
                tree("name\r\n")
                        .withProperty("1", "2")
                        .withProperty("3", "4")
                        .withProperty("4", "5"));
        requireEquals(testSubject.toJsonString(), "{\"\":{\"name\\r\\n\":{\"1\":\"2\",\"3\":\"4\",\"4\":\"5\"}}}");
    }

    @UnitTest public void testToJsonStringWithNestedDictionary() {
        final var testSubject = tree("")
                .withProperty("1", "2")
                .withChild(tree("test")
                        .withProperty("a", "b\r")
                        .withProperty("c", "d\n")
                )
                .withProperty("3", "4");
        requireEquals(testSubject.toJsonString(), "{\"\":{\"1\":\"2\",\"test\":{\"a\":\"b\\r\",\"c\":\"d\\n\"},\"3\":\"4\"}}");
    }

    @UnitTest public void testPrintCommonMarkString() {
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

    @UnitTest public void testToXmlStringWithPrefixesAndGenericNameSpaceCase() {
        final var resultData = TreeI.tree("test", SEW).withChild(TreeI.tree("case", SEW))
                .toXmlStringWithPrefixes();
        requireEquals(resultData, "<s:test><s:case/></s:test>");
    }

    @UnitTest public void testToXmlStringWithAllNameSpaceDeclarationsAtTop() {
        final var resultData = TreeI.tree("test", SEW).withChild(TreeI.tree("case", SEW))
                .toXmlStringWithAllNameSpaceDeclarationsAtTop();
        requireEquals(resultData, "<s:test xmlns:s=\"http://splitcells.net/sew.xsd\" ><s:case/></s:test>");
    }

    @UnitTest public void testToHtmlString() {
        final var simpleSubject = tree("html", HTML)
                .withProperty("xmlns", HTML_ATTRIBUTE, "http://www.w3.org/1999/xhtml")
                .withChild(tree("body", HTML));
        requireEquals(simpleSubject.toHtmlString(), "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body/></html>");
        final var stringTest = tree("html", HTML)
                .withChild(tree("test", STRING));
        requireEquals(stringTest.toHtmlString(), "<html>test</html>");
        final var simpleNaturalTest = tree("html", HTML)
                .withChild(tree("test", NATURAL));
        requireEquals(simpleNaturalTest.toHtmlString(), "<html><test/></html>");
        final var complexNaturalTest = tree("html", HTML)
                .withChild(tree("test", NATURAL).withChild(tree("content", NATURAL)));
        requireEquals(complexNaturalTest.toHtmlString(), "<html><test><content/></test></html>");
        requireThrow(() -> tree("test", JSON).toHtmlString());
    }

    @UnitTest public void testAsCompactXhtmlList() {
        final var testSubject = tree("root");
        testSubject.withProperty("attribute", "value");
        testSubject.withChild(tree("child"));
        requireEquals(testSubject.asCompactXhtmlList(), "<ol><li>root<ol><li>attribute<ol><li>value</li></ol></li><li>child</li></ol></li></ol>");
    }

    @UnitTest public void testAsCompactXhtmlListWithEmptyNodes() {
        final var testSubject = tree("");
        testSubject.withProperty("attribute", "value");
        testSubject.withChild(tree("child"));
        requireEquals(testSubject.asCompactXhtmlList(), "<ol><li>attribute<ol><li>value</li></ol></li><li>child</li></ol>");
    }

    @UnitTest public void testAsCompactXhtmlListWithNestedEmptyNodes() {
        final var testSubject = tree("");
        testSubject.withProperty("attribute", "value");
        testSubject.withProperty("", tree("").withProperty("", "child"));
        requireEquals(testSubject.asCompactXhtmlList(), "<ol><li>attribute<ol><li>value</li></ol></li><li>child</li></ol>");
    }

    @UnitTest public void testAsCompactXhtmlListWithNestedEmptyNodesWithEmptyNodes() {
        final var testSubject = tree("");
        testSubject.withChild(tree(""));
        testSubject.withProperty("attribute", STRING, "value");
        testSubject.withChild(tree(""));
        testSubject.withProperty("", tree("").withChild(tree(""))
                .withProperty("", "child"));
        requireEquals(testSubject.asCompactXhtmlList(), "<ol><li>attribute<ol><li>value</li></ol></li><li>child</li></ol>");
    }

    @UnitTest public void testWithMerged() {
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

    @UnitTest public void testRequireEqualsTo() {
        tree("a").withChild(tree("b")).requireEqualsTo(tree("a").withChild(tree("b")));
        requireThrow(() -> tree("a").requireEqualsTo(tree("b")));
        requireThrow(() -> tree("a").requireEqualsTo(tree("a").withChild(tree("b"))));
        requireThrow(() -> tree("a").requireEqualsTo(tree("a", STRING)));
        requireThrow(() -> tree("a").requireEqualsTo(tree("b").withChild(tree("c"))));
    }

    @UnitTest public void testWithChildren() {
        final var testSubject = tree("a").withChildren(list(tree("b"), tree("c")).stream());
        testSubject.child(0).requireEqualsTo(tree("b"));
        testSubject.child(1).requireEqualsTo(tree("c"));
    }

    @UnitTest public void testChildrenPath() {
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

        require(tree("1").withChild(tree("2")).equals(tree("1").withChild(tree("2"))));
        requireNot(tree("1").equals(tree("1").withChild(tree("2"))));
        requireNot(tree("1").withChild(tree("2")).equals(tree("1")));

        require(tree("1").withPath(tree("2"), tree("3"))
                .equals(tree("1").withPath(tree("2"), tree("3"))));
        requireNot(tree("1").withPath(tree("2"))
                .equals(tree("1").withPath(tree("2"), tree("3"))));
        requireNot(tree("1").withPath(tree("2"), tree("3"))
                .equals(tree("1").withPath(tree("2"))));
        requireNot(tree("1").withPath(tree("2"), tree("3"))
                .equals(tree("1").withPath(tree("2"), tree("4"))));
        requireNot(tree("1").withPath(tree("2"), tree("3"))
                .equals(tree("1").withPath(tree("3"), tree("3"))));
        requireNot(tree("1").withPath(tree("2"), tree("3"))
                .equals(tree("4").withPath(tree("2"), tree("3"))));
    }

    @UnitTest public void testToCompactTree() {
        requireEquals(tree("1"), tree("1").toCompactTree());
        requireEquals("* 1: 2: 3: 4\n"
                , tree("1").withPath(tree("2"), tree("3"), tree("4"))
                        .toCompactTree()
                        .toCommonMarkString());
        requireEquals("* 1: 2:\n    * a\n    * b\n"
                , tree("1").withPath(tree("2").withChildren(tree("a"), tree("b")))
                        .toCompactTree()
                        .toCommonMarkString());
    }

    @UnitTest public void testToCommonMarkString() {
        requireEquals("```\n" + multiple("3", 300) + "\n```\n", tree(multiple("3", 300))
                .toCommonMarkString());
        requireEquals("* " + multiple("3", 300) + "\n", tree(multiple("3", 300))
                .toCommonMarkString(commonMarkConfig().setUseBlockQuotesForLongNames(false)));
        requireEquals("```\na\nb\n```\n", tree("a\nb").toCommonMarkString());
        requireEquals("    * a\n    * b\n", tree("").withChildren(tree("a"), tree("b")).toCommonMarkString());
    }

    @UnitTest public void testPrintCommonMarkStringWithPrefixes() {
        final var resultData = binaryOutputStream();
        tree("a").withChildren(tree("b"), tree("c")).printCommonMarkString(stringSender(resultData), "  ", "- ");
        requireEquals(resultData.toString(), "- a:\n      * b\n      * c\n");
    }

    @UnitTest public void testPropertyInstancesWithoutExactlyOneChild() {
        val testData = tree("").withChild(tree("a", STRING));
        testData.propertyInstances("a").requireEmpty();
        testData.propertyInstances("a", STRING).requireEmpty();
    }

    @UnitTest public void testChildNamedWithDifferentNameSpace() {
        requireEquals(tree("").withChild(tree("a", STRING)).childNamed("a", NATURAL), Optional.empty());
    }

    @UnitTest public void testWithMergedWithDifferentNameSpace() {
        final var testResult = tree("a").withChild(tree("b", STRING)).withMerged(tree("b", NATURAL));
        testResult.children().requireSizeOf(2);
        testResult.child(0).requireEqualsTo(tree("b", STRING));
        testResult.child(1).requireEqualsTo(tree("b", NATURAL));
    }

    @UnitTest public void testToHtmlStringWithNestedStringAndDen() {
        requireEquals(tree("a", STRING).withChild(tree("b", STRING)).toHtmlString(), "ab");
        requireEquals(tree("a", DEN).toHtmlString(), "<a/>");
        requireEquals(tree("a", DEN).withChild(tree("b", DEN)).toHtmlString(), "<a><b/></a>");
    }

    @UnitTest public void testToXmlStringWithMultipleChildren() {
        requireEquals(tree("a&b").withChildren(tree("c"), tree("d")).toXmlString()
                , "<val name=\"a&amp;b\"><c/><d/></val>");
        requireEquals(tree("a", HTML).withChildren(tree("b", HTML), tree("c", HTML)).toXmlString()
                , "<a><b/><c/></a>");
        requireEquals(tree("a", STRING).withChildren(tree("b", STRING), tree("c", STRING)).toXmlString()
                , "abc");
    }

    @UnitTest public void testToXmlStringWithConfig() {
        final var config = xmlConfig().withPrintNameSpaceAttributeAtTop(false).withPrintXmlDeclaration(false);
        requireEquals(tree("", SEW).toXmlString(config), "<empty/>");
        requireEquals(tree("a", SEW).withChild(tree("b", XML_SYNTAX)).toXmlString(config), "<s:a></s:a>");
    }

    @UnitTest public void testToXmlStringWithAllNameSpaceDeclarationsAtTopWithoutMultipleChildren() {
        requireEquals(tree("", SEW).toXmlStringWithAllNameSpaceDeclarationsAtTop(), "<empty/>");
        requireEquals(tree("a", SEW).toXmlStringWithAllNameSpaceDeclarationsAtTop()
                , "<s:a xmlns:s=\"http://splitcells.net/sew.xsd\" />");
        requireEquals(tree("a", SEW).withChildren(tree("b", SEW), tree("c", SEW))
                        .toXmlStringWithAllNameSpaceDeclarationsAtTop()
                , "<s:a xmlns:s=\"http://splitcells.net/sew.xsd\" ><s:b/><s:c/></s:a>");
    }

    @UnitTest public void testToXmlStringWithPrefixesWithMultipleChildren() {
        requireEquals(tree("a&b").withChildren(tree("c", STRING), tree("d", STRING)).toXmlStringWithPrefixes()
                , "<d:val name=\"a&amp;b\">cd</d:val>");
        requireEquals(tree("a", HTML).withChildren(tree("b", STRING), tree("c", STRING)).toXmlStringWithPrefixes()
                , "<x:a>bc</x:a>");
        requireEquals(tree("a", STRING).withChildren(tree("b", STRING), tree("c", STRING)).toXmlStringWithPrefixes()
                , "abc");
        requireEquals(tree("a", NATURAL).withChildren(tree("b", STRING), tree("c", STRING)).toXmlStringWithPrefixes()
                , "<n:a>bc</n:a>");
        requireEquals(tree("a", DEN).withChildren(tree("b", STRING), tree("c", STRING)).toXmlStringWithPrefixes()
                , "<d:a>bc</d:a>");
    }

    @UnitTest public void testExtendWithExistingPath() {
        final var testData = tree("a").extendWith(list("b")).extendWith(list("c")).extendWith(list("c", "d"));
        testData.children().requireSizeOf(2);
        requireEquals(testData.childrenPath(1).name(), "c");
        requireEquals(testData.childrenPath(1, 0).name(), "d");
    }

    @UnitTest public void testAsCompactXhtmlListWithOnlyEmptyNodes() {
        requireEquals(tree("").withChildren(tree(""), tree("")).asCompactXhtmlList(), "<ol></ol>");
    }

    @UnitTest public void testHasAnyPrimitiveValues() {
        require(tree("a").withChild(tree("b")).hasAnyPrimitiveValues());
        requireNot(tree("a").withPath(tree("b"), tree("c")).hasAnyPrimitiveValues());
    }

    @UnitTest public void testToJsonStringWithBooleans() {
        requireEquals(tree(JSON_ARRAY, JSON).withChildren(tree(JSON_TRUE, JSON), tree(JSON_FALSE, JSON)).toJsonString()
                , "[true,false]");
    }

    @UnitTest public void testToJsonStringWithConfigOfPrimitive() {
        requireEquals(tree("a").toJsonString(jsonConfig().withIsTopElement(false)), "\"a\"");
        requireEquals(tree("a").toJsonString(jsonConfig().withIsArrayElement(true)), "\"a\"");
    }

    @UnitTest public void testToJsonStringWithDictionariesInArray() {
        requireEquals(tree(JSON_ARRAY, JSON).withChild(tree("a").withChild(tree("b"))).toJsonString()
                , "[{\"a\":\"b\"}]");
        requireEquals(tree(JSON_ARRAY, JSON).withChild(tree("a").withPath(tree("b"), tree("c"))).toJsonString()
                , "[{\"a\":{\"b\":\"c\"}}]");
    }

    @UnitTest public void testToJsonStringWithJsonNameSpaceOutsideOfArray() {
        requireEquals(tree("a").withChild(tree("b", JSON).withChild(tree("c"))).toJsonString()
                , "{\"a\":{\"b\":\"c\"}}");
    }

    @UnitTest public void testSubtreeWithMissingChild() {
        requireThrow(() -> Tree.subtree(tree("a"), list("b")));
    }

    @UnitTest public void testSubtreeOfInstance() {
        val b = tree("b");
        final var testData = tree("a").withPath(b, tree("c"));
        testData.subtree(list()).requireEqualsTo(testData);
        testData.subtree(list("b")).requireEqualsTo(b);
        requireThrow(() -> testData.subtree(list("d")));
    }

    @UnitTest public void testSubtreeByNameWithMissingChild() {
        requireThrow(() -> tree("a").subtreeByName("b"));
    }

    @UnitTest public void testToTree() {
        val testData = tree("a");
        require(testData.toTree() == testData);
    }

    @UnitTest public void testPathOfDenValueTreeWithoutNameProperty() {
        requireEquals(tree("a").withChild(tree(VAL, DEN)).pathOfDenValueTree("b"), Optional.empty());
    }

    @UnitTest public void testPathOfValueTreeWithConfig() {
        val b = tree("b");
        final var testData = tree("a").withChild(b);
        testData.pathOfValueTree(pathQueryConfig(), "a", "b").orElseThrow().requireEquals(list(b));
        requireEquals(testData.pathOfValueTree(pathQueryConfig(), "b", "b"), Optional.empty());
    }

    @UnitTest public void testMerge() {
        val recorded = tree("").withProperty("a", "1").merge(merger()).withIsRecording(false);
        final var withExistingProperty = tree("").withProperty("a", "0");
        withExistingProperty.merge(recorded);
        withExistingProperty.children().requireSizeOf(1);
        requireEquals(withExistingProperty.namedChild("a").content(), "1");
        final var withoutProperty = tree("");
        withoutProperty.merge(recorded);
        withoutProperty.children().requireSizeOf(1);
        requireEquals(withoutProperty.namedChild("a").content(), "1");
    }

    @UnitTest public void testWithThrowable() {
        final var testData = tree("a").with(new RuntimeException("b"));
        requireEquals(testData.namedChild("throwable").namedChild("message").content(), "b");
        testData.namedChild("throwable").namedChild("stack-trace").children().requireSizeOf(1);
    }
}
