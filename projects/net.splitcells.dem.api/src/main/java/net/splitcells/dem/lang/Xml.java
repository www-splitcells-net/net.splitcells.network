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
package net.splitcells.dem.lang;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.ExecutionException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static org.w3c.dom.Node.ELEMENT_NODE;

/**
 * Currently XML is used as the base of all documents.
 * If it is not suitable anymore, it will be replaced by {@link Tree}.
 * Use this only, in order to parse, render and transform XML.
 * Do not handle XML content in Java code directly via this API or Java's XML API!
 */
@JavaLegacy
public final class Xml {

    public static String escape(String arg) {
        return arg
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
                .replace("~", "&Tilde;");
    }
    /**
     * Using this is deprecated, because it causes race conditions.
     */
    @Deprecated
    private static final Transformer TRANSFORMER = Xml.newTransformer();
    /**
     * Using this is deprecated, because it causes race conditions.
     */
    @Deprecated
    private static final Transformer UNDECLARED_TRANSFORMER = Xml.newTransformer();
    /**
     * Using this is deprecated, because it causes race conditions.
     */
    @Deprecated
    private static final DocumentBuilder ROOT_DOCUMENT_BUILDER = Xml.rootDocumentBuilder();
    /**
     * Using this is deprecated, because it causes race conditions.
     */
    @Deprecated
    private static final Document ROOT_DOCUMENT = ROOT_DOCUMENT_BUILDER.newDocument();

    public static Document document() {
        return ROOT_DOCUMENT_BUILDER.newDocument();
    }

    static {
        Xml.TRANSFORMER.setOutputProperty(INDENT, "yes");
        Xml.TRANSFORMER.setOutputProperty(OMIT_XML_DECLARATION, "no");
        Xml.UNDECLARED_TRANSFORMER.setOutputProperty(INDENT, "yes");
        Xml.UNDECLARED_TRANSFORMER.setOutputProperty(OMIT_XML_DECLARATION, "yes");
    }

    private Xml() {
        throw constructorIllegal();
    }

    private static Transformer newTransformer() {
        try {
            final var factory = TransformerFactory.newInstance();
            /* The following security settings do not work, because these are not supported by Saxon-HE.
             * The missing support error only happens in integration tests.
             * factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
             * factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
             */
            return factory.newTransformer();
        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            throw new RuntimeException(e);
        }
    }

