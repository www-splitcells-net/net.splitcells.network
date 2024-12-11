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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.utils.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.tree.JsonConfig.jsonConfig;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.utils.BinaryUtils.binaryOutputStream;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>Interface for adhoc and dynamic trees.
 * Originally, this was called Perspective,
 * as this has its origins from the `net.splitcells.symbiosis` project.</p>
 * <p>
 * There is no distinction between text, attributes and elements like in XML, as there is no
 * actual meaning in this distinction. In XML this is used for rendering and
 * helps to distinct between text and elements in XSL. In Perspective this distinction is
 * done via name spaces.</p>
 * <p>IDEA Create alternative to XSL.</p>
 * <p>
 * A perspective is like an variable. An variable may only hold one value,
 * that may be a list of values. A perspective holds a value and a list of perspectives.
 * In other words, a Perspective is an structure for variables.</p>
 * <p>
 * It has a name in a certain scope which is the namespace.
 * The name is only valid in this scope and may restrict the possible values of the perspective.
 * In other words the namespace may have an type encoded in it, that is described externally.
 * A perspective has a value, if it only contains exactly one value.
 * A perspective has children, if it contains multiple values.</p>
 */
@SuppressWarnings("ALL")
public interface Tree extends TreeView {

    Pattern _VALID_XML_NAME = Pattern.compile("[a-zA-Z][a-zA-Z0-9-_\\.]*");
    String JSON_ARRAY = "array";
    String JSON_OBJECT = "object";
    String JSON_TRUE = "true";
    String JSON_FALSE = "false";

    List<Tree> children();

    default Tree withText(String text) {
        return withValues(TreeI.tree(text, STRING));
    }

    default Tree withProperty(String name, NameSpace nameSpace, String value) {
        return withValue(TreeI.tree(name, nameSpace)
                .withValue(TreeI.tree(value, STRING)));
    }

    default Tree withProperty(String name, NameSpace nameSpace, Tree value) {
        return withValue(TreeI.tree(name, nameSpace).withValue(value));
    }

    default Tree withProperty(String name, String value) {
        return withValue(tree(name)
                .withValue(TreeI.tree(value, STRING)));
    }

    default Tree withProperty(String name, Tree value) {
        return withValue(tree(name).withValue(value));
    }

    default Tree withValues(Tree... args) {
        children().addAll(list(args));
        return this;
    }

    default List<Tree> propertiesWithValue(String name, NameSpace nameSpace, String value) {
        return propertyInstances(name, nameSpace).stream()
                .filter(property -> property.value().get().name().equals(value))
                .collect(toList());
    }

    default String toStringPathsDescription() {
        return toStringPathsDescription(toStringPaths());
    }

