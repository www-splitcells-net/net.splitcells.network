/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.namespace;

import net.splitcells.dem.environment.config.framework.Option;

import java.util.Optional;

public class NameSpace {

    public static NameSpace nameSpace(String prefix, String uri) {
        return new NameSpace(prefix, uri, Optional.empty());
    }

    public static NameSpace nameSpace(String prefix, String uri, Optional<Boolean> isXmlAttributeArg) {
        return new NameSpace(prefix, uri, isXmlAttributeArg);
    }

    private final String uri;
    private final String prefix;
    /**
     * TODO Maybe something like a generic tag system for perspectives would be a better fit.
     */
    private Optional<Boolean> isXmlAttribute;

    private NameSpace(String prefix, String uri, Optional<Boolean> isXmlAttributeArg) {
        this.uri = uri;
        this.prefix = prefix;
        isXmlAttribute = isXmlAttributeArg;
    }

    public String uri() {
        return uri;
    }

    public String defaultPrefix() {
        return prefix;
    }

    public String prefixedName(String name) {
        return prefix + ":" + name;
    }

    public Optional<Boolean> isXmlAttribute() {
        return isXmlAttribute;
    }

    @Override
    public String toString() {
        return uri;
    }
}