    private static DocumentBuilder rootDocumentBuilder() {
        try {
            final DocumentBuilderFactory rBase = DocumentBuilderFactory.newInstance();
            rBase.setFeature("http://xml.org/sax/features/external-general-entities", false);
            rBase.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            rBase.setNamespaceAware(true);
            return rBase.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Transformer transformer() {
        return TRANSFORMER;
    }

    @Deprecated
    public static Element elementWithChildren(NameSpace nameSpace, String name) {
        return ROOT_DOCUMENT.createElement(nameSpace.prefixedName(name));
    }

    @Deprecated
    public static Element rElement(NameSpace nameSpace, String name) {
        try {
            final var rVal = ROOT_DOCUMENT.createElement(nameSpace.prefixedName(name));
            elementWithChildren(rVal, nameSpaceDecleration(nameSpace));
            return rVal;
        } catch (Throwable e) {
            throw ExecutionException.execException("Could not transform String to XML element: " + name, e);
        }
    }

    @Deprecated
    public static Element elementWithChildren(NameSpace nameSpace, String name, String value) {
        final var rVal = ROOT_DOCUMENT.createElement(nameSpace.prefixedName(name));
        elementWithChildren(rVal, textNode(value));
        return rVal;
    }

    @Deprecated
    public static Element event(String name, String subject, Node... arguments) {
        final var rVal = elementWithChildren(name);
        final var subjectNode = elementWithChildren("subject");
        subjectNode.appendChild(textNode(subject));
        rVal.appendChild(subjectNode);
        asList(arguments).forEach(arg -> rVal.appendChild(arg));
        return rVal;
    }

    @Deprecated
    public static Node textNode(String text) {
        return ROOT_DOCUMENT.createTextNode(text);
    }

    @Deprecated
    public static Element elementWithChildren(String name) {
        try {
            return ROOT_DOCUMENT.createElement(name);
        } catch (RuntimeException e) {
            logs().append(name);
            throw e;
        }
    }

    @Deprecated
    public static Element elementWithChildren(Element element, Attr... attributes) {
        for (Attr attribute : attributes) {
            element.setAttributeNode(attribute);
        }
        return element;
    }

    @Deprecated
    public static Element element2(String name, Stream<Node> nodes) {
        return elementWithChildren(elementWithChildren(name), nodes.toList());
    }

    @Deprecated
    public static Element elementWithChildren(String name, Stream<Node> nodes) {
        return elementWithChildren(elementWithChildren(name), nodes.toList());
    }

    @Deprecated
    public static Element elementWithChildren(String name, Node... nodes) {
        return elementWithChildren(elementWithChildren(name), nodes);
    }

    @Deprecated
    public static Element elementWithChildren(String name, NameSpace nameSpace, Node... nodes) {
        return elementWithChildren(name, nameSpace, asList(nodes));
    }

    @Deprecated
    public static Element elementWithChildren(String name, NameSpace nameSpace, Collection<Node> nodes) {
        final var element = ROOT_DOCUMENT.createElement(nameSpace.prefixedName(name));
        nodes.forEach(node -> element.appendChild(node));
        return element;
    }

    @Deprecated
    public static Element elementWithChildren(Element element, Node... nodes) {
        return elementWithChildren(element, asList(nodes));
    }

    @Deprecated
    public static Element elementWithChildren(Element element, Collection<Node> nodes) {
        for (Node node : nodes) {
            if (node != null) {
                element.appendChild(node);
            } else {
                element.appendChild(textNode("null"));
            }
        }
        return element;
    }

    /**
     * Namespace declaration is deprecated, because we need an alternative.
     * Currently there is a problem, when creating single elements with certain namespaces.
     *
     * @param nameSpace
     * @return
     */
    @Deprecated()
    public static Attr nameSpaceDecleration(NameSpace nameSpace) {
        final var rVal = ROOT_DOCUMENT.createAttribute("xmlns:" + nameSpace.defaultPrefix());
        rVal.setNodeValue(nameSpace.uri());
        return rVal;
    }

    @Deprecated
    public static Attr attribute(NameSpace nameSpace, String name, String value) {
        final var rVal = ROOT_DOCUMENT.createAttribute(nameSpace.prefixedName(name));
        rVal.setNodeValue(value);
        return rVal;
    }

    @Deprecated
    public static Attr attribute(String name, String value) {
        final var rVal = ROOT_DOCUMENT.createAttribute(name);
        rVal.setNodeValue(value);
        return rVal;
    }

    public static String toDocumentString(Node arg) {
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(arg);
        try {
            TRANSFORMER.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return result.getWriter().toString();
    }

    public static String toPrettyString(Node arg) {
        return toDocumentString(arg);
    }

    public static Document parse(Path file) {
        try {
            return Xml.rootDocumentBuilder().parse(file.toFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parse(InputStream document) {
        try {
            return Xml.rootDocumentBuilder().parse(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parse(String content) {
        return parse(new ByteArrayInputStream(content.getBytes()));
    }

    public static String toFlatString(Node arg) {
        return toPrettyString(arg).replaceAll("\\R", "");
    }

    public static String toPrettyWithoutHeaderString(Node arg) {
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(arg);
        try {
            UNDECLARED_TRANSFORMER.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return result.getWriter().toString();
    }

    @Deprecated
    public static Element directChildElementByName(Element element, String name, NameSpace nameSpace) {
        final var directChildrenByName = directChildElementsByName(element, name, nameSpace)
                .collect(Lists.toList());
        if (directChildrenByName.size() != 1) {
            throw new IllegalArgumentException("Illegal Number of fitting children. Only one fitting child is allowed: " + directChildrenByName.size());
        }
        return directChildrenByName.get(0);
    }

    @Deprecated
    public static Stream<Element> directChildElementsByName(Element element, String name, NameSpace nameSpace) {
        return directChildElements(element)
                .filter(node -> nameSpace.uri().equals(node.getNamespaceURI()))
                .filter(node -> node.getLocalName().equals(name));
    }

    @Deprecated
    public static Optional<Element> optionalDirectChildElementsByName(Element element, String name, NameSpace nameSpace) {
        return directChildElements(element)
                .filter(node -> nameSpace.uri().equals(node.getNamespaceURI()))
                .filter(node -> node.getLocalName().equals(name))
                .findFirst();
    }

    @Deprecated
    public static Stream<Node> directChildNodes(Element element) {
        final var nodeList = element.getChildNodes();
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(i -> nodeList.item(i));
    }

    @Deprecated
    public static Stream<Element> directChildElements(Element element) {
        return directChildNodes(element)
                .filter(node -> node.getNodeType() == ELEMENT_NODE)
                .map(node -> (Element) node);
    }
}