    static String toStringPathsDescription(List<String> paths) {
        return paths
                .stream()
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

    static String toMultilineStringPathsDescription(List<String> paths) {
        return paths
                .stream()
                .reduce((a, b) -> a + "\\n" + b)
                .orElse("");
    }

    default List<String> toStringPaths() {
        if (children().isEmpty()) {
            return list(name());
        }
        return children().stream()
                .map(child -> child.toStringPaths().stream()
                        .map(childS -> name() + " " + childS)
                        .collect(toList()))
                .flatMap(e -> e.stream())
                .collect(toList());
    }

    default List<Tree> propertyInstances(String name, NameSpace nameSpace) {
        return children().stream()
                .filter(property -> name.equals(property.name()))
                .filter(property -> nameSpace.equals(property.nameSpace()))
                .filter(property -> property.children().size() == 1)
                .filter(property -> STRING.equals(property.children().get(0).nameSpace()))
                .collect(Lists.toList());
    }

    default List<Tree> propertyInstances(String name) {
        return children().stream()
                .filter(property -> name.equals(property.name()))
                .filter(property -> property.children().size() == 1)
                .collect(Lists.toList());
    }

    default Optional<Tree> propertyInstance(String name, NameSpace nameSpace) {
        final var propertyInstances = propertyInstances(name, nameSpace);
        assertThat(propertyInstances).hasSizeLessThan(2);
        if (propertyInstances.isEmpty()) {
            return Optional.ofNullable(null);
        }
        return Optional.of(propertyInstances.get(0));
    }

    default Optional<Tree> propertyInstance(String name) {
        final var propertyInstances = propertyInstances(name);
        assertThat(propertyInstances).hasSizeLessThan(2);
        if (propertyInstances.isEmpty()) {
            return Optional.ofNullable(null);
        }
        return Optional.of(propertyInstances.get(0));
    }

    default Optional<Tree> childNamed(String name, NameSpace nameSpace) {
        final var children = children().stream()
                .filter(child -> nameSpace.equals(child.nameSpace()) && name.equals(child.name()))
                .collect(toList());
        if (children.isEmpty()) {
            return Optional.ofNullable(null);
        }
        return Optional.of(children.get(0));
    }

    default List<Tree> namedChildren(String name) {
        return children().stream()
                .filter(child -> name.equals(child.name()))
                .collect(toList());
    }

    default Tree namedChild(String name) {
        return namedChildren(name).get(0);
    }

    default Tree child(int index) {
        return children().get(index);
    }

    default Tree withChildren(Tree... argChildren) {
        Stream.of(argChildren).forEach(children()::add);
        return this;
    }

    default Tree withChildren(List<Tree> argChildren) {
        argChildren.forEach(children()::add);
        return this;
    }

    default Tree withChildren(Stream<Tree> argChildren) {
        argChildren.forEach(children()::add);
        return this;
    }

    default Tree withChild(Tree arg) {
        children().add(arg);
        return this;
    }

    @Deprecated
    default Tree withValue(Tree arg) {
        children().add(arg);
        return this;
    }

    @ReturnsThis
    default Tree withPath(Tree path, String propertyName, NameSpace nameSpace) {
        return withPath(this, path, propertyName, nameSpace);
    }

    default Tree withPath(Tree... path) {
        var current = this;
        for (int i = 0; i < path.length; ++i) {
            final var next = path[i];
            current.withChild(next);
            current = next;
        }
        return this;
    }

    private static Tree withPath(Tree current, Tree path, String propertyName, NameSpace nameSpace) {
        final var propertyInstances = path.propertyInstances(propertyName, nameSpace);
        if (propertyInstances.isEmpty()) {
            return current;
        }
        assertThat(propertyInstances).hasSize(1);
        final var element = propertyInstances.get(0);
        final var propertyValue = element.value().orElseThrow().name();
        final var propertyHosters = current.children().stream()
                .filter(child -> child.propertiesWithValue(propertyName, nameSpace, propertyValue).size() == 1)
                .collect(toList());
        final Tree child;
        if (propertyHosters.isEmpty()) {
            // HACK Use generic rendering specifics based on argument.
            child = TreeI.tree(NameSpaces.VAL, NATURAL)
                    .withProperty(NameSpaces.NAME, NATURAL, propertyValue);
            final var elementLinking = path.childNamed(LINK, DEN);
            if (elementLinking.isPresent()) {
                child.withChild(elementLinking.get());
            }
            current.withChild(child);
        } else {
            assertThat(propertyHosters).hasSize(1);
            child = propertyHosters.get(0);
        }
        path.children().stream()
                .filter(pathChild -> !child.propertyInstances(propertyName, nameSpace).isEmpty())
                .forEach(pathChild -> withPath(child, pathChild, propertyName, nameSpace));
        return current;
    }

    default String toHtmlString() {
        final var htmlString = StringUtils.stringBuilder();
        if (nameSpace().equals(HTML)) {
            if (children().isEmpty()) {
                htmlString.append("<" + name() + "/>");
            } else {
                htmlString.append("<" + name());
                if (children().stream().anyMatch(c -> c.nameSpace().isXmlAttribute().orElse(false))) {
                    htmlString.append(" ");
                    children().stream().filter(c -> c.nameSpace().isXmlAttribute().orElse(false)).forEach(c -> {
                        c.children().requireSizeOf(1);
                        htmlString.append(c.name());
                        htmlString.append("=\"");
                        htmlString.append(c.children().get(0).name());
                        htmlString.append("\"");
                    });
                }
                htmlString.append(">");
                htmlString.append(children().stream().map(Tree::toHtmlString).reduce((a, b) -> a + b).orElse(""));
                htmlString.append("</" + name() + ">");
            }
        } else if (nameSpace().equals(STRING)) {
            htmlString.append(name());
            htmlString.append(children().stream().map(Tree::toHtmlString).reduce((a, b) -> a + b).orElse(""));
        } else if (nameSpace().equals(NATURAL) || nameSpace().equals(DEN)) {
            if (children().isEmpty()) {
                htmlString.append("<" + name() + "/>");
            } else {
                htmlString.append("<" + name() + ">");
                htmlString.append(children().stream().map(Tree::toHtmlString).reduce((a, b) -> a + b).orElse(""));
                htmlString.append("</" + name() + ">");
            }
        } else if (nameSpace().isXmlAttribute().orElse(false)) {
            // Nothing needs to be done, as XML attributes are only rendered for HTML elements.
        } else {
            throw executionException("Unsupported namespace `" + nameSpace().uri() + "` for value `" + name() + "`.");
        }
        return htmlString.toString();
    }

    default String toXmlString() {
        return toXmlString(false);
    }

    /**
     * TODO Use a config object in order to get finer control on the printing of namespace attributes
     * and XML namespace prefixes.
     *
     * @param withNameSpaceAttribute If this is set to true,
     *                               the current namespace will be added to the root via `xmlns="[...]"`,
     *                               if the {@link NameSpace} is not {@link NameSpaces#HTML}, {@link NameSpaces#STRING},
     *                               {@link NameSpaces#NATURAL} or {@link NameSpaces#DEN}.
     * @return
     * @deprecated Use {@link #toXmlStringWithAllNameSpaceDeclarationsAtTop} instead.
     */
    @Deprecated
    default String toXmlString(boolean withNameSpaceAttribute) {
        String xmlString = "";
        if (name().isBlank()) {
            return "<empty/>";
        } else if (!_VALID_XML_NAME.matcher(name()).matches() && !nameSpace().equals(STRING)) {
            xmlString += "<val name=\"" + xmlName() + "\">";
            xmlString += children().stream()
                    .map(p -> p.toXmlString(false))
                    .reduce((a, b) -> a + b).orElse("");
            xmlString += "</val>";
        } else if (nameSpace().equals(HTML)) {
            if (children().isEmpty()) {
                xmlString += "<" + name() + "/>";
            } else {
                xmlString += "<" + name() + ">";
                xmlString += children().stream()
                        .map(p -> p.toXmlString(false))
                        .reduce((a, b) -> a + b).orElse("");
                xmlString += "</" + name() + ">";
            }
        } else if (nameSpace().equals(STRING)) {
            xmlString += xmlName();
            xmlString += children().stream()
                    .map(p -> p.toXmlString(false))
                    .reduce((a, b) -> a + b).orElse("");
        } else if (nameSpace().equals(NATURAL) || nameSpace().equals(DEN)) {
            if (children().isEmpty()) {
                xmlString += "<" + name() + "/>";
            } else {
                xmlString += "<" + name() + ">";
                xmlString += children().stream()
                        .map(p -> p.toXmlString(false))
                        .reduce((a, b) -> a + b).orElse("");
                xmlString += "</" + name() + ">";
            }
        } else {
            if (children().isEmpty()) {
                if (withNameSpaceAttribute) {
                    xmlString += "<" + name() + " xmlns=\"" + nameSpace().uri() + "\"/>";
                } else {
                    xmlString += "<" + name() + "/>";
                }
            } else {
                if (withNameSpaceAttribute) {
                    xmlString += "<" + name() + " xmlns=\"" + nameSpace().uri() + "\">";
                } else {
                    xmlString += "<" + name() + ">";
                }
                xmlString += children().stream()
                        .map(p -> p.toXmlString(false))
                        .reduce((a, b) -> a + b).orElse("");
                xmlString += "</" + name() + ">";
            }
        }
        return xmlString;
    }

    default void visit(Consumer<Tree> visitor) {
        visitor.accept(this);
        children().forEach(c -> c.visit(visitor));
    }

    default Tree withXmlAttribute(String attributeName, String attributeValue, NameSpace nameSpace) {
        return withChild(TreeI.tree("attribute", XML_SYNTAX)
                .withChildren(TreeI.tree(attributeName, nameSpace), tree(attributeValue)));
    }

    /**
     * @param xmlConfig This configures the output.
     * @return Returns the XML element name of the current {@link Tree} optionally with a prefix and/or namespace declarations.
     * The String ends with a whitespace.
     */
    default String toXmlElementStartName(XmlConfig xmlConfig) {
        var name = toXmlElementName(xmlConfig) + " ";
        if (xmlConfig.printNameSpaceAttributeAtTop()) {
            final Set<NameSpace> nameSpaces = setOfUniques();
            visit(p -> nameSpaces.add(p.nameSpace()));
            name += nameSpaceDeclarations(nameSpaces);
        }
        name += children().stream()
                .map(c -> {
                    if (c.nameSpace().equals(XML_SYNTAX) && c.name().equals("attribute")) {
                        final var cName = c.child(0);
                        return cName.nameSpace().defaultPrefix()
                                + ":"
                                + cName.name()
                                + "=\""
                                + c.child(1).name()
                                + "\"";
                    }
                    return "";
                }).reduce("", (a, b) -> a + " " + b);
        return name;
    }

    /**
     * @param xmlConfig This configures the output.
     * @return Returns the XML element name of the current {@link Tree} optionally with a prefix.
     */
    default String toXmlElementName(XmlConfig xmlConfig) {
        return nameSpace().defaultPrefix() + ":" + name();
    }

    /**
     * TODO Support escaping most important characters.
     *
     * @param xmlConfig
     * @return
     */
    default String toXmlString(XmlConfig xmlConfig) {
        String xmlString = "";
        final var childConfig = xmlConfig.deepClone();
        if (xmlConfig.printNameSpaceAttributeAtTop()) {
            childConfig.withPrintNameSpaceAttributeAtTop(false);
        }
        if (xmlConfig.printXmlDeclaration()) {
            xmlString += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            childConfig.withPrintXmlDeclaration(false);
        }
        if (nameSpace().equals(STRING) || nameSpace().equals(TEXT)) {
            return name();
        }
        if (name().isBlank()) {
            return "<empty/>";
        }
        if (children().isEmpty()) {
            xmlString += "<" + toXmlElementStartName(xmlConfig) + "/>";
        } else {
            xmlString += "<" + toXmlElementStartName(xmlConfig) + ">";
            xmlString += children().stream()
                    .filter(c -> !c.nameSpace().equals(XML_SYNTAX))
                    .map(c -> c.toXmlString(childConfig))
                    .reduce((a, b) -> a + b).orElse("");
            xmlString += "</" + toXmlElementName(xmlConfig) + ">";
        }
        return xmlString;
    }

    /**
     * @return
     * @deprecated Use {@link #toXmlString(XmlConfig)} instead.
     */
    @Deprecated
    default String toXmlStringWithAllNameSpaceDeclarationsAtTop() {
        final Set<NameSpace> nameSpaces = setOfUniques();
        visit(p -> nameSpaces.add(p.nameSpace()));
        if (name().isBlank()) {
            return "<empty/>";
        }
        String xmlString = "";
        if (children().isEmpty()) {
            xmlString += "<" + nameSpace().defaultPrefix()
                    + ":"
                    + name()
                    + " "
                    + nameSpaceDeclarations(nameSpaces)
                    + "/>";
        } else {
            xmlString += "<"
                    + nameSpace().defaultPrefix()
                    + ":"
                    + name()
                    + " "
                    + nameSpaceDeclarations(nameSpaces)
                    + ">";
            xmlString += children().stream().map(Tree::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
            xmlString += "</" + nameSpace().defaultPrefix() + ":" + name() + ">";
        }
        return xmlString;
    }

    private static String nameSpaceDeclarations(Set<NameSpace> nameSpaces) {
        final var declarations = stringBuilder();
        nameSpaces.forEach(n -> {
            declarations.append("xmlns:");
            declarations.append(n.defaultPrefix());
            declarations.append("=\"");
            declarations.append(n.uri());
            declarations.append("\" ");
        });
        return declarations.toString();
    }

    /**
     * @return
     * @deprecated Use {@link #toXmlString(XmlConfig)} instead.
     */
    @Deprecated
    default String toXmlStringWithPrefixes() {
        String xmlString = "";
        if (name().isBlank()) {
            return "<empty/>";
        } else if (!_VALID_XML_NAME.matcher(name()).matches()) {
            if (children().isEmpty()) {
                xmlString += xmlName();
            } else {
                xmlString += "<d:val name=\"" + xmlName() + "\">";
                xmlString += children().stream().map(Tree::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
                xmlString += "</d:val>";
            }
        } else if (nameSpace().equals(HTML)) {
            if (children().isEmpty()) {
                xmlString += "<x:" + name() + "/>";
            } else {
                xmlString += "<x:" + name() + ">";
                xmlString += children().stream().map(Tree::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
                xmlString += "</x:" + name() + ">";
            }
        } else if (nameSpace().equals(STRING)) {
            xmlString += xmlName();
            xmlString += children().stream().map(Tree::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
        } else if (nameSpace().equals(NATURAL)) {
            if (children().isEmpty()) {
                xmlString += "<n:" + name() + "/>";
            } else {
                xmlString += "<n:" + name() + ">";
                xmlString += children().stream().map(Tree::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
                xmlString += "</n:" + name() + ">";
            }
        } else if (nameSpace().equals(DEN)) {
            if (children().isEmpty()) {
                xmlString += "<d:" + name() + "/>";
            } else {
                xmlString += "<d:" + name() + ">";
                xmlString += children().stream().map(Tree::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
                xmlString += "</d:" + name() + ">";
            }
        } else {
            if (children().isEmpty()) {
                xmlString += "<" + nameSpace().defaultPrefix() + ":" + name() + "/>";
            } else {
                xmlString += "<" + nameSpace().defaultPrefix() + ":" + name() + ">";
                xmlString += children().stream().map(Tree::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
                xmlString += "</" + nameSpace().defaultPrefix() + ":" + name() + ">";
            }
        }
        return xmlString;
    }

    @ReturnsThis
    default Tree extendWith(List<String> path) {
        if (path.isEmpty()) {
            return this;
        }
        final var next = path.remove(0);
        final var match = children().stream()
                .filter(child -> child.name().equals(next))
                .findFirst();
        if (match.isEmpty()) {
            final var nextPerspective = tree(next);
            this.withChild(nextPerspective);
            nextPerspective.extendWith(path);
        } else {
            match.get().extendWith(path);
        }
        return this;
    }

    /**
     * @return Renders the {@link Tree} as a nested XHTML list without a namespace decleration.
     */
    default String asXhtmlList() {
        return asXhtmlList(true);
    }

    default String asXhtmlList(boolean isRoot) {
        final String htmlList;
        if (children().isEmpty()) {
            htmlList = "<li>" + xmlName() + "</li>";
        } else {
            final String childrenHtmlList = children().stream()
                    .map(c -> c.asXhtmlList(false))
                    .reduce("", (a, b) -> a + b);
            htmlList = "<li>" + xmlName() + "</li>"
                    + "<ol>" + childrenHtmlList + "</ol>";
        }
        if (isRoot) {
            return "<ol>" + htmlList + "</ol>";
        }
        return htmlList;
    }

    /**
     * @return Renders the {@link Tree} as a nested XHTML list without a namespace decleration,
     * while avoiding nested lists with no content,
     * in order have a more compact and readable rendering.
     */
    default String asCompactXhtmlList() {
        return asCompactXhtmlList(true);
    }

    default String asCompactXhtmlList(boolean isRoot) {
        final String htmlList;
        if (isRoot && name().isEmpty()) {
            return "<ol>"
                    + children().stream().map(c -> c.asCompactXhtmlList(false))
                    .reduce((a, b) -> a + b)
                    .orElse("")
                    + "</ol>";
        }
        if (!isRoot && name().isEmpty() && children().size() == 1) {
            return children().stream().map(c -> c.asCompactXhtmlList(false))
                    .reduce((a, b) -> a + b)
                    .orElse("");
        }
        if (children().isEmpty()) {
            htmlList = "<li>" + xmlName() + "</li>";
        } else {
            final String childrenHtmlList = children().stream()
                    .map(c -> c.asCompactXhtmlList(false))
                    .reduce("", (a, b) -> a + b);
            htmlList = "<li>" + xmlName() + "</li>"
                    + "<ol>" + childrenHtmlList + "</ol>";
        }
        if (isRoot) {
            return "<ol>" + htmlList + "</ol>";
        }
        return htmlList;
    }

    /**
     * TODO I think, this method is not required anymore.
     *
     * @return Returns a {@link Tree}, that is guaranteed to be printable like a JSON.
     */
    @Deprecated
    default Tree createToJsonPrintable() {
        return this;
    }

    /**
     * <p>This method should be able to create a JSON string for any {@link Tree},
     * as long as any usage of {@link NameSpaces#JSON} is correct.</p>
     * <p>TODO IDEA Consider speeding this up, by caching the {@link JsonConfig}.</p>
     *
     * @return
     */
    default String toJsonString() {
        return toJsonString(jsonConfig());
    }

    private static String encodeJsonString(String arg) {
        return arg.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\"", "\\\"")
                ;
    }

    default boolean hasAnyPrimitiveValues() {
        return children().stream().anyMatch(c -> c.children().isEmpty());
    }

    /**
     * @param config
     * @return A JSON String that represents part or a complete JSON document.
     * If this is classified as a JSON primitive value, the returned String is a partial JSON document.
     * Otherwise, it is a complete JSON document.
     */
    default String toJsonValue(JsonConfig config) {
        if (children().hasElements()) {
            return toJsonString(config);
        }
        if (nameSpace().equals(JSON)) {
            if (name().equals(JSON_TRUE)) {
                return "true";
            }
            if (name().equals(JSON_FALSE)) {
                return "false";
            }
        }
        return "\"" + encodeJsonString(name()) + "\"";
    }

    /**
     * <p>Creates a JSON, where all primitive values are Strings.</p>
     * <p>TODO Mabye the {@link NameSpaces#JSON} is incorrect.
     * Maybe using dedicated JSON class for such would be better.
     * On the other hand, this provides a way to process and transform all kind of AST like things
     * via one data structure like XSLT does.
     * This is needed for the web server, in order to migrate from XSLT to Java code,
     * as XSLT is not portable legacy software, because related software gets slowly out of date.</p>
     * <p>The implementation is split into prefix, body and suffix parts.
     * In this implementation only prefix and suffix parts xor the body part is allowed to determine,
     * whether something is a array, dictionary or primitive in order to avoid duplicate code and thereby errors.
     * Here the determination is done in the prefix and suffix parts,
     * because for the very top level node, this determination needs to be done at the prefix level.</p>
     *
     * @return This is a JSON, representing the contents of this, whereby the {@link #nameSpace()} is ignored.
     */
    default String toJsonString(JsonConfig config) {
        final StringBuilder jsonString = new StringBuilder();
        if (!config.isTopElement() && children().isEmpty()) {
            return "\"" + encodeJsonString(name()) + "\"";
        }
        if (config.isTopElement() && children().isEmpty()) {
            if (config.isArrayElement()) {
                return "\"" + encodeJsonString(name()) + "\"";
            }
            return "[\"" + encodeJsonString(name()) + "\"]";
        }
        if (children().isEmpty()) {
            jsonString.append("{\"" + encodeJsonString(name()) + "\":\"\"}");
        } else {
            boolean isNotFirstChild = false;
            final var hasAnyPrimitiveValues = children().stream()
                    .anyMatch(c -> c.children().isEmpty() || (c.nameSpace().equals(JSON) && c.name().equals(JSON_ARRAY)));
            final var isJsonArray = nameSpace().equals(JSON) && name().equals(JSON_ARRAY);
            final var isJsonDictionary = nameSpace().equals(JSON) && name().equals(JSON_OBJECT);
            if (isJsonDictionary) {
                if (hasAnyPrimitiveValues) {
                    throw executionException("JSON objects are not allowed to contain primitive values like arrays have. JSON objects are only allowed to have name/value pairs.");
                }
                jsonString.append("{");
            } else if (isJsonArray) {
                jsonString.append("[");
            } else if (hasAnyPrimitiveValues) {
                if (config.isTopElement() || config.isArrayElement()) {
                    jsonString.append("{\"");
                } else {
                    jsonString.append("\"");
                }
                if (containsOneJsonMember()) {
                    jsonString.append(encodeJsonString(name()) + "\":");
                } else {
                    jsonString.append(encodeJsonString(name()) + "\":[");
                }
            } else {
                if (config.isTopElement() || config.isArrayElement()) {
                    jsonString.append("{\"");
                } else {
                    jsonString.append("\"");
                }
                if (containsOneJsonMember()) {
                    jsonString.append(encodeJsonString(name()) + "\":");
                } else {
                    jsonString.append(encodeJsonString(name()) + "\":{");
                }
            }
            for (final var child : children()) {
                if (isNotFirstChild) {
                    jsonString.append(",");
                }
                isNotFirstChild = true;
                jsonString.append(child.toJsonValue(jsonConfig()
                        .withIsTopElement(false)
                        .withIsArrayElement(isJsonArray)));
            }
            if (isJsonDictionary) {
                jsonString.append("}");
            } else if (isJsonArray) {
                jsonString.append("]");
            } else if (hasAnyPrimitiveValues) {
                if (!containsOneJsonMember()) {
                    jsonString.append("]");
                }
                if (config.isTopElement() || config.isArrayElement()) {
                    jsonString.append("}");
                }
            } else {
                if (!containsOneJsonMember()) {
                    jsonString.append("}");
                }
                if (config.isTopElement() || config.isArrayElement()) {
                    jsonString.append("}");
                }
            }
        }
        return jsonString.toString();
    }

    private boolean isJsonPrimitive() {
        return children().isEmpty();
    }

    private boolean containsOneJsonMember() {
        return children().size() == 1
                && (children().get(0).children().isEmpty()
                || (children().get(0).nameSpace().equals(JSON) && children().get(0).name().equals("array")));
    }

    default String toCommonMarkString() {
        final var commonMarkString = binaryOutputStream();
        printCommonMarkString(stringSender(commonMarkString));
        return commonMarkString.toString();
    }

    default void printCommonMarkString(Sender<String> output) {
        printCommonMarkString(output, "", "* ");
    }

    default void printCommonMarkString(Sender<String> output, String prefix, String listPrefix) {
        if (children().isEmpty()) {
            if (name().length() > 200 || name().contains("\n")) {
                output.append("```\n" + name() + "\n```");
            } else {
                output.append(listPrefix + name());
            }
            return;
        }
        final var isProperty = children().size() == 1 && children().get(0).children().isEmpty();
        if (isProperty) {
            output.append(listPrefix + name() + ": " + children().get(0).name());
            return;
        }
        if (!name().isEmpty()) {
            final var lastNameChar = name().charAt(name().length() - 1);
            if (lastNameChar == '.' || lastNameChar == ':') {
                output.append(listPrefix + name());
            } else {
                output.append(listPrefix + name() + ":");
            }
        }
        final var isSimpleList = children().stream().map(c -> c.children().isEmpty()).reduce(true, (a, b) -> a && b);
        if (isSimpleList) {
            final var newListPrefix = prefix + "    * ";
            final var newPrefix = newListPrefix;
            children().forEach(c -> c.printCommonMarkString(output, newPrefix, newListPrefix));
        } else {
            final var newListPrefix = prefix + "    * ";
            final var newPrefix = prefix + "    ";
            children().forEach(c -> c.printCommonMarkString(output, newPrefix, newListPrefix));
        }
    }

    default Tree subtree(List<String> path) {
        if (path.isEmpty()) {
            return this;
        }
        final var next = path.remove(0);
        return children().stream()
                .filter(child -> child.name().equals(next))
                .findFirst()
                .orElseThrow()
                .subtree(path);
    }

    default Tree subtreeByName(String... path) {
        if (path.length == 0) {
            return this;
        }
        var current = this;
        for (final var element : path) {
            final var next = current.children().stream()
                    .filter(child -> child.name().equals(element))
                    .findFirst();
            if (next.isEmpty()) {
                throw executionException(tree("Could not find " + getClass().getName() + " child by name.")
                        .withProperty("path", listWithValuesOf(path).toString())
                        .withProperty("searched", element));
            }
            current = next.get();
        }
        return current;
    }

    /**
     * TODO It would be best to return a copy of {@link Tree} instead of {@code this}.
     *
     * @return return
     */
    default Tree toTree() {
        return this;
    }

    /**
     * TODO Move this functionality into {@link #pathOfValueTree(PathQueryConfig, List)}.
     *
     * @param stringPath
     * @return
     */
    @Deprecated
    default Optional<List<Tree>> pathOfDenValueTree(String stringPath) {
        return pathOfDenValueTree(listWithValuesOf(stringPath.split("/")));
    }

    /**
     * TODO Move this functionality into {@link #pathOfValueTree(PathQueryConfig, List)}.
     *
     * @param stringPath
     * @return
     */
    @Deprecated
    default Optional<List<Tree>> pathOfDenValueTree(List<String> stringPath) {
        final List<Tree> path = listWithValuesOf();
        Tree currentNode = this;
        while (stringPath.hasElements()) {
            final var currentPathElement = stringPath.remove(0);
            final var nextPathPerspective = currentNode.children().stream()
                    .filter(c -> c.nameIs(VAL, DEN))
                    .filter(c -> {
                        final var nameProp = c.propertyInstance(NAME, DEN);
                        if (nameProp.isEmpty()) {
                            return false;
                        }
                        return nameProp.orElseThrow().valueName().equals(currentPathElement);
                    })
                    .findFirst();
            if (nextPathPerspective.isEmpty()) {
                return Optional.empty();
            }
            currentNode = nextPathPerspective.orElseThrow();
            path.withAppended(currentNode);
        }
        return Optional.of(path);
    }

    default Optional<List<Tree>> pathOfValueTree(String... stringPath) {
        return pathOfValueTree(PathQueryConfig.pathQueryConfig(), listWithValuesOf(stringPath));
    }

    default Optional<List<Tree>> pathOfValueTree(PathQueryConfig config, String... stringPath) {
        return pathOfValueTree(config, listWithValuesOf(stringPath));
    }

    /**
     * The {@link NameSpace} of the {@link Tree} is ignored in this method.
     *
     * @param stringPath List of {@link Tree#name()} describing a path starting with {@code this}.
     * @return The list of {@link Tree} corresponding to a path
     */
    default Optional<List<Tree>> pathOfValueTree(PathQueryConfig config, List<String> stringPath) {
        final List<Tree> path = listWithValuesOf();
        Tree currentNode = this;
        if (config.checkRootNode()) {
            if (!name().equals(stringPath.remove(0))) {
                return Optional.empty();
            }
        }
        while (stringPath.hasElements()) {
            final var currentPathElement = stringPath.remove(0);
            final var nextPathPerspective = currentNode.children().stream()
                    .filter(c -> c.name().equals(currentPathElement))
                    .findFirst();
            if (nextPathPerspective.isEmpty()) {
                return Optional.empty();
            }
            currentNode = nextPathPerspective.orElseThrow();
            path.withAppended(currentNode);
        }
        return Optional.of(path);
    }
}
