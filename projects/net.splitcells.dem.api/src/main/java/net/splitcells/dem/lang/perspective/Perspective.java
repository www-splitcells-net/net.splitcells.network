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

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.communication.Sender;
import org.assertj.core.api.Assertions;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.splitcells.dem.data.atom.Bools.bool;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.perspective.JsonConfig.jsonConfig;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.utils.BinaryUtils.binaryOutputStream;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.StringUtils.multiple;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>Interface for adhoc and dynamic trees.</p>
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
public interface Perspective extends PerspectiveView {

    Pattern _VALID_XML_NAME = Pattern.compile("[a-zA-Z][a-zA-Z0-9-_\\.]*");

    List<Perspective> children();

    default Perspective withText(String text) {
        return withValues(perspective(text, STRING));
    }

    default Perspective withProperty(String name, NameSpace nameSpace, String value) {
        return withValue(perspective(name, nameSpace)
                .withValue(perspective(value, STRING)));
    }

    default Perspective withProperty(String name, String value) {
        return withValue(perspective(name)
                .withValue(perspective(value, STRING)));
    }

    default Perspective withProperty(String name, Perspective value) {
        return withValue(perspective(name).withValue(value));
    }

    default Perspective withValues(Perspective... args) {
        children().addAll(list(args));
        return this;
    }

