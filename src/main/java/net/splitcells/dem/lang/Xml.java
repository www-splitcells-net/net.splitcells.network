package net.splitcells.dem.lang;

import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.utils.ConstructorIllegal;
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
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;

/**
 * Currently XML is used as the base of all documents.
 * If it is not suitable anymore, it will be replaced by {@link net.splitcells.dem.lang.perspective.PerspectiveDocument}.
 */
public final class Xml {
    private static final Transformer TRANSFORMER = Xml.newTransformer();
    private static final Transformer UNDECLARED_TRANSFORMER = Xml.newTransformer();
    private static final DocumentBuilder ROOT_DOCUMENT_BUILDER = Xml.rootDocumentBuilder();
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
        throw new ConstructorIllegal();
    }

    private static Transformer newTransformer() {
        try {
            return TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            throw new RuntimeException(e);
        }
    }

    private static DocumentBuilder rootDocumentBuilder() {
        try {
            final DocumentBuilderFactory rBase = DocumentBuilderFactory.newInstance();
            rBase.setNamespaceAware(true);
            return rBase.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Transformer transformer() {
        return TRANSFORMER;
    }

    public static Element element(NameSpace nameSpace, String name) {
        return ROOT_DOCUMENT.createElement(nameSpace.prefixedName(name));
    }

    public static Element rElement(NameSpace nameSpace, String name) {
        final var rVal = ROOT_DOCUMENT.createElement(nameSpace.prefixedName(name));
        element(rVal, nameSpaceDecleration(nameSpace));
        return rVal;
    }

    public static Element element(NameSpace nameSpace, String name, String value) {
        final var rVal = ROOT_DOCUMENT.createElement(nameSpace.prefixedName(name));
        element(rVal, textNode(value));
        return rVal;
    }

    @Deprecated
    public static Element event(String name, String subject, Node... arguments) {
        final var rVal = element(name);
        final var subjectNode = element("subject");
        subjectNode.appendChild(textNode(subject));
        rVal.appendChild(subjectNode);
        asList(arguments).forEach(arg -> rVal.appendChild(arg));
        return rVal;
    }

    public static Node textNode(String text) {
        return ROOT_DOCUMENT.createTextNode(text);
    }

    public static Element element(String name) {
        try {
            return ROOT_DOCUMENT.createElement(name);
        } catch (RuntimeException e) {
            domsole().append(name);
            throw e;
        }
    }

    public static Element element(Element element, Attr... attributes) {
        for (Attr attribute : attributes) {
            element.setAttributeNode(attribute);
        }
        return element;
    }

    public static Element element2(String name, Stream<Node> nodes) {
        return element(element(name), nodes.collect(toList()));
    }

    public static Element element(String name, Stream<Node> nodes) {
        return element(element(name), nodes.collect(toList()));
    }

    public static Element element(String name, Node... nodes) {
        return element(element(name), nodes);
    }

    public static Element element(String name, NameSpace nameSpace, Node... nodes) {
        return element(name, nameSpace, asList(nodes));
    }

    public static Element element(String name, NameSpace nameSpace, Collection<Node> nodes) {
        final var element = element(name, nameSpace);
        nodes.forEach(node -> element.appendChild(node));
        return element;
    }

    public static Element element(Element element, Node... nodes) {
        return element(element, asList(nodes));
    }

    public static Element element(Element element, Collection<Node> nodes) {
        for (Node node : nodes) {
            if (node != null) {
                element.appendChild(node);
            } else {
                element.appendChild(textNode("null"));
            }
        }
        return element;
    }

    @Deprecated
    public static Attr nameSpaceDecleration(NameSpace nameSpace) {
        final var rVal = ROOT_DOCUMENT.createAttribute("xmlns:" + nameSpace.defaultPrefix());
        rVal.setNodeValue(nameSpace.uri());
        return rVal;
    }

    public static Attr attribute(NameSpace nameSpace, String name, String value) {
        final var rVal = ROOT_DOCUMENT.createAttribute(nameSpace.prefixedName(name));
        rVal.setNodeValue(value);
        return rVal;
    }

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
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(arg);
        try {
            TRANSFORMER.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return result.getWriter().toString();
    }

    public static Document parse(Path file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            return factory.newDocumentBuilder().parse(file.toFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parse(InputStream document) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            return factory.newDocumentBuilder().parse(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public static Element getDirectChildElementByName(Element element) {
        final var nodeList = element.getChildNodes();
        // TODO
        throw new RuntimeException();
    }
}