    default List<Perspective> propertiesWithValue(String name, NameSpace nameSpace, String value) {
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

    default List<Perspective> propertyInstances(String name, NameSpace nameSpace) {
        return children().stream()
                .filter(property -> name.equals(property.name()))
                .filter(property -> nameSpace.equals(property.nameSpace()))
                .filter(property -> property.children().size() == 1)
                .filter(property -> STRING.equals(property.children().get(0).nameSpace()))
                .collect(Lists.toList());
    }

    default List<Perspective> propertyInstances(String name) {
        return children().stream()
                .filter(property -> name.equals(property.name()))
                .filter(property -> property.children().size() == 1)
                .collect(Lists.toList());
    }

    default Optional<Perspective> propertyInstance(String name, NameSpace nameSpace) {
        final var propertyInstances = propertyInstances(name, nameSpace);
        assertThat(propertyInstances).hasSizeLessThan(2);
        if (propertyInstances.isEmpty()) {
            return Optional.ofNullable(null);
        }
        return Optional.of(propertyInstances.get(0));
    }

    default Optional<Perspective> propertyInstance(String name) {
        final var propertyInstances = propertyInstances(name);
        assertThat(propertyInstances).hasSizeLessThan(2);
        if (propertyInstances.isEmpty()) {
            return Optional.ofNullable(null);
        }
        return Optional.of(propertyInstances.get(0));
    }

    default Optional<Perspective> childNamed(String name, NameSpace nameSpace) {
        final var children = children().stream()
                .filter(child -> nameSpace.equals(child.nameSpace()) && name.equals(child.name()))
                .collect(toList());
        if (children.isEmpty()) {
            return Optional.ofNullable(null);
        }
        return Optional.of(children.get(0));
    }

    default List<Perspective> namedChildren(String name) {
        return children().stream()
                .filter(child -> name.equals(child.name()))
                .collect(toList());
    }

    default Perspective namedChild(String name) {
        return namedChildren(name).get(0);
    }

    default Perspective child(int index) {
        return children().get(index);
    }

    default Perspective withChildren(Perspective... argChildren) {
        Stream.of(argChildren).forEach(children()::add);
        return this;
    }

    default Perspective withChildren(List<Perspective> argChildren) {
        argChildren.forEach(children()::add);
        return this;
    }

    default Perspective withChildren(Stream<Perspective> argChildren) {
        argChildren.forEach(children()::add);
        return this;
    }

    default Perspective withChild(Perspective arg) {
        children().add(arg);
        return this;
    }

    @Deprecated
    default Perspective withValue(Perspective arg) {
        children().add(arg);
        return this;
    }

    @JavaLegacyBody
    @Override
    default org.w3c.dom.Node toDom() {
        final org.w3c.dom.Node dom;
        // HACK Use generic rendering specifics based on argument.
        if (STRING.equals(nameSpace()) && children().isEmpty() ||
                TEXT.equals(nameSpace()) && children().isEmpty()) {
            dom = Xml.textNode(name());
        } else {
            if (name().contains(" ")) {
                final var element = Xml.rElement(nameSpace(), "val");
                element.setAttributeNode(Xml.attribute("name", name()));
                dom = element;
            } else {
                dom = Xml.rElement(nameSpace(), name());
            }
        }
        children().forEach(child -> dom.appendChild(child.toDom()));
        return dom;
    }

    @ReturnsThis
    default Perspective withPath(Perspective path, String propertyName, NameSpace nameSpace) {
        return withPath(this, path, propertyName, nameSpace);
    }

    default Perspective withPath(Perspective... path) {
        var current = this;
        for (int i = 0; i < path.length; ++i) {
            final var next = path[i];
            current.withChild(next);
            current = next;
        }
        return this;
    }

    private static Perspective withPath(Perspective current, Perspective path, String propertyName, NameSpace nameSpace) {
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
        final Perspective child;
        if (propertyHosters.isEmpty()) {
            // HACK Use generic rendering specifics based on argument.
            child = perspective(NameSpaces.VAL, NATURAL)
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
        String htmlString = "";
        if (nameSpace().equals(HTML)) {
            if (children().isEmpty()) {
                htmlString += "<" + name() + "/>";
            } else {
                htmlString += "<" + name() + ">";
                htmlString += children().stream().map(Perspective::toHtmlString).reduce((a, b) -> a + b).orElse("");
                htmlString += "</" + name() + ">";
            }
        } else if (nameSpace().equals(STRING)) {
            htmlString += name();
            htmlString += children().stream().map(Perspective::toHtmlString).reduce((a, b) -> a + b).orElse("");
        } else if (nameSpace().equals(NATURAL) || nameSpace().equals(DEN)) {
            if (children().isEmpty()) {
                htmlString += "<" + name() + "/>";
            } else {
                htmlString += "<" + name() + ">";
                htmlString += children().stream().map(Perspective::toHtmlString).reduce((a, b) -> a + b).orElse("");
                htmlString += "</" + name() + ">";
            }
        } else {
            throw executionException("Unsupported namespace `" + nameSpace().uri() + "` for value `" + name() + "`.");
        }
        return htmlString;
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
     */
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

    default String toXmlStringWithPrefixes() {
        String xmlString = "";
        if (name().isBlank()) {
            return "<empty/>";
        } else if (!_VALID_XML_NAME.matcher(name()).matches()) {
            if (children().isEmpty()) {
                xmlString += xmlName();
            } else {
                xmlString += "<d:val name=\"" + xmlName() + "\">";
                xmlString += children().stream().map(Perspective::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
                xmlString += "</d:val>";
            }
        } else if (nameSpace().equals(HTML)) {
            if (children().isEmpty()) {
                xmlString += "<x:" + name() + "/>";
            } else {
                xmlString += "<x:" + name() + ">";
                xmlString += children().stream().map(Perspective::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
                xmlString += "</x:" + name() + ">";
            }
        } else if (nameSpace().equals(STRING)) {
            xmlString += xmlName();
            xmlString += children().stream().map(Perspective::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
        } else if (nameSpace().equals(NATURAL)) {
            if (children().isEmpty()) {
                xmlString += "<n:" + name() + "/>";
            } else {
                xmlString += "<n:" + name() + ">";
                xmlString += children().stream().map(Perspective::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
                xmlString += "</n:" + name() + ">";
            }
        } else if (nameSpace().equals(DEN)) {
            if (children().isEmpty()) {
                xmlString += "<d:" + name() + "/>";
            } else {
                xmlString += "<d:" + name() + ">";
                xmlString += children().stream().map(Perspective::toXmlStringWithPrefixes).reduce((a, b) -> a + b).orElse("");
                xmlString += "</d:" + name() + ">";
            }
        } else {
            throw executionException("No prefix known for given perspective: " + nameSpace().uri());
        }
        return xmlString;
    }

    @ReturnsThis
    default Perspective extendWith(List<String> path) {
        if (path.isEmpty()) {
            return this;
        }
        final var next = path.remove(0);
        final var match = children().stream()
                .filter(child -> child.name().equals(next))
                .findFirst();
        if (match.isEmpty()) {
            final var nextPerspective = perspective(next);
            this.withChild(nextPerspective);
            nextPerspective.extendWith(path);
        } else {
            match.get().extendWith(path);
        }
        return this;
    }

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

    default String toJsonString() {
        return toJsonString(jsonConfig());
    }

    private static String encodeJsonString(String arg) {
        return arg.replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Creates a JSON, where all primitive values are Strings.
     *
     * @return This is a JSON, representing the contents of this, whereby the {@link #nameSpace()} is ignored.
     */
    default String toJsonString(JsonConfig config) {
        final StringBuilder jsonString = new StringBuilder();
        if (children().isEmpty()) {
            jsonString.append("{\"" + encodeJsonString(name()) + "\":\"\"}");
        } else {
            boolean isNotFirstChild = false;
            final var hasAnyPrimitiveValues = children().stream().anyMatch(c -> c.children().isEmpty()
                    || c.children().get(0).name().isEmpty());
            final var isThisANamedDictionary = !hasAnyPrimitiveValues && !name().isEmpty();
            if (hasAnyPrimitiveValues) {
                jsonString.append("[");
            } else {
                if (name().isEmpty()) {
                    requireNot(isThisANamedDictionary);
                    jsonString.append("{");
                } else {
                    require(isThisANamedDictionary);
                    if (config.isTopElement()) {
                        jsonString.append("{\"" + encodeJsonString(name()) + "\":{");
                    } else {
                        jsonString.append("\"" + encodeJsonString(name()) + "\":{");
                    }
                }
            }
            for (final var child : children()) {
                if (isNotFirstChild) {
                    jsonString.append(",");
                }
                if (child.children().size() == 1) {
                    if (child.children().get(0).children().size() == 0) {
                        jsonString.append("\"" + encodeJsonString(child.name()) + "\":\"" + encodeJsonString(child.children().get(0).name()) + "\"");
                    } else {
                        jsonString.append("\"" + encodeJsonString(child.name()) + "\":"
                                + child.toJsonString(jsonConfig().withIsTopElement(false)));
                    }
                } else if (child.children().isEmpty()) {
                    require(hasAnyPrimitiveValues);
                    jsonString.append("\"" + encodeJsonString(child.name()) + "\"");
                } else {
                    jsonString.append(child.toJsonString(jsonConfig().withIsTopElement(false)));
                }
                isNotFirstChild = true;
            }
            if (hasAnyPrimitiveValues) {
                jsonString.append("]");
            } else {
                if (isThisANamedDictionary) {
                    if (config.isTopElement()) {
                        jsonString.append("}}");
                    } else {
                        jsonString.append("}");
                    }
                } else {
                    jsonString.append("}");
                }
            }
        }

        return jsonString.toString();
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
        } else {
            final var isSimpleList = children().stream().map(c -> c.children().isEmpty()).reduce(true, (a, b) -> a && b);
            if (!name().isEmpty()) {
                final var lastNameChar = name().charAt(name().length() - 1);
                if (lastNameChar == '.' || lastNameChar == ':') {
                    output.append(listPrefix + name());
                } else {
                    output.append(listPrefix + name() + ":");
                }
            }
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
    }

    default Perspective subtree(List<String> path) {
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

    /**
     * TODO It would be best to return a copy of {@link Perspective} instead of {@code this}.
     *
     * @return return
     */
    default Perspective toPerspective() {
        return this;
    }

    /**
     * TODO Move this functionality into {@link #pathOfValueTree(PathQueryConfig, List)}.
     *
     * @param stringPath
     * @return
     */
    @Deprecated
    default Optional<List<Perspective>> pathOfDenValueTree(String stringPath) {
        return pathOfDenValueTree(listWithValuesOf(stringPath.split("/")));
    }

    /**
     * TODO Move this functionality into {@link #pathOfValueTree(PathQueryConfig, List)}.
     *
     * @param stringPath
     * @return
     */
    @Deprecated
    default Optional<List<Perspective>> pathOfDenValueTree(List<String> stringPath) {
        final List<Perspective> path = listWithValuesOf();
        Perspective currentNode = this;
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

    default Optional<List<Perspective>> pathOfValueTree(String... stringPath) {
        return pathOfValueTree(PathQueryConfig.pathQueryConfig(), listWithValuesOf(stringPath));
    }

    default Optional<List<Perspective>> pathOfValueTree(PathQueryConfig config, String... stringPath) {
        return pathOfValueTree(config, listWithValuesOf(stringPath));
    }

    /**
     * The {@link NameSpace} of the {@link Perspective} is ignored in this method.
     *
     * @param stringPath List of {@link Perspective#name()} describing a path starting with {@code this}.
     * @return The list of {@link Perspective} corresponding to a path
     */
    default Optional<List<Perspective>> pathOfValueTree(PathQueryConfig config, List<String> stringPath) {
        final List<Perspective> path = listWithValuesOf();
        Perspective currentNode = this;
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
